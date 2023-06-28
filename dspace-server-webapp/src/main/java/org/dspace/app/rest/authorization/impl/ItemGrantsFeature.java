/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ItemGrants feature. It can be used for verify that an user
 * has access to modify the grants of a specific item.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = ItemGrantsFeature.NAME,description =
    "It can be used for verify that an user has access to modify the grants of a specific item")
public class ItemGrantsFeature implements AuthorizationFeature {

    public static final String NAME = "canEditGrants";

    @Autowired
    AuthorizeService authService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private Utils utils;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        EPerson currentUser = context.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return false;
        }

        if (authService.isAdmin(context)) {
            return true;
        }

        if (StringUtils.equals(object.getType(), ItemRest.NAME)) {
            Item item = getItem(context, object);
            if (isForbbidenEntityType(context, item)) {
                return false;
            }
            Community c = projectConsumerService.getFirstOwningCommunity(context, item);
            return Optional.ofNullable(
                    projectConsumerService.getFirstOwningCommunity(context, item)
                )
                .map(funding -> isCoordinatorMemberOfFunding(context, funding, currentUser))
                .orElse(false);
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private Item getItem(Context context, BaseObjectRest object) throws IllegalArgumentException, SQLException {
        DSpaceObject dSpaceObject = (DSpaceObject) utils.getDSpaceAPIObjectFromRest(context, object);
        if (dSpaceObject.getType() == Constants.ITEM && Objects.nonNull(dSpaceObject)) {
            return (Item) dSpaceObject;
        }
        return null;
    }

    private boolean isForbbidenEntityType(Context context, Item item) {
        String entiyType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);

        return StringUtils.isBlank(entiyType) || !entiyType.equals(ProjectConstants.FUNDING_ENTITY);
    }

    private boolean isCoordinatorMemberOfFunding(Context context, Community community, EPerson submitter) {
        try {
            Group group = projectConsumerService.getFundingCommunityGroupByRole(context, community,
                ProjectConstants.COORDINATORS_ROLE);
            if (!Objects.isNull(group) && groupService.isMember(context, submitter, group)) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot retrieve the related funding group", e);
        }
        return false;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] { ItemRest.CATEGORY + "." + ItemRest.NAME };
    }

}