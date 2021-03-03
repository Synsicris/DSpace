/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public class ProjectConsumerServiceImpl implements ProjectConsumerService {

    private static final Logger log = LogManager.getLogger(ProjectConsumerServiceImpl.class);

    private static final String PROJECT = "project";
    private static final String SUBPROJECT = "subproject";

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

    @Override
    public void processItem(Context context, EPerson currentUser, Item item) {
        try {
            if (StringUtils.isNotBlank(itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY))) {
                String shared = itemService.getMetadataFirstValue(item, "cris", "workspace", "shared", Item.ANY);
                Community projectCommunity = getProjectCommunity(context, item);
                if (Objects.isNull(projectCommunity)) {
                    return;
                }
                switch (shared) {
                    case PROJECT :
                        if (!setPolicyGroup(context, item, currentUser, projectCommunity)) {
                            log.error("something went wrong, the item:" + item.getID().toString()
                                    + " could not register the policy 'cris.policy.group'.");
                        }
                        break;
                    case SUBPROJECT:
                        Community subProject = getSubProjectCommunity(projectCommunity);
                        if (Objects.isNull(subProject)) {
                            throw new RuntimeException("It was not possible to find the subProject Community");
                        }
                        List<Community> subCommunities = subProject.getSubcommunities();
                        for (Community community : subCommunities) {
                            if (setPolicyGroup(context, item, currentUser, community)) {
                                return;
                            }
                        }
                        break;
                    default:
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Community getProjectCommunity(Context context, Item item) throws SQLException {
        WorkspaceItem workspaceItem = workspaceItemService.findByItem(context, item);
        if (Objects.nonNull(workspaceItem)) {
            return workspaceItem.getCollection().getCommunities().get(0);
        }
        if (item.getCollections().isEmpty() || Objects.isNull(item.getCollections())) {
            // the item is a template item
            return null;
        }
        return item.getCollections().get(0).getCommunities().get(0);
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
                    memberGrouoOfProjectCommunity.getID().toString(), 400, 0);
            return true;
        }
        return false;
    }
}