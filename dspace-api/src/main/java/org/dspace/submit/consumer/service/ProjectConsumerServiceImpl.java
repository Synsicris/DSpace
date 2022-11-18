/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import static org.dspace.project.util.ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    private static final String SOLR_FILTER_UNIQUEID = "synsicris.uniqueid:\"*\\_%s$\"";
    private static final String SOLR_FILTER_UNIQUEID_PROJECT =
        "synsicris.uniqueid:\"*\\_%s$\" AND -(dspace.entity.type:Project OR search.resourceid:%s)";
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
        try {
            if (StringUtils.isNotBlank(itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY))) {
                String shared = itemService.getMetadataFirstValue(item, "cris", "project", "shared", Item.ANY);
                String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
                Community projectCommunity;
                if (entityType != null && entityType.equals(ProjectConstants.PROJECT_ENTITY)) {
                    projectCommunity = getProjectCommunity(context, item);
                } else {
                    projectCommunity = getProjectCommunityByRelationProject(context, item);
                }

                if (Objects.isNull(projectCommunity) || StringUtils.isBlank(shared)) {
                    return;
                }
                switch (shared) {
                    case ProjectConstants.PROJECT :
                    case ProjectConstants.OWNING_PROJECT :
                        if (!setPolicyGroup(context, item, currentUser, projectCommunity, false)) {
                            log.error("something went wrong, the item:" + item.getID().toString()
                                    + " could not register the policy 'cris.policy.group'.");
                        }
                        break;
                    case ProjectConstants.FUNDING:
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
                        break;
                    case ProjectConstants.FUNDER:
                        setPolicyGroup(context, item, configurationService.getProperty("project.funder.group"));
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public Community getProjectCommunity(Context context, Item item) throws SQLException {
        Community projectCommunity = null;
        Collection owningCollection = null;
        String[] commToSkip = configurationService.getArrayProperty("project.community-name.to-skip", new String[] {});

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

        if (owningCollection == null) {
            // the item is a template item
            return null;
        }

        projectCommunity = owningCollection.getCommunities().get(0);
        while (Arrays.stream(commToSkip).anyMatch(projectCommunity.getName()::equals)) {
            projectCommunity = projectCommunity.getParentCommunities().get(0);
        }
        return projectCommunity;
    }

    @Override
    public Group getFundingCommunityGroupByRole(Context context, Community fundingCommunity, String role)
            throws SQLException {
        if (Objects.isNull(fundingCommunity)) {
            return null;
        }
        String template;
        switch (role) {
            case ProjectConstants.MEMBERS_ROLE:
                template = FUNDING_MEMBERS_GROUP_TEMPLATE;;
                break;
            default:
                template = ProjectConstants.FUNDING_COORDINATORS_GROUP_TEMPLATE;
                break;
        }
        String groupName = String.format(template, fundingCommunity.getID().toString());
        return groupService.findByName(context, groupName);
    }

    @Override
    public Group getProjectCommunityGroupByRole(Context context, Community projectCommunity, String role)
            throws SQLException {
        if (Objects.isNull(projectCommunity)) {
            return null;
        }
        String template;
        switch (role) {
            case ProjectConstants.MEMBERS_ROLE:
                template = PROJECT_MEMBERS_GROUP_TEMPLATE;
                break;
            case ProjectConstants.FUNDERS_ROLE:
                template = ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
                break;
            case ProjectConstants.READERS_ROLE:
                template = ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
                break;
            default:
                template = ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE;
                break;
        }
        String groupName = String.format(template, projectCommunity.getID().toString());
        return groupService.findByName(context, groupName);
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

    private Iterator<Item> findItemsByCommunity(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(
            String.format(SOLR_FILTER_UNIQUEID, version)
        );
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Iterator<Item> findNotProjectItemsByCommunity(
        Context context, Community projectCommunity, Item projectItem, String version
    ) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        discoverQuery.setMaxResults(10000);
        discoverQuery.setQuery(
            String.format(SOLR_FILTER_UNIQUEID_PROJECT, version, projectItem.getID().toString())
        );
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private Community getFundingCommunity(Community projectCommunity) {
        String fundingName =  configurationService.getProperty("project.funding-community-name");
        List<Community> subCommunities = new ArrayList<>();
        subCommunities.addAll(projectCommunity.getSubcommunities());
        subCommunities.addAll(projectCommunity.getParentCommunities());
        for (Community community : subCommunities) {
            if (StringUtils.equals(fundingName, community.getName())) {
                return community;
            }
        }
        return null;
    }

    private boolean setPolicyGroup(Context context, Item item, EPerson currentUser, Community community,
            boolean isFunding) throws SQLException {
        String memberGroupName =
            String.format(
                isFunding ? FUNDING_MEMBERS_GROUP_TEMPLATE : PROJECT_MEMBERS_GROUP_TEMPLATE,
                community.getID().toString()
            );
        Group memberGrouoOfProjectCommunity = groupService.findByName(context, memberGroupName);
        boolean isAdmin = authorizeService.isAdmin(context);
        boolean isCommunityAdmin = authorizeService.authorizeActionBoolean(context, community, Constants.ADMIN, false);
        boolean isGroupMember = groupService.isMember(context, currentUser, memberGrouoOfProjectCommunity);

        if (isAdmin || isGroupMember || isCommunityAdmin) {
            itemService.replaceMetadata(
                context, item, "cris", "policy", "group", null, memberGroupName,
                memberGrouoOfProjectCommunity.getID().toString(), Choices.CF_ACCEPTED, 0
            );
            return true;
        }
        return false;
    }

    @Override
    public void checkGrants(Context context, EPerson currentUser, Item item) {
        try {
            Community projectCommunity = getProjectCommunity(context, item);
            if (Objects.isNull(projectCommunity)) {
                return;
            }
            Community fundingCommunity = getFundingCommunityByUser(context, currentUser, projectCommunity);
            if (Objects.nonNull(fundingCommunity)) {
                List<MetadataValue> values = communityService.getMetadata(fundingCommunity,
                        ProjectConstants.MD_RELATION_ITEM_ENTITY.schema,
                        ProjectConstants.MD_RELATION_ITEM_ENTITY.element,
                        ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier, null);
                if (CollectionUtils.isNotEmpty(values)) {
                    String defaultValue = getDefaultSharedValueByItemProject(context, values);
                    if (StringUtils.isNoneEmpty(defaultValue)) {
                        itemService.replaceMetadata(context, item, "cris", "project", "shared",
                                                       null, defaultValue, null, Choices.CF_UNSET, 0);
                    }
                }
            } else {
                itemService.replaceMetadata(context, item, "cris", "project", "shared",
                                            null, ProjectConstants.PROJECT, null, Choices.CF_UNSET, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private String getDefaultSharedValueByItemProject(Context context, List<MetadataValue> values) throws SQLException {
        UUID uuidProjectItem = UUID.fromString(values.get(0).getAuthority());
        if (Objects.nonNull(uuidProjectItem)) {
            Item projectItem = itemService.find(context, uuidProjectItem);
            if (Objects.isNull(projectItem)) {
                return null;
            }
            return itemService.getMetadataFirstValue(projectItem, "cris", "project", "shared", Item.ANY);
        }
        return null;
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

        List<Community> fundings = getAllFundingsByUser(context, ePerson, projectCommunity);
        // user MUST be member of at least one funding within a project
        if (fundings.size() > 0) {
            return fundings.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Community> getAllFundingsByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException {
        Community fundingsParentCommunity = getFundingCommunity(projectCommunity);
        List<Community> fundings = new ArrayList<Community>();

        if (Objects.isNull(fundingsParentCommunity)) {
            return fundings;
        }

        List<Community> subCommunities = fundingsParentCommunity.getSubcommunities();
        for (Community community : subCommunities) {
            StringBuilder memberGroupName = new StringBuilder("funding_")
                                                      .append(community.getID().toString())
                                                      .append("_members_group");
            Group group = groupService.findByName(context, memberGroupName.toString());
            boolean isGroupMember = groupService.isMember(context, ePerson, group);
            if (isGroupMember) {
                fundings.add(community);
            }
        }
        return fundings;
    }

    @Override
    public Community getProjectCommunityByRelationProject(Context context, Item item) throws SQLException {
        List<MetadataValue> values = itemService.getMetadata(item, ProjectConstants.MD_PROJECT_RELATION.schema,
                ProjectConstants.MD_PROJECT_RELATION.element,
                ProjectConstants.MD_PROJECT_RELATION.qualifier, null);
        if (values.isEmpty()) {
            return null;
        }
        String uuid = values.get(0).getAuthority();
        if (StringUtils.isNotBlank(uuid)) {
            // item that represent Project community
            Item projectItem = itemService.find(context, UUID.fromString(uuid));
            return getProjectCommunity(context, projectItem);
        }
        return null;
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
            return getProjectCommunity(context, projectItem);
        }
        return null;
    }

    @Override
    public Item getParentProjectItemByCollectionUUID(Context context, UUID collectionUUID) throws SQLException {
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

        List<MetadataValue> values = communityService.getMetadata(communities.get(0),
                ProjectConstants.MD_RELATION_ITEM_ENTITY.schema, ProjectConstants.MD_RELATION_ITEM_ENTITY.element,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier, null);

        if (values.size() != 1) {
            log.warn("Communitiy {} has {} project items, unable to proceed", communities.get(0).getID().toString(),
                    values.size());
            return projectItem;
        }

        String itemUUID = values.get(0).getAuthority();
        if (StringUtils.isBlank(itemUUID)) {
            log.warn("Communitiy {} has no project items, unable to proceed", communities.get(0).getID().toString());
            return projectItem;
        }

        return itemService.find(context, UUIDUtils.fromString(itemUUID));
    }

    @Override
    public Item getParentProjectItemByCommunityUUID(Context context, UUID communityUUID) throws SQLException {
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

        return itemService.find(context, UUIDUtils.fromString(itemUUID));
    }

    @Override
    public boolean isProjectItem(Item item) {
        return ProjectConstants.PROJECT_ENTITY.equals(itemService.getEntityType(item));
    }

}