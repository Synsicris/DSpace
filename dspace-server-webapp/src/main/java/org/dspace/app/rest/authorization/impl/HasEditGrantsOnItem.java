/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checks if the given user has edit policy on the given item
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.com)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = HasEditGrantsOnItem.NAME,
    description = "Used to verify if the given user has policy grants on the given item")
public class HasEditGrantsOnItem implements AuthorizationFeature {

    public static final String NAME = "hasEditGrantsOnItem";

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {

        if (!(object instanceof ItemRest) || Objects.isNull(object)) {
            return false;
        }

        String id = ((ItemRest) object).getId();
        Item item = itemService.find(context, UUID.fromString(id));

        if (Objects.isNull(item)) {
            return false;
        }

        List<MetadataValue> mdList =
            itemService.getMetadataByMetadataString(
                item,
                ProjectConstants.MD_POLICY_GROUP.toString()
            );

        if (mdList.size() == 0) {
            return false;
        }

        MetadataValue policyValue = mdList.get(0);

        if (StringUtils.isBlank(policyValue.getAuthority())) {
            return false;
        }

        Group group = groupService.find(context, UUIDUtils.fromString(policyValue.getAuthority()));

        if (Objects.isNull(group)) {
            return false;
        }

        EPerson ePerson = context.getCurrentUser();
        return groupService.isMember(context, ePerson, group);
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {
            ItemRest.CATEGORY + "." + ItemRest.NAME
        };
    }

}
