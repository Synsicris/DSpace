/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;

import java.sql.SQLException;
import java.util.UUID;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.CommunityRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuthorizationFeature} to evaluate if the current
 * user is member of the given project.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsMemberOfProjectFeature.NAME,
    description = "It can be used for verify that an user is member of the given project")
public class IsMemberOfProjectFeature implements AuthorizationFeature {

    private static final Logger logger = LoggerFactory.getLogger(IsMemberOfProjectFeature.class);
    public static final String NAME = "isMemberOfProject";

    @Autowired
    protected ProjectConsumerService projectConsumerService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private GroupService groupService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (!(object instanceof ItemRest) && !(object instanceof CommunityRest)) {
            return false;
        }

        if (context.getCurrentUser() == null) {
            return false;
        }

        Item item = null;
        if (object instanceof CommunityRest) {
            Community comm = communityService.find(context, UUID.fromString(((CommunityRest) object).getUuid()));
            if (comm == null) {
                throw new IllegalArgumentException(
                    "No community found with the given id: " + ((CommunityRest) object).getUuid()
                );
            }

            item = projectConsumerService.getParentProjectItemByCommunityUUID(context, comm.getID());

            if (item == null) {
                logger.error(
                    "No parent project item found for the given community id: " + ((CommunityRest) object).getUuid()
                );
                return false;
            }
        } else {

            item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));

            if (item == null) {
                logger.error("No item found with the given id: " + ((ItemRest) object).getUuid());
                return false;
            }
        }

        return isMemberOfGroup(context, item, getRoleName());
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {
            CommunityRest.CATEGORY + "." + CommunityRest.NAME,
            ItemRest.CATEGORY + "." + ItemRest.NAME
        };
    }

    protected boolean isMemberOfGroup(Context context, Item item, String roleName) throws SQLException {
        Group group;
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
        if (PROJECT_ENTITY.equals(entityType)) {
            group = getGroupFromItem(context, item, roleName);
        } else {
            group = getGroupFromRelatedItem(context, item, roleName);
        }

        return group != null && groupService.isMember(context, group);

    }

    protected Group getGroupFromRelatedItem(Context context, Item item, String roleName) throws SQLException {
        return projectConsumerService.getProjectCommunityGroupByRole(
            context,
            projectConsumerService.getProjectCommunityByRelationProject(context, item),
            roleName
        );
    }

    protected Group getGroupFromItem(Context context, Item item, String roleName) throws SQLException {
        return projectConsumerService.getProjectCommunityGroupByRole(
            context,
            projectConsumerService.getProjectCommunity(context, item),
            roleName
        );
    }

    protected String getRoleName() {
        return MEMBERS_ROLE;
    }


}
