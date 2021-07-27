/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
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
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public class ProjectConsumerServiceImpl implements ProjectConsumerService {

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

    @Override
    public void processItem(Context context, EPerson currentUser, Item item) {
        try {
            if (StringUtils.isNotBlank(itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY))) {
                String shared = itemService.getMetadataFirstValue(item, "cris", "project", "shared", Item.ANY);
                Community projectCommunity = getProjectCommunity(context, item);
                if (Objects.isNull(projectCommunity) || StringUtils.isBlank(shared)) {
                    return;
                }
                switch (shared) {
                    case ProjectConstants.PARENTPROJECT :
                    case ProjectConstants.OWNING_PROJECT :
                        if (!setPolicyGroup(context, item, currentUser, projectCommunity)) {
                            log.error("something went wrong, the item:" + item.getID().toString()
                                    + " could not register the policy 'cris.policy.group'.");
                        }
                        break;
                    case ProjectConstants.PROJECT:
                        Community project = getSubProjectCommunity(projectCommunity);
                        if (Objects.isNull(project)) {
                            throw new RuntimeException("It was not possible to find the subProject Community");
                        }
                        List<Community> subCommunities = project.getSubcommunities();
                        for (Community community : subCommunities) {
                            if (setPolicyGroup(context, item, currentUser, community)) {
                                return;
                            }
                        }
                        break;
                    case ProjectConstants.SHARED:
                        setPolicyGroup(context, item, configurationService.getProperty("project.creation.group"));
                        break;
                    case ProjectConstants.FUNDER:
                        setPolicyGroup(context, item, configurationService.getProperty("project.funder.group"));
                        break;
                    case ProjectConstants.FUNDER_PROGRAMME:
                        setPolicyGroup(context,item,configurationService.getProperty("project.funder_programme.group"));
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
        Community parentProjectCommunity = null;
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

        parentProjectCommunity = owningCollection.getCommunities().get(0);
        while (Arrays.stream(commToSkip).anyMatch(parentProjectCommunity.getName()::equals)) {
            parentProjectCommunity = parentProjectCommunity.getParentCommunities().get(0);
        }
        return parentProjectCommunity;
    }

    private Community getSubProjectCommunity(Community projectCommunity) {
        String subprojectName =  configurationService.getProperty("project.subproject-community-name");
        List<Community> subCommunities = projectCommunity.getSubcommunities();
        for (Community community : subCommunities) {
            if (StringUtils.equals(subprojectName, community.getName())) {
                return community;
            }
        }
        return null;
    }

    private boolean setPolicyGroup(Context context, Item item, EPerson currentUser, Community community)
            throws SQLException {
        StringBuilder memberGroupName = new StringBuilder("project_")
                                                 .append(community.getID().toString())
                                                 .append("_members_group");
        Group memberGrouoOfProjectCommunity = groupService.findByName(context, memberGroupName.toString());
        boolean isAdmin = authorizeService.isAdmin(context);
        boolean isCommunityAdmin = authorizeService.isAdmin(context, community);
        boolean isGroupMember = groupService.isMember(context, currentUser, memberGrouoOfProjectCommunity);
        if (isAdmin || isGroupMember || isCommunityAdmin) {
            itemService.replaceMetadata(context, item, "cris", "policy", "group", null, memberGroupName.toString(),
                    memberGrouoOfProjectCommunity.getID().toString(), Choices.CF_ACCEPTED, 0);
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
            Community subprojectCommunity = isMemberOfSubProject(context, currentUser, projectCommunity);
            if (Objects.nonNull(subprojectCommunity)) {
                List<MetadataValue> values = communityService.getMetadata(subprojectCommunity,
                        ProjectConstants.MD_PROJECT_ENTITY.SCHEMA, ProjectConstants.MD_PROJECT_ENTITY.ELEMENT,
                        ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, null);
                if (CollectionUtils.isNotEmpty(values)) {
                    String defaultValue = getDefaultSharedValueByItemProject(context, values);
                    if (StringUtils.isNoneEmpty(defaultValue)) {
                        itemService.replaceMetadata(context, item, "cris", "project", "shared",
                                                       null, defaultValue, null, Choices.CF_UNSET, 0);
                    }
                }
            } else {
                itemService.replaceMetadata(context, item, "cris", "project", "shared",
                                            null, ProjectConstants.PARENTPROJECT, null, Choices.CF_UNSET, 0);
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
    public Community isMemberOfSubProject(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException {

        List<Community> subprojects = getAllSubProjectsByUser(context, ePerson, projectCommunity);
        // user MUST be member of at least one subproject within a project
        if (subprojects.size() > 0) {
            return subprojects.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Community> getAllSubProjectsByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException {
        Community subprojectsParentCommunity = getSubProjectCommunity(projectCommunity);
        List<Community> subprojects = new ArrayList<Community>();

        if (Objects.isNull(subprojectsParentCommunity)) {
            return subprojects;
        }

        List<Community> subCommunities = subprojectsParentCommunity.getSubcommunities();
        for (Community community : subCommunities) {
            StringBuilder memberGroupName = new StringBuilder("project_")
                                                      .append(community.getID().toString())
                                                      .append("_members_group");
            Group group = groupService.findByName(context, memberGroupName.toString());
            boolean isGroupMember = groupService.isMember(context, ePerson, group);
            if (isGroupMember) {
                subprojects.add(community);
            }
        }
        return subprojects;
    }

    @Override
    public Community getParentCommunityByProjectItem(Context context, Item item) throws SQLException {
        String uuid = itemService.getMetadataFirstValue(item, "synsicris", "relation", "parentproject", Item.ANY);
        if (StringUtils.isNotBlank(uuid)) {
            Item parentProjectItem = itemService.find(context, UUID.fromString(uuid));
            if (Objects.nonNull(parentProjectItem)) {
                return item.getOwningCollection().getCommunities().get(0);
            }
        }
        return null;
    }

}