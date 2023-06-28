/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import static org.dspace.project.util.ProjectConstants.EXTERNAL_READERS_ROLE;
import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;
import static org.dspace.project.util.ProjectConstants.FUNDING_COORDINATORS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_SHARED;
import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;
import static org.dspace.project.util.ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_EXTERNALREADERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.exception.SQLRuntimeException;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultItemIterator;
import org.dspace.discovery.indexobject.IndexableCommunity;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public class ProjectConsumerServiceImpl implements ProjectConsumerService {

    private static final String SOLR_FILTER_LAST_VERSION_VISIBLE = "synsicris.isLastVersion.visible:true";
    private static final String SOLR_FILTER_PREVIOUS_VISIBLE_VERSIONS = "synsicris.version:[1 TO %s]";
    private static final String SOLR_FILTER_VERSION = "synsicris.version:\"%s\"";
    private static final String SOLR_FILTER_PROJECT_UNIQUEID =
        "synsicris.uniqueid:%s AND dspace.entity.type:Project";
    private static final String SOLR_FILTER_PROJECTS =
        "synsicris.uniqueid:* AND dspace.entity.type:Project";
    private static final String SOLR_FILTER_VERSION_PROJECT =
        "synsicris.version:\"%s\" AND -(dspace.entity.type:Project OR search.resourceid:%s)";
    private static final String SOLR_FILTER_FUNDING_RELATION =
        "synsicris.relation.funding_authority:\"%s\" AND " +
        "!entityType:comment AND " +
        "-(relation.isVersionOf:* OR synsicris.uniqueid:*)";
    private static final Logger log = LogManager.getLogger(ProjectConsumerServiceImpl.class);

    @Autowired
    private ItemService itemService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private WorkspaceItemService workspaceItemService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private CollectionService collectionService;

    @Override
    public void processItem(Context context, EPerson currentUser, Item item) {
        if (StringUtils.isBlank(itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY))) {
            return;
        }
        this.setPolicy(context, currentUser, item, getSharedPolicyValue(item));
    }

    @Override
    public String getSharedPolicyValue(Item item) {
        return itemService.getMetadataFirstValue(
            item,
            MD_POLICY_SHARED.schema,
            MD_POLICY_SHARED.element,
            MD_POLICY_SHARED.qualifier,
            Item.ANY
        );
    }

    @Override
    public void setPolicy(Context context, EPerson currentUser, Item item, String policy) {
        try {
            if (StringUtils.isBlank(itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY))) {
                return;
            }
            Community projectCommunity;
            if (isProjectItem(item)) {
                projectCommunity = getProjectCommunity(context, item);
            } else {
                projectCommunity = getProjectCommunityByRelationProject(context, item);
            }

            if (Objects.isNull(projectCommunity) || StringUtils.isBlank(policy)) {
                return;
            }
            switch (policy) {
                case ProjectConstants.PROJECT :
                case ProjectConstants.OWNING_PROJECT :
                    setPolicyRestrictionToProject(context, currentUser, item, projectCommunity);
                    break;
                case ProjectConstants.FUNDING:
                    setPolicyRestrictionToFunding(context, currentUser, item, projectCommunity);
                    break;
                case ProjectConstants.FUNDER:
                    setPolicyRestrictionToFunder(context, item);
                    break;
                default:
                    return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot change policy", e);
        }
    }

    protected void setPolicyRestrictionToFunder(Context context, Item item) throws SQLException {
        setPolicyGroup(context, item, configurationService.getProperty("project.funder.group"));
    }

    protected void setPolicyRestrictionToFunding(
        Context context, EPerson currentUser, Item item, Community projectCommunity
    )
        throws SQLException {
        Community project = getFundingCommunity(projectCommunity);
        if (Objects.isNull(project)) {
            throw new RuntimeException("It was not possible to find the funding Community");
        }
        List<Community> subCommunities = project.getSubcommunities();
        for (Community community : subCommunities) {
            if (setPolicyGroup(context, item, currentUser, community, true)) {
                return;
            }
        }
    }

    protected void setPolicyRestrictionToProject(
        Context context, EPerson currentUser, Item item, Community projectCommunity
    )
        throws SQLException {
        if (!setPolicyGroup(context, item, currentUser, projectCommunity, false)) {
            log.error(
                "something went wrong, the item:" + item.getID().toString()
                    + " could not register the policy 'cris.policy.group'."
            );
        }
    }

    private void setPolicyGroup(Context context, Item item, String groupUuid) throws SQLException {
        Group group = groupService.find(context, UUID.fromString(groupUuid));
        if (Objects.nonNull(group)) {
            itemService.replaceMetadata(context, item, "cris", "policy", "group", null, group.getName(),
                        groupUuid, Choices.CF_ACCEPTED, 0);
        } else {
            log.error("It was not possible to find the group with uuid : " + groupUuid);
        }
    }

    @Override
    public Community getFirstOwningCommunity(Context context, Item item) throws SQLException {
        Community parentCommunity = null;
        Collection owningCollection = null;

        WorkspaceItem workspaceItem = workspaceItemService.findByItem(context, item);
        if (Objects.nonNull(workspaceItem)) {
            owningCollection = workspaceItem.getCollection();
        } else {
            if (item.getCollections().isEmpty() || Objects.isNull(item.getCollections())) {
                // the item is a template item
                return null;
            }
            owningCollection = item.getOwningCollection();
        }
        String[] commToSkip = configurationService.getArrayProperty("project.community-name.to-skip", new String[] {});
        parentCommunity = owningCollection.getCommunities().get(0);
        while (Arrays.stream(commToSkip).anyMatch(parentCommunity.getName()::equals)) {
            parentCommunity = parentCommunity.getParentCommunities().get(0);
        }
        return parentCommunity;
    }

    @Override
    public Community getProjectCommunity(Context context, Item item) throws SQLException {
        Community owningCommunity = getFirstOwningCommunity(context, item);
        if (Objects.isNull(owningCommunity)) {
            return null;
        }

        Optional<Community> parentCommunity =
            Optional.ofNullable(owningCommunity.getParentCommunities())
                .filter(CollectionUtils::isNotEmpty)
                .map(list -> list.get(0));

        if (parentCommunity.isEmpty()) {
            return null;
        }

        String parentCommId = configurationService.getProperty("project.parent-community-id", null);

        if (parentCommunity.get().getID().toString().equals(parentCommId)) {
            return owningCommunity;
        } else {
            return parentCommunity
                    .map(Community::getParentCommunities)
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .orElse(null);
        }
    }

    @Override
    public Group getFundingCommunityGroupByRole(Context context, Community fundingCommunity, String role)
            throws SQLException {
        if (Objects.isNull(fundingCommunity)) {
            return null;
        }
        String template;
        switch (role) {
            case MEMBERS_ROLE:
                template = FUNDING_MEMBERS_GROUP_TEMPLATE;
                break;
            default:
                template = FUNDING_COORDINATORS_GROUP_TEMPLATE;
                break;
        }
        String groupName = String.format(template, fundingCommunity.getID().toString());
        return groupService.findByName(context, groupName);
    }

    @Override
    public String getOwningFundigPolicy(Context context, Item item) throws SQLException {
        Community parentComm = getFirstOwningCommunity(context, item);
        Item fundingItem = getRelationItemByCommunity(context, parentComm);

        return getDefaultSharedValueByItemProject(context, fundingItem);
    }

    @Override
    public Group getProjectCommunityGroupByRole(Context context, Community projectCommunity, String role)
            throws SQLException {
        if (Objects.isNull(projectCommunity)) {
            return null;
        }
        String template;
        switch (role) {
            case MEMBERS_ROLE:
                template = PROJECT_MEMBERS_GROUP_TEMPLATE;
                break;
            case FUNDERS_ROLE:
                template = PROJECT_FUNDERS_GROUP_TEMPLATE;
                break;
            case EXTERNAL_READERS_ROLE:
                template = PROJECT_EXTERNALREADERS_GROUP_TEMPLATE;
                break;
            case READERS_ROLE:
                template = PROJECT_READERS_GROUP_TEMPLATE;
                break;
            default:
                template = PROJECT_COORDINATORS_GROUP_TEMPLATE;
                break;
        }
        String groupName = String.format(template, projectCommunity.getID().toString());
        return groupService.findByName(context, groupName);
    }

    @Override
    public Iterator<Item> findRelatedFundingItems(
        Context context, Community fundingCommunity, Item funding
    ) {
        return findRelatedItemsByFunding(context, fundingCommunity, funding);
    }

    @Override
    public Iterator<Item> findPreviousVisibleVersionsInCommunity(
        Context context, Community projectCommunity, String versionNumber
    ) {
        return findPreviousVisibleVersionsByCommunity(context, projectCommunity, versionNumber);
    }

    @Override
    public Iterator<Item> findLastVersionVisibleInCommunity(
        Context context, Community projectCommunity
    ) {
        return findLastVersionVisibleByCommunity(context, projectCommunity);
    }

    @Override
    public Iterator<Item> findVersionedProjectItemsBy(Context context, UUID projectId) {
        return findProjectItemsBy(context, projectId);
    }

    @Override
    public Iterator<Item> findVersionedItemsOfProject(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        try {

            projectCommunity =
                Optional.ofNullable(projectCommunity)
                    .orElse(getProjectCommunity(context, projectItem));
            if (projectCommunity == null) {
                return IteratorUtils.emptyIterator();
            }

            return findItemsByCommunity(context, projectCommunity, projectItem, version);

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public Iterator<Item> findVersionedItemsRelatedToProject(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        try {

            projectCommunity =
                Optional.ofNullable(projectCommunity)
                .orElse(getProjectCommunity(context, projectItem));
            if (projectCommunity == null) {
                return IteratorUtils.emptyIterator();
            }

            return findNotProjectItemsByCommunity(context, projectCommunity, projectItem, version);

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private Iterator<Item> findRelatedItemsByFunding(
        Context context, Community fundingCommunity, Item funding
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(fundingCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(String.format(SOLR_FILTER_FUNDING_RELATION, funding.getID().toString()));
        return new DiscoverResultItemIterator(context, new IndexableCommunity(fundingCommunity), discoverQuery);
    }

    private Iterator<Item> findPreviousVisibleVersionsByCommunity(
        Context context, Community projectCommunity, String versionNumber
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(String.format(SOLR_FILTER_PREVIOUS_VISIBLE_VERSIONS, versionNumber));
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Iterator<Item> findLastVersionVisibleByCommunity(
        Context context, Community projectCommunity
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(SOLR_FILTER_LAST_VERSION_VISIBLE);
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Iterator<Item> findItemsByCommunity(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(
            String.format(SOLR_FILTER_VERSION, version)
        );
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Iterator<Item> findProjectItemsBy(Context context, UUID itemId) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(
            String.format(SOLR_FILTER_PROJECT_UNIQUEID, itemId.toString())
        );
        return new DiscoverResultItemIterator(context, discoverQuery);
    }

    private Iterator<Item> findNotProjectItemsByCommunity(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(
            String.format(SOLR_FILTER_VERSION_PROJECT, version, projectItem.getID().toString())
        );
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Community getFundingCommunity(Community projectCommunity) {
        String fundingName = configurationService.getProperty("project.funding-community-name");
        return Stream.concat(
                projectCommunity.getSubcommunities().stream(),
                projectCommunity.getParentCommunities().stream()
            )
            .filter(community -> StringUtils.equals(fundingName, community.getName()))
            .findFirst()
            .orElse(null);
    }

    private boolean setPolicyGroup(Context context, Item item, EPerson currentUser, Community community,
            boolean isFunding) throws SQLException {
        String memberGroupName =
            String.format(
                isFunding ? FUNDING_MEMBERS_GROUP_TEMPLATE : PROJECT_MEMBERS_GROUP_TEMPLATE,
                community.getID().toString()
            );
        Group memberGroupOfProjectCommunity = groupService.findByName(context, memberGroupName);
        boolean isAdmin = authorizeService.isAdmin(context);
        boolean isCommunityAdmin = authorizeService.authorizeActionBoolean(context, community, Constants.ADMIN, false);
        boolean isGroupMember = groupService.isMember(context, currentUser, memberGroupOfProjectCommunity);

        if (isAdmin || isGroupMember || isCommunityAdmin) {
            itemService.replaceMetadata(
                context, item, "cris", "policy", "group", null, memberGroupName,
                memberGroupOfProjectCommunity.getID().toString(), Choices.CF_ACCEPTED, 0
            );
            return true;
        }
        return false;
    }

    @Override
    public void setGrantsByFundingPolicy(Context context, Item item) {
        try {
            Community projectCommunity = getProjectCommunity(context, item);
            Community fundingCommunity = getFirstOwningCommunity(context, item);
            if (Objects.isNull(projectCommunity)) {
                return;
            }

            String policyValue = getOwningFundigPolicy(context, item);
            if (StringUtils.isBlank(policyValue)) {
                policyValue = ProjectConstants.PROJECT;
            }

            if (policyValue.equals(ProjectConstants.FUNDING) && Objects.isNull(fundingCommunity)) {
                return;
            }

            itemService.replaceMetadata(
                context, item,
                MD_POLICY_SHARED.schema,
                MD_POLICY_SHARED.element,
                MD_POLICY_SHARED.qualifier,
                null, policyValue, null,
                Choices.CF_UNSET,
                0
            );

            Group group;
            switch (policyValue) {
                case ProjectConstants.PROJECT:
                    group = getProjectCommunityGroupByRole(context, projectCommunity, MEMBERS_ROLE);
                    break;
                case ProjectConstants.FUNDING:
                    group = getFundingCommunityGroupByRole(context, fundingCommunity, MEMBERS_ROLE);
                    break;
                default:
                    throw new IllegalArgumentException("Unable to find policy named : " + policyValue);
            }

            if (Objects.isNull(group)) {
                return;
            }
            itemService.replaceMetadata(context, item, MD_POLICY_GROUP.schema,
                    MD_POLICY_GROUP.element, MD_POLICY_GROUP.qualifier,
                    null, group.getName(), group.getID().toString(), Choices.CF_ACCEPTED, 0);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    public String getDefaultSharedValueByItemProject(Context context, Item projectItem) {
        if (Objects.isNull(projectItem)) {
            return null;
        }
        return itemService.getMetadataFirstValue(projectItem, MD_POLICY_SHARED.schema,
                MD_POLICY_SHARED.element, MD_POLICY_SHARED.qualifier, Item.ANY);
    }

    @Override
    public boolean isMemberOfFunding(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException {

        Community funding  = getFundingCommunityByUser(context, ePerson, projectCommunity);
        return Objects.nonNull(funding);
    }

    @Override
    public Community getFundingCommunityByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException {
        return getFundingsByUser(context, ePerson, projectCommunity).findFirst().orElse(null);
    }

    @Override
    public List<Community> getAllFundingsByUser(Context context, EPerson ePerson, Community projectCommunity) {
        return getFundingsByUser(context, ePerson, projectCommunity).collect(Collectors.toList());
    }

    private Stream<Community> getFundingsByUser(Context context, EPerson ePerson, Community projectCommunity) {
        return Optional.ofNullable(getFundingCommunity(projectCommunity))
            .map(fundingsParentCommunity ->
                fundingsParentCommunity.getSubcommunities()
                    .stream()
                    .filter(community -> isMemberOfFundingMembersGroup(context, ePerson, community))
            )
            .orElse(Stream.of());
    }

    protected boolean isMemberOfFundingMembersGroup(Context context, EPerson ePerson, Community community) {
        String fundingMembersGroup =
            String.format(
                ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE,
                community.getID().toString()
            );
        try {
            return groupService.isMember(
                context, ePerson,
                groupService.findByName(context, fundingMembersGroup)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Cannot retrieve linked funding_members_group.", e);
        }
    }

    @Override
    public Community getProjectCommunityByRelationProject(Context context, Item item) throws SQLException {
        return Optional.ofNullable(
            this.getProjectItemByRelatedItem(context, item)
        )
        .map(projectItem -> {
            try {
                return this.getProjectCommunity(context, projectItem);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })
        .orElse(null);
    }

    @Override
    public Item getProjectItemByRelatedItem(Context context, Item relatedItem) throws SQLException {
        List<MetadataValue> values = itemService.getMetadata(relatedItem, ProjectConstants.MD_PROJECT_RELATION.schema,
            ProjectConstants.MD_PROJECT_RELATION.element,
            ProjectConstants.MD_PROJECT_RELATION.qualifier, null);
        if (values.isEmpty()) {
            return null;
        }
        String uuid =
            Optional.ofNullable(values.get(0))
                .map(MetadataValue::getAuthority)
                .orElse(null);
        Item projectItem = null;
        if (StringUtils.isNotBlank(uuid)) {
            projectItem = itemService.find(context, UUID.fromString(uuid));
        }
        return projectItem;
    }

    @Override
    public Community getFundingCommunityByRelationFunding(Context context, Item item) throws SQLException {
        List<MetadataValue> values = itemService.getMetadata(item, ProjectConstants.MD_FUNDING_RELATION.schema,
                ProjectConstants.MD_FUNDING_RELATION.element,
                ProjectConstants.MD_FUNDING_RELATION.qualifier, null);
        if (values.isEmpty()) {
            return null;
        }
        String uuid = values.get(0).getAuthority();
        if (StringUtils.isNotBlank(uuid)) {
            // item that represent Project community
            Item projectItem = itemService.find(context, UUID.fromString(uuid));
            return getFirstOwningCommunity(context, projectItem);
        }
        return null;
    }

    public Item getRelationItemByCommunity(Context context, Community community) {
        Item projectItem = null;

        List<MetadataValue> values = communityService.getMetadata(community,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.schema, ProjectConstants.MD_RELATION_ITEM_ENTITY.element,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier, null);

        if (values.size() != 1) {
            log.warn("Communitiy {} has {} project items, unable to proceed", community.getID().toString(),
                    values.size());
            return projectItem;
        }

        String itemUUID = values.get(0).getAuthority();
        if (StringUtils.isBlank(itemUUID)) {
            log.warn("Communitiy {} has no project items, unable to proceed", community.getID().toString());
            return projectItem;
        }

        try {
            projectItem = itemService.find(context, UUIDUtils.fromString(itemUUID));
        } catch (SQLException e) {
            return projectItem;
        }

        return projectItem;
    }

    @Override
    public Item getParentProjectItemByCollectionUUID(Context context, UUID collectionUUID) {
        Item projectItem = null;
        List<Community> communities;

        try {
            Collection collection = collectionService.find(context, collectionUUID);
            communities = collection.getCommunities();
        } catch (SQLException e) {
            log.error("Error while trying to extract communities for collection {}: {}", collectionUUID.toString(),
                    e.getMessage());
            return projectItem;
        }

        if (Objects.isNull(communities)) {
            return projectItem;
        }
        if (communities.size() != 1) {
            log.warn("Collection {} has {} communities, unable to proceed", collectionUUID.toString(),
                communities.size());
            return projectItem;
        }

        return getRelationItemByCommunity(context, communities.get(0));
    }

    @Override
    public Item getParentProjectItemByCommunityUUID(Context context, UUID communityUUID) {
        Item projectItem = null;
        Community community = null;

        try {
            community = communityService.find(context, communityUUID);
        } catch (SQLException e) {
            log.error("Error while trying to extract communities for collection {}: {}", communityUUID.toString(),
                    e.getMessage());
            return projectItem;
        }

        if (Objects.isNull(community)) {
            return projectItem;
        }

        return getRelationItemByCommunity(context, community);
    }

    @Override
    public boolean isProjectItem(Item item) {
        return ProjectConstants.PROJECT_ENTITY.equals(itemService.getEntityType(item));
    }

    @Override
    public boolean isFundingItem(Item item) {
        return ProjectConstants.FUNDING_ENTITY.equals(itemService.getEntityType(item));
    }

    @Override
    public Iterator<Item> findVersionedProjectsInCommunity(Context context, Community projectCommunity) {
        return findVersionedProjectsByCommunity(context, projectCommunity);
    }

    private Iterator<Item> findVersionedProjectsByCommunity(Context context, Community projectCommunity) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(SOLR_FILTER_PROJECTS);
        discoverQuery.setSortField("bi_sort_3_sort", DiscoverQuery.SORT_ORDER.desc);
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

}
