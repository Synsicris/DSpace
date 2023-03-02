/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;
import static org.dspace.project.util.ProjectConstants.FUNDING_ENTITY;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The MakeEasyOnlineImport feature. It can be used to verify if project item can be updated with EasyOnlineImpotr.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = CanMakeEasyOnlineImportFeature.NAME,
    description = "It can be used to verify if project item can be updated with EasyOnlineImpotr")
public class CanMakeEasyOnlineImportFeature implements AuthorizationFeature {

    public final static String NAME = "canMakeEasyOnlineImport";

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (object instanceof ItemRest) {
            if (authorizeService.isAdmin(context)) {
                return true;
            }
            Item item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));

            String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
            if (!FUNDING_ENTITY.equals(entityType)) {
                // import is available only for project entity
                return false;
            }

            List<MetadataValue> values = itemService.getMetadata(item, ProjectConstants.MD_PROJECT_RELATION.schema,
                    ProjectConstants.MD_PROJECT_RELATION.element, ProjectConstants.MD_PROJECT_RELATION.qualifier,
                    null);
            if (values.isEmpty()) {
                return false;
            }
            String uuid = values.get(0).getAuthority();
            if (StringUtils.isNotBlank(uuid)) {
                // item that rappresent Project community
                Item projectItem = itemService.find(context, UUID.fromString(uuid));
                Community projectCommunity = projectConsumerService.getProjectCommunity(context, projectItem);
                if (Objects.isNull(projectCommunity)) {
                    return false;
                }
                return projectConsumerService.isMemberOfFunding(context, context.getCurrentUser(),
                        projectCommunity);
            }
        }
        return false;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {
            ItemRest.CATEGORY + "." + ItemRest.NAME
            };
    }

}