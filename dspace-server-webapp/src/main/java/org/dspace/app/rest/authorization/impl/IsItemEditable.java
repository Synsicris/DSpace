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

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.edit.EditItemMode;
import org.dspace.content.edit.service.EditItemModeService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checks if the given user can edit the given item.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsItemEditable.NAME,
    description = "Used to verify if the given user can request the edit of an item")
public class IsItemEditable implements AuthorizationFeature {

    public static final String NAME = "isItemEditable";

    @Autowired
    private EditItemModeService editItemModeService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {

        if (!(object instanceof ItemRest) || Objects.isNull(context.getCurrentUser())) {
            return false;
        }

        String id = ((ItemRest) object).getId();

        return hasMode(editItemModeService.findModes(context, UUID.fromString(id)));
    }

    private boolean hasMode(List<EditItemMode> modes) {
        String editMode = configurationService.getProperty("project.entity.edit-mode");
        return modes.stream()
                    .anyMatch(mode -> mode.getName().equals(editMode));
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] { ItemRest.CATEGORY + "." + ItemRest.NAME };
    }

}
