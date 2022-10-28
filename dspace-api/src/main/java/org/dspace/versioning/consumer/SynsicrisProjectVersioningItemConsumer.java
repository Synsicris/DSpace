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
import static org.dspace.project.util.ProjectConstants.MD_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

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

        Boolean isVersionVisible =
            Boolean.valueOf(this.itemService.getMetadataFirstValue(item, MD_VERSION_VISIBLE, null));
        String uniqueId = this.itemService.getMetadataFirstValue(item, MD_UNIQUE_ID, null);
        String[] versionedId = Optional.ofNullable(uniqueId).map(value -> value.split("_")).orElse(null);
        if (versionedId == null || versionedId.length < 2 || StringUtils.isEmpty(versionedId[1])) {
            return;
        }
        consumeProjectItem(ctx, item, isVersionVisible, versionedId[1]);
    }

    @Override
    public void end(Context ctx) throws Exception {
    }

    @Override
    public void finish(Context ctx) throws Exception {}

    private void consumeProjectItem(Context ctx, Item projectItem, Boolean isVersionVisible, String version)
        throws SQLException, AuthorizeException {
        Community community =
            this.projectConsumerService.getProjectCommunity(ctx, projectItem);
        Iterator<Item> projectItems =
            this.projectConsumerService.findVersionedItemsOfProject(ctx, community, projectItem, version);
        if (!projectItems.hasNext()) {
            return;
        }
        Item actual;
        Group fundersGroup =
            Optional.ofNullable(this.getFunderPolicyGroup(ctx, community))
                .orElseThrow(
                    () -> new RuntimeException(
                        "Cannot find the funders policy group for community: " + community.getID()
                    )
                );
        Group readersGroup =
            Optional.ofNullable(this.getReaderPolicyGroup(ctx, community))
                .orElseThrow(
                    () -> new RuntimeException(
                        "Cannot find the readers policy group for community: " + community.getID()
                    )
                );
        while (projectItems.hasNext() && (actual = projectItems.next()) != null) {
            // makes visible versioned project and all its items to the funder role of the project-community
            if (!actual.getID().equals(projectItem.getID())) {
                setVersionVisibility(ctx, actual, isVersionVisible);
            }
            if (isVersionVisible) {
                clearMetadataPolicies(ctx, actual);
                addReadPolicy(ctx, actual, fundersGroup);
                addGroupsPolicy(ctx, actual, fundersGroup, readersGroup);
            // hides versioned project and all its items to the funder role of the project-community
            } else {
                addMetadataPolicies(ctx, community, actual);
                removeReadPolicy(ctx, actual, fundersGroup);
                removeGroupsPolicy(ctx, actual, fundersGroup, readersGroup);
            }
        }
    }

    private void setVersionVisibility(Context ctx, Item item, boolean isVersionVisible) throws SQLException {
        this.itemService
            .setMetadataSingleValue(ctx, item, MD_VERSION_VISIBLE, null, Boolean.toString(isVersionVisible));
    }

    private void addGroupsPolicy(Context ctx, Item actual, Group... groups) throws SQLException {
        for (int i = 0; i < groups.length; i++) {
            addGroupPolicy(ctx, actual, groups[i]);
        }
    }

    private void addGroupPolicy(Context ctx, Item actual, Group fundersGroup) throws SQLException {
        this.itemService.addMetadata(
            ctx, actual, ProjectConstants.MD_POLICY_GROUP.schema, ProjectConstants.MD_POLICY_GROUP.element,
            ProjectConstants.MD_POLICY_GROUP.qualifier, null, fundersGroup.getName(),
            fundersGroup.getID().toString(),
            Choices.CF_ACCEPTED
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

    private void removeGroupsPolicy(Context ctx, Item actual, Group... groups) throws SQLException {
        Map<String, MetadataValue> policyGroupMetadatas =
            this.itemService.getMetadata(
                actual, ProjectConstants.MD_POLICY_GROUP.schema, ProjectConstants.MD_POLICY_GROUP.element,
                ProjectConstants.MD_POLICY_GROUP.qualifier, null
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

    private void clearMetadataPolicies(Context ctx, Item actual) throws SQLException {
        this.itemService.clearMetadata(
            ctx, actual, ProjectConstants.MD_FUNDER_POLICY_GROUP.schema,
            ProjectConstants.MD_FUNDER_POLICY_GROUP.element, ProjectConstants.MD_FUNDER_POLICY_GROUP.qualifier, null
        );
        this.itemService.clearMetadata(
            ctx, actual, ProjectConstants.MD_READER_POLICY_GROUP.schema,
            ProjectConstants.MD_READER_POLICY_GROUP.element, ProjectConstants.MD_READER_POLICY_GROUP.qualifier, null
        );
        this.itemService.clearMetadata(
            ctx, actual, ProjectConstants.MD_MEMBER_POLICY_GROUP.schema,
            ProjectConstants.MD_MEMBER_POLICY_GROUP.element, ProjectConstants.MD_MEMBER_POLICY_GROUP.qualifier, null
        );
        this.itemService.clearMetadata(
            ctx, actual, ProjectConstants.MD_COORDINATOR_POLICY_GROUP.schema,
            ProjectConstants.MD_COORDINATOR_POLICY_GROUP.element,
            ProjectConstants.MD_COORDINATOR_POLICY_GROUP.qualifier, null
        );
    }

    private void addMetadataPolicies(Context ctx, Community community, Item actual) throws SQLException {
        MetadataValueVO funders =
            this.projectGeneratorService.getProjectCommunityMetadata(ctx, community, FUNDERS_ROLE);
        this.itemService.addMetadata(
            ctx, actual, ProjectConstants.MD_FUNDER_POLICY_GROUP.schema,
            ProjectConstants.MD_FUNDER_POLICY_GROUP.element, ProjectConstants.MD_FUNDER_POLICY_GROUP.qualifier, null,
            funders.getValue(), funders.getAuthority(), funders.getConfidence()
        );
        MetadataValueVO readers =
            this.projectGeneratorService.getProjectCommunityMetadata(ctx, community, READERS_ROLE);
        this.itemService.addMetadata(
            ctx, actual, ProjectConstants.MD_READER_POLICY_GROUP.schema,
            ProjectConstants.MD_READER_POLICY_GROUP.element, ProjectConstants.MD_READER_POLICY_GROUP.qualifier, null,
            readers.getValue(), readers.getAuthority(), readers.getConfidence()
        );
        MetadataValueVO members =
            this.projectGeneratorService.getProjectCommunityMetadata(ctx, community, MEMBERS_ROLE);
        this.itemService.addMetadata(
            ctx, actual, ProjectConstants.MD_MEMBER_POLICY_GROUP.schema,
            ProjectConstants.MD_MEMBER_POLICY_GROUP.element, ProjectConstants.MD_MEMBER_POLICY_GROUP.qualifier, null,
            members.getValue(), members.getAuthority(), members.getConfidence()
        );
        MetadataValueVO coordinators =
            this.projectGeneratorService.getProjectCommunityMetadata(ctx, community, COORDINATORS_ROLE);
        this.itemService.addMetadata(
            ctx, actual, ProjectConstants.MD_COORDINATOR_POLICY_GROUP.schema,
            ProjectConstants.MD_COORDINATOR_POLICY_GROUP.element,
            ProjectConstants.MD_COORDINATOR_POLICY_GROUP.qualifier, null,
            coordinators.getValue(), coordinators.getAuthority(), coordinators.getConfidence()
        );
    }

    private Group getFunderPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, FUNDERS_ROLE);
    }

    private Group getReaderPolicyGroup(Context ctx, Community projectCommunity) {
        return this.projectGeneratorService.getProjectCommunityGroup(ctx, projectCommunity, READERS_ROLE);
    }

}
