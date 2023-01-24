/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The create version feature. It can be used to verify if the user can create the version of an Item.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = CanCreateVersionFeature.NAME,
    description = "It can be used to verify if the user can create a new version of an Item")
public class CanCreateVersionFeature implements AuthorizationFeature {

    public static final String NAME = "canCreateVersion";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {

        if (!(object instanceof ItemRest)) {
            return false;
        }

        if (!configurationService.getBooleanProperty("versioning.enabled", true)) {
            return false;
        }

        Item item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));

        if (!projectConsumerService.isProjectItem(item) || isVersionItem(item)) {
            return false;
        }

        EPerson currentUser = context.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return false;
        }

        return isMemberOfProjectAdminGroup(context, item, currentUser);
    }

    private boolean isMemberOfProjectAdminGroup(Context context, Item item, EPerson currentUser) throws SQLException {
        Community projectCommunity = projectConsumerService.getProjectCommunity(context, item);
        if (projectCommunity == null) {
            return false;
        }

        String adminGroupName = String.format(ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE,
                projectCommunity.getID().toString());
        Group adminGroup = groupService.findByName(context, adminGroupName);
        if (adminGroup == null) {
            return false;
        }

        return groupService.isMember(context, currentUser, adminGroup);
    }

    private boolean isVersionItem(Item item) {
        return isNotEmpty(itemService.getMetadataFirstValue(item, "synsicris", "uniqueid", null, Item.ANY));
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[]{
            ItemRest.CATEGORY + "." + ItemRest.NAME
        };
    }

}
