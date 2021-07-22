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
import java.util.UUID;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.EasyOnlineImportRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = CanMakeEasyOnlineImport.NAME, description = "")
public class CanMakeEasyOnlineImport implements AuthorizationFeature {

    public final static String NAME = "canMakeEasyOnlineImport";

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Override
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (object instanceof ItemRest) {
            Item item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));
            Community projectCommunity = projectConsumerService.getProjectCommunity(context, item);
            if (Objects.isNull(projectCommunity)) {
                return false;
            }
            if (Objects.nonNull(
                projectConsumerService.isMemberOfSubProject(context, context.getCurrentUser(), projectCommunity))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {
            EasyOnlineImportRest.CATEGORY + "." + EasyOnlineImportRest.NAME };
    }

}