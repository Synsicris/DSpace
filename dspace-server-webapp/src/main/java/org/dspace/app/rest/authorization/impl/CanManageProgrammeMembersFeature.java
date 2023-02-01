/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;

import java.sql.SQLException;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.ProgrammeCreateGroupConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This features checks the presence of groups created by the consumer {@link ProgrammeCreateGroupConsumer}
 * and that the user is an admin or is in the `programme_%s_managers_group`
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = CanManageProgrammeMembersFeature.NAME,
    description = "It can be used to verify if the user can manage (ADD | REMOVE) members of a programme entity")
public class CanManageProgrammeMembersFeature implements AuthorizationFeature {

    public static final String NAME = "canManageProgrammeMembers";

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private Utils utils;

    @Override
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (object instanceof ItemRest) {
            Item item = (Item) utils.getDSpaceAPIObjectFromRest(context, object);
            return PROGRAMME.equals(this.itemService.getEntityType(item)) &&
                doProgrammeGroupsExist(context, item) &&
                canManageMembers(context, item);
        }
        return false;
    }

    public boolean doProgrammeGroupsExist(Context context, Item item)
        throws SQLException {
        return
            findTemplateGroup(context, item, PROGRAMME_MANAGERS_GROUP_TEMPLATE) != null &&
            findTemplateGroup(context, item, PROGRAMME_MEMBERS_GROUP_TEMPLATE) != null;
    }

    public boolean canManageMembers(Context context, Item item) throws SQLException {
        return
            isAdmin(context, item) ||
            isMemberOfGroup(
                context,
                this.findTemplateGroup(context, item, PROGRAMME_MANAGERS_GROUP_TEMPLATE)
            );
    }

    public boolean isMemberOfGroup(Context context, Group programmeManagersGroup) throws SQLException {
        return this.groupService.isMember(context, programmeManagersGroup);
    }

    public boolean isAdmin(Context context, Item item) throws SQLException {
        return this.authorizeService
            .authorizeActionBoolean(context, context.getCurrentUser(), item, Constants.ADMIN, true);
    }

    public Group findTemplateGroup(Context context, Item item, String groupTemplate) throws SQLException {
        return this.groupService.findByName(
            context, String.format(groupTemplate, item.getID().toString())
        );
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[]{
            ItemRest.CATEGORY + "." + ItemRest.NAME
        };
    }

}