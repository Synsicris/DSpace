/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.versioning.consumer;

import static org.dspace.project.util.ProjectConstants.COORDINATORS_ROLE;
import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;
import static org.dspace.project.util.ProjectConstants.MD_COORDINATOR_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_FUNDER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_LAST_VERSION;
import static org.dspace.project.util.ProjectConstants.MD_LAST_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.MD_MEMBER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_READER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_READ_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.MD_V_COORDINATOR_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_V_FUNDER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_V_MEMBER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_V_READER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataFieldName;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.content.template.generator.ProjectGeneratorService;
import org.dspace.content.template.generator.ProjectGeneratorServiceImpl;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;
import org.dspace.versioning.Version;
import org.dspace.versioning.VersionHistory;
import org.dspace.versioning.factory.VersionServiceFactory;
import org.dspace.versioning.service.VersioningService;

/**
 * This class is used to enhance metadatas whenever {@code synsicris.version.visible} metadata
 * of a target project is changed.
 * The behaviour is to make visible the project and all of its item to the {@code organisational_founder_group}
 * members (in read-only mode).
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class SynsicrisProjectVersioningItemConsumer implements Consumer {

    public static final MetadataFieldName MD_UNIQUE_ID = new MetadataFieldName("synsicris", "uniqueid");
    private static final String versionVisibleDot = MD_VERSION_VISIBLE.toString();
    private static final String versionVisibleUndescore = versionVisibleDot.replaceAll("\\.", "\\_");

    private ItemService itemService;
    private ProjectConsumerService projectConsumerService;
    private AuthorizeService authorizeService;
    private ProjectGeneratorService projectGeneratorService;
    private VersioningService versioningService;
    private Map<UUID, ImmutablePair<Item, String>> itemsToProcess = new HashMap<>();

    @Override
    public void initialize() throws Exception {
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
        this.projectConsumerService =
            new DSpace().getServiceManager()
                .getServiceByName(
                    ProjectConsumerServiceImpl.class.getName(),
                    ProjectConsumerServiceImpl.class
                );
        this.projectGeneratorService =
            new DSpace().getServiceManager()
            .getServiceByName(
                ProjectGeneratorServiceImpl.class.getName(),
                ProjectGeneratorServiceImpl.class
            );
        this.versioningService = VersionServiceFactory.getInstance().getVersionService();
    }

    @Override
    public void consume(Context ctx, Event event) throws Exception {
        if (
                event.getSubjectType() != Constants.ITEM ||
                event.getEventType() != Event.MODIFY_METADATA ||
                !StringUtils.containsAny(
                    event.getDetail(),
                    versionVisibleDot,
                    versionVisibleUndescore
                )
        ) {
            return;
        }

        Item item = (Item) event.getSubject(ctx);
        if (item == null || !this.projectConsumerService.isProjectItem(item)) {
            return;
        }

        String uniqueId = this.itemService.getMetadataFirstValue(item, MD_UNIQUE_ID, null);
        String[] versionedId = Optional.ofNullable(uniqueId).map(value -> value.split("_")).orElse(null);
        if (versionedId == null || versionedId.length < 2 || StringUtils.isEmpty(versionedId[1])) {
            return;
        }
        itemsToProcess.put(item.getID(), ImmutablePair.of(item, versionedId[1]));
    }

    @Override
    public void end(Context ctx) throws Exception {
        if (this.itemsToProcess.isEmpty()) {
            return;
        }
        while (!this.itemsToProcess.isEmpty()) {
            Iterator<Entry<UUID, ImmutablePair<Item, String>>> it = this.itemsToProcess.entrySet().iterator();
            Entry<UUID, ImmutablePair<Item, String>> entry = it.next();
            Item item = entry.getValue().getLeft();
            String version = entry.getValue().getRight();
            Boolean isVersionVisible =
                Boolean.valueOf(this.itemService.getMetadataFirstValue(item, MD_VERSION_VISIBLE, null));
            ctx.turnOffAuthorisationSystem();
            this.consumeProjectItem(ctx, item, isVersionVisible, version);
            this.itemService.update(ctx, item);
            ctx.restoreAuthSystemState();
            this.itemsToProcess.remove(entry.getKey());
        }
        ctx.commit();
    }

    @Override
    public void finish(Context ctx) throws Exception {}

    private void consumeProjectItem(Context ctx, Item projectItem, Boolean isVersionVisible, String version)
        throws SQLException, AuthorizeException {
        int versionNumber = Integer.valueOf(version);
        Community community = this.projectConsumerService.getProjectCommunity(ctx, projectItem);
        Group fundersGroup = getFundersGroup(ctx, community);
        Group readersGroup = getReadersGroup(ctx, community);
        Group membersGroup = getMembersGroup(ctx, community);
        Group coordinatorsGroup = getCoordinatorsGroup(ctx, community);
        List<Version> versionsByHistory = getVersionsByHistory(ctx, projectItem);
        boolean isLastVisibleProjectVersion =
            isLastVisibleProjectVersion(projectItem, versionNumber, versionsByHistory);
        if (isLastVisibleProjectVersion) {
            clearLastVersionVisible(ctx, community, version);
        }
        Iterator<Item> projectItems =
            this.projectConsumerService.findVersionedItemsOfProject(ctx, community, projectItem, version);
        if (!projectItems.hasNext()) {
            return;
        }
        consumeRelatedItems(
            ctx, isVersionVisible, community,
            fundersGroup, readersGroup, membersGroup, coordinatorsGroup,
            isLastVisibleProjectVersion, projectItems,
            projectItem
        );
        getProcessablePreviousVersion(
            ctx, versionNumber, versionsByHistory, isVersionVisible, isLastVisibleProjectVersion
        )
            .ifPresent(entry -> this.itemsToProcess.put(entry.getKey(), entry.getValue()));
    }

    private List<Version> getVersionsByHistory(Context ctx, Item projectItem) throws SQLException {
        return Optional.ofNullable(this.versioningService.getVersion(ctx, projectItem))
            .map(Version::getVersionHistory)
            .map(versionHistory -> getVersions(ctx, versionHistory))
            .orElse(List.of());
    }

    private List<Version> getVersions(Context ctx, VersionHistory versionHistory) {
        try {
            return this.versioningService.getVersionsByHistory(ctx, versionHistory);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot find linked version for item", e);
        }
    }

    private Group getMembersGroup(Context ctx, Community community) {
        return Optional.ofNullable(this.getMembersPolicyGroup(ctx, community))
            .orElseThrow(
                () -> new RuntimeException(
                    "Cannot find the readers policy group for community: " + community.getID()
                    )
                );
    }

    private Group getReadersGroup(Context ctx, Community community) {
        return Optional.ofNullable(this.getReaderPolicyGroup(ctx, community))
            .orElseThrow(
                () -> new RuntimeException(
                    "Cannot find the readers policy group for community: " + community.getID()
                )
            );
    }

    private Group getFundersGroup(Context ctx, Community community) {
        return Optional.ofNullable(this.getFunderPolicyGroup(ctx, community))
            .orElseThrow(
                () -> new RuntimeException(
                    "Cannot find the funders policy group for community: " + community.getID()
                )
            );
    }

    private Group getCoordinatorsGroup(Context ctx, Community community) {
        return Optional.ofNullable(this.getCoordinatorsPolicyGroup(ctx, community))
            .orElseThrow(
                () -> new RuntimeException(
                    "Cannot find the coordinators policy group for community: " + community.getID()
                    )
                );
    }

    private Optional<Version> findPreviousVisibleVersion(int versionNumber, List<Version> versionsByHistory) {
        return versionsByHistory
            .stream()
            // first version is excluded (is not a real versioned item)
            .filter(v -> v.getVersionNumber() > 1 && v.getVersionNumber() < versionNumber)
            .reduce((v, acc) -> {
                if (acc == null || v.getVersionNumber() > acc.getVersionNumber()) {
                    return v;
                }
                return acc;
            });
    }

    private boolean isLastVisibleProjectVersion(Item projectItem, int versionNumber, List<Version> versionsByHistory) {
        return Optional.of(this.itemService.getMetadataByMetadataString(projectItem, MD_LAST_VERSION.toString()))
            .filter(list -> !list.isEmpty())
            .map(list -> list.stream().allMatch(value -> Boolean.valueOf(value.getValue())))
            .orElse(false) ||
        Optional.ofNullable(versionsByHistory)
            .map(
                list -> list
                    .stream()
                    .filter(v -> v.getVersionNumber() > versionNumber)
                    .flatMap(
                        v ->
                            this.itemService
                                .getMetadataByMetadataString(v.getItem(), MD_LAST_VERSION_VISIBLE.toString())
                                .stream()
                                .map(MetadataValue::getValue)
                    )
                    .noneMatch(Boolean::valueOf)
            )
            .orElse(false);
    }

    private Optional<Entry<UUID, ImmutablePair<Item, String>>> getProcessablePreviousVersion(
        Context ctx, int versionNumber, List<Version> versionsByHistory, boolean isVersionVisible,
        boolean isLastVisibleProjectVersion
    ) throws SQLException, AuthorizeException {
        Optional<Entry<UUID, ImmutablePair<Item, String>>> toProcess = Optional.empty();
        if (wasLastVisible(isVersionVisible, isLastVisibleProjectVersion)) {
            Optional<Version> version = findPreviousVisibleVersion(versionNumber, versionsByHistory);
            Optional<UUID> uuidToProcess = version.map(Version::getItem).map(Item::getID);
            if (uuidToProcess.isPresent() && !this.itemsToProcess.containsKey(uuidToProcess.get())) {
                Item previousItem = this.itemService.find(ctx, uuidToProcess.get());
                toProcess =
                    Optional.of(
                        Map.entry(
                            uuidToProcess.get(),
                            ImmutablePair.of(previousItem, String.valueOf(version.map(Version::getVersionNumber).get()))
                        )
                    );
            }
        }
        return toProcess;
    }

    private boolean wasLastVisible(boolean isVersionVisible, boolean isLastVisibleProjectVersion) {
        return !isVersionVisible && isLastVisibleProjectVersion;
    }

    private void consumeRelatedItems(Context ctx, Boolean isVersionVisible, Community community,
        Group fundersGroup, Group readersGroup, Group membersGroup, Group coordinatorsGroup,
        boolean isLastVisibleProjectVersion, Iterator<Item> projectItems,
        Item projectItem
    ) throws SQLException, AuthorizeException {
        Item actual;
        Set<UUID> processed = new HashSet<>();
        while (
                projectItems.hasNext() &&
                (actual = projectItems.next()) != null &&
                !processed.contains(actual.getID())
        ) {
            // makes visible versioned project and all its items to the organisational_funder
            if (isVersionVisible) {
                if (isLastVisibleProjectVersion) {
                    addLastVersionVisible(ctx, actual);
                }

                if (!this.projectConsumerService.isProjectItem(actual) && !actual.getID().equals(projectItem.getID())) {
                    addVersionVisible(ctx, actual);
                }

                clearMetadataPolicies(ctx, actual);
                addReadPolicy(ctx, actual, fundersGroup);
                addVersionPolicyGroups(ctx, actual, fundersGroup, readersGroup, membersGroup);
                copyMetadataPolicyToVersionPolicy(ctx, actual,
                        fundersGroup, readersGroup, membersGroup, coordinatorsGroup);
            // hides versioned project and all its items to the funder role of the project-community
            } else {
                if (!actual.getID().equals(projectItem.getID())) {
                    clearVersionVisible(ctx, actual);
                }
                addMetadataPolicies(ctx, community, actual);
                clearMetadataVersionPolicies(ctx, actual);
                removeReadPolicy(ctx, actual, fundersGroup);
                removeVersionPolicyGroup(ctx, actual, fundersGroup, readersGroup, membersGroup);
            }
            this.itemService.update(ctx, actual);
            processed.add(actual.getID());
        }
    }

    private void clearLastVersionVisible(Context ctx, Community community, String versionNumber)
        throws SQLException, AuthorizeException {
        Iterator<Item> previousVisibleVersions =
            this.projectConsumerService.findPreviousVisibleVersionsInCommunity(ctx, community, versionNumber);
        Item actual = null;
        while (previousVisibleVersions.hasNext() && (actual = previousVisibleVersions.next()) != null) {
            clearLastVersionVisible(ctx, actual);
            this.itemService.update(ctx, actual);
        }
        // Commit here to avoid the update event trigger the consumer itself with also previous version project
        // DO NOT MOVE
        ctx.commit();
    }

    private void addLastVersionVisible(Context ctx, Item actual) {
        try {
            this.itemService.setMetadataSingleValue(
                ctx, actual, MD_LAST_VERSION_VISIBLE.schema, MD_LAST_VERSION_VISIBLE.element,
                MD_LAST_VERSION_VISIBLE.qualifier, null, Boolean.TRUE.toString()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addVersionVisible(Context ctx, Item actual) {
        try {
            this.itemService.setMetadataSingleValue(
                ctx, actual, MD_VERSION_VISIBLE.schema, MD_VERSION_VISIBLE.element,
                MD_VERSION_VISIBLE.qualifier, null, Boolean.TRUE.toString()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearLastVersionVisible(Context ctx, Item actual) {
        try {
            this.itemService.setMetadataSingleValue(
                ctx, actual, MD_LAST_VERSION_VISIBLE.schema, MD_LAST_VERSION_VISIBLE.element,
                MD_LAST_VERSION_VISIBLE.qualifier, null, Boolean.FALSE.toString()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearVersionVisible(Context ctx, Item actual) {
        try {
            this.itemService.setMetadataSingleValue(
                ctx, actual, MD_VERSION_VISIBLE.schema, MD_VERSION_VISIBLE.element,
                MD_VERSION_VISIBLE.qualifier, null, Boolean.FALSE.toString()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addVersionPolicyGroups(Context ctx, Item actual, Group... groups) throws SQLException {
        for (int i = 0; i < groups.length; i++) {
            addVersionPolicyGroup(ctx, actual, groups[i]);
        }
    }

    private void addVersionPolicyGroup(Context ctx, Item actual, Group group) throws SQLException {
        List<MetadataValue> groupPolicy =
            this.itemService.getMetadata(actual, MD_VERSION_READ_POLICY_GROUP.toString(), group.getID().toString());
        if (groupPolicy.isEmpty()) {
            addGroupPolicyMetadata(ctx, actual, group, MD_VERSION_READ_POLICY_GROUP);
        }
    }

    private void addGroupPolicyMetadata(Context ctx, Item actual, Group group, MetadataFieldName mfn)
            throws SQLException {
        this.itemService.addMetadata(
            ctx, actual, mfn.schema, mfn.element,mfn.qualifier, null,
            group.getName(), group.getID().toString(), Choices.CF_ACCEPTED
        );
    }

    private void addReadPolicy(Context ctx, Item actual, Group fundersGroup) throws SQLException, AuthorizeException {
        Map<Integer, ResourcePolicy> resourcePoliciesMap = this.authorizeService.getPoliciesForGroup(ctx, fundersGroup)
            .stream()
            .filter(rp ->
                fundersGroup.getID().equals(rp.getGroup().getID()) &&
                actual.getID().equals(rp.getdSpaceObject().getID()) &&
                (rp.getAction() == Constants.READ || rp.getAction() == Constants.DEFAULT_BITSTREAM_READ)
            )
            .collect(Collectors.toMap(rp -> rp.getAction(), rp -> rp));
        if (resourcePoliciesMap.get(Constants.READ) == null) {
            this.authorizeService.addPolicy(ctx, actual, Constants.READ, fundersGroup);
        }
        if (resourcePoliciesMap.get(Constants.DEFAULT_BITSTREAM_READ) == null) {
            this.authorizeService.addPolicy(ctx, actual, Constants.DEFAULT_BITSTREAM_READ, fundersGroup);
        }
    }

    private void removeVersionPolicyGroup(Context ctx, Item actual, Group... groups) throws SQLException {
        Map<String, MetadataValue> policyGroupMetadatas =
            this.itemService.getMetadata(
                actual, MD_VERSION_READ_POLICY_GROUP.schema, MD_VERSION_READ_POLICY_GROUP.element,
                MD_VERSION_READ_POLICY_GROUP.qualifier, null
            )
            .stream()
            .map(meta -> new SimpleEntry<>(meta.getAuthority(), meta))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        List<MetadataValue> removableMetadatas = new ArrayList<>(groups.length);
        for (int i = 0; i < groups.length; i++) {
            removableMetadatas.add(
                policyGroupMetadatas.get(groups[i].getID().toString())
            );
        }
        this.itemService.removeMetadataValues(ctx, actual, removableMetadatas);
    }

    private void removeReadPolicy(Context ctx, Item actual, Group fundersGroup)
        throws SQLException, AuthorizeException {
        this.authorizeService.removeGroupPolicies(ctx, actual, fundersGroup, Constants.DEFAULT_BITSTREAM_READ);
        this.authorizeService.removeGroupPolicies(ctx, actual, fundersGroup, Constants.READ);
    }

    private void copyMetadataPolicyToVersionPolicy(Context ctx, Item item, Group fundersGroup, Group readersGroup,
                                                   Group membersGroup, Group coordinatorsGroup) throws SQLException {
        addGroupPolicyMetadata(ctx, item, fundersGroup, MD_V_FUNDER_POLICY_GROUP);
        addGroupPolicyMetadata(ctx, item, readersGroup, MD_V_READER_POLICY_GROUP);
        addGroupPolicyMetadata(ctx, item, membersGroup, MD_V_MEMBER_POLICY_GROUP);
        addGroupPolicyMetadata(ctx, item, coordinatorsGroup, MD_V_COORDINATOR_POLICY_GROUP);
    }

    private void clearMetadataPolicies(Context context, Item actual) throws SQLException {
        clearMetadata(context, actual, MD_FUNDER_POLICY_GROUP);
        clearMetadata(context, actual, MD_READER_POLICY_GROUP);
        clearMetadata(context, actual, MD_MEMBER_POLICY_GROUP);
        clearMetadata(context, actual, MD_COORDINATOR_POLICY_GROUP);
    }

    private void clearMetadataVersionPolicies(Context context, Item actual) throws SQLException {
        clearMetadata(context, actual, MD_V_FUNDER_POLICY_GROUP);
        clearMetadata(context, actual, MD_V_READER_POLICY_GROUP);
        clearMetadata(context, actual, MD_V_MEMBER_POLICY_GROUP);
        clearMetadata(context, actual, MD_V_COORDINATOR_POLICY_GROUP);
    }

    private void clearMetadata(Context ctx, Item item, MetadataFieldName mfn) throws SQLException {
        this.itemService.clearMetadata(ctx, item, mfn.schema, mfn.element, mfn.qualifier, null);
    }

    private void addMetadataPolicies(Context ctx, Community community, Item actual) throws SQLException {
        MetadataValueVO funders = this.projectGeneratorService.getProjectCommunityMetadata(ctx, community,FUNDERS_ROLE);
        this.itemService.addMetadata(ctx, actual, MD_FUNDER_POLICY_GROUP.schema,
                                                  MD_FUNDER_POLICY_GROUP.element,
                                                  MD_FUNDER_POLICY_GROUP.qualifier, null,
                                                  funders.getValue(), funders.getAuthority(), funders.getConfidence());

        MetadataValueVO readers = this.projectGeneratorService.getProjectCommunityMetadata(ctx, community,READERS_ROLE);
        this.itemService.addMetadata(ctx, actual, MD_READER_POLICY_GROUP.schema,
                                                  MD_READER_POLICY_GROUP.element,
                                                  MD_READER_POLICY_GROUP.qualifier, null,
                                                  readers.getValue(), readers.getAuthority(), readers.getConfidence());

        MetadataValueVO members = this.projectGeneratorService.getProjectCommunityMetadata(ctx, community,MEMBERS_ROLE);
        this.itemService.addMetadata(ctx, actual, MD_MEMBER_POLICY_GROUP.schema,
                                                  MD_MEMBER_POLICY_GROUP.element,
                                                  MD_MEMBER_POLICY_GROUP.qualifier, null,
                                                  members.getValue(), members.getAuthority(), members.getConfidence());

        MetadataValueVO coordinators =
                            this.projectGeneratorService.getProjectCommunityMetadata(ctx, community, COORDINATORS_ROLE);
        this.itemService.addMetadata(ctx, actual, MD_COORDINATOR_POLICY_GROUP.schema,
                                                  MD_COORDINATOR_POLICY_GROUP.element,
                                                  MD_COORDINATOR_POLICY_GROUP.qualifier, null,
                                    coordinators.getValue(), coordinators.getAuthority(), coordinators.getConfidence());
    }

    private Group getFunderPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, FUNDERS_ROLE);
    }

    private Group getReaderPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, READERS_ROLE);
    }

    private Group getMembersPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, MEMBERS_ROLE);
    }

    private Group getCoordinatorsPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, COORDINATORS_ROLE);
    }

}
