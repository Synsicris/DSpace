/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;

import java.sql.SQLException;
import java.util.UUID;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
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
@AuthorizationFeatureDocumentation(name = IsMemberOfFundingFeature.NAME,
    description = "It can be used for verify that an user is member of the given funding")
public class IsMemberOfFundingFeature implements AuthorizationFeature {

    public static final String NAME = "isMemberOfFunding";

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private GroupService groupService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (!(object instanceof ItemRest)) {
            return false;
        }

        if (context.getCurrentUser() == null) {
            return false;
        }

        return isMemberOfFundingGroup(context, (ItemRest) object);
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] { ItemRest.CATEGORY + "." + ItemRest.NAME };
    }

    private boolean isMemberOfFundingGroup(Context context, ItemRest itemRest) throws SQLException {

        Item item = itemService.find(context, UUID.fromString(itemRest.getUuid()));
        if (item == null) {
            throw new IllegalArgumentException("No item found with the given id: " + itemRest.getUuid());
        }

        Community community = projectConsumerService.getFirstOwningCommunity(context, item);
        if (community == null) {
            return false;
        }

        Group group = projectConsumerService.getFundingCommunityGroupByRole(context, community, getRoleName());
        return group != null && groupService.isMember(context, group);

    }

    protected String getRoleName() {
        return MEMBERS_ROLE;
    }

}
