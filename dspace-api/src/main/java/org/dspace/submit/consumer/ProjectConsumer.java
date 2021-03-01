/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Consumer}
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectConsumer implements Consumer {

    private static final Logger log = LoggerFactory.getLogger(ProjectConsumer.class);

    private static final String PROJECT = "project";
    private static final String SUBPROJECT = "subproject";

    private ItemService itemService;

    private GroupService groupService;

    private ConfigurationService configurationService;

    private CommunityService communityService;

    private AuthorizeService authorizeService;

    private Set<Item> itemsAlreadyProcessed = new HashSet<Item>();

    @Override
    public void initialize() throws Exception {
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.communityService = ContentServiceFactory.getInstance().getCommunityService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
        this.configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
//        if (event.getEventType() != Event.MODIFY_METADATA) {
//            log.warn("ProjectConsumer should not have been given this kind of subject in an event, skipping: "
//                     + event.toString());
//            return;
//        }
        EPerson currentUser = context.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return;
        }
        Object dso = event.getSubject(context);
        if ((dso instanceof Item)) {
            Item item = (Item) dso;
            processItem(context, currentUser, item);
        }
        if ((dso instanceof WorkspaceItem)) {
            WorkspaceItem WorkspaceItem = (WorkspaceItem) dso;
            processItem(context, currentUser, WorkspaceItem.getItem());
        }
    }

    private void processItem(Context context, EPerson currentUser, Item item) {
        if (itemsAlreadyProcessed.contains(item)) {
            return;
        }
        String s = itemService.getMetadataFirstValue(item, "cris", "policy", "group", Item.ANY);
        System.out.println(s);
        if (StringUtils.isNotBlank(s)) {
            String shared = itemService.getMetadataFirstValue(item, "cris", "workspace", "shared", Item.ANY);
            switch (shared) {
                case PROJECT: setProjectType(context, item, currentUser);
                  break;
                case SUBPROJECT: setSubProjectType(context, item, currentUser);;
                  break;
                default:
                    log.warn(" " + shared);
            }
        }
        itemsAlreadyProcessed.add(item);
    }

    private void setSubProjectType(Context context, Item item, EPerson currentUser) {
        try {
            String subProjectCommunityUUID =  configurationService.getProperty("project.subproject-community-id");
            Community subProject = communityService.find(context, UUID.fromString(subProjectCommunityUUID));
            List<Community> subCommunities = subProject.getSubcommunities();
            for (Community community : subCommunities) {
                StringBuilder memberGroupName = new StringBuilder("project_").append(community.getID().toString())
                        .append("_members_group");
                if (groupService.isMember(context, currentUser, community.getAdministrators())) {
                    itemService.replaceMetadata(context, item, "cris", "policy", "group", null,
                             "project_" + community.getAdministrators().getID().toString() + "_admin_group",
                                          community.getAdministrators().getID().toString(), 400, 0);
                } else if (replacePolicyGroupValue(context, item, currentUser, memberGroupName.toString())) {
                    return;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private void setProjectType(Context context, Item item, EPerson currentUser) {
        try {
            Community projectCommunity = item.getCollections().get(0).getCommunities().get(0);
            Group adminsGroup = projectCommunity.getAdministrators();
            if (groupService.isMember(context, currentUser, adminsGroup)) {
                itemService.replaceMetadata(context, item, "cris", "policy", "group", null,
                         "project_" + projectCommunity.getID().toString() + "_admin_group",
                         adminsGroup.getID().toString(), 400, 0);
            } else {
                StringBuilder memberGroupName = new StringBuilder("project_")
                             .append(projectCommunity.getID().toString()).append("_members_group");
                replacePolicyGroupValue(context, item, currentUser, memberGroupName.toString());
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private boolean replacePolicyGroupValue(Context context, Item item, EPerson currentUser, String memberGroupName)
            throws SQLException {
        Group memberGrouoOfProjectCommunity = groupService.findByName(context, memberGroupName.toString());
        if (groupService.isMember(context, currentUser, memberGrouoOfProjectCommunity)) {
            itemService.replaceMetadata(context, item, "cris", "policy", "group", null, memberGroupName,
                    memberGrouoOfProjectCommunity.getID().toString(), 400, 0);
            return true;
        }
        return false;
    }

    @Override
    public void end(Context ctx) throws Exception {
        itemsAlreadyProcessed.clear();
    }

    @Override
    public void finish(Context ctx) throws Exception {}

}