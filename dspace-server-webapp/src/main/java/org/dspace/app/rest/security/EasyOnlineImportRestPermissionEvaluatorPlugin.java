/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.security;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.model.EasyOnlineImportRest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
public class EasyOnlineImportRestPermissionEvaluatorPlugin extends RestObjectPermissionEvaluatorPlugin {

    private static final Logger log = LoggerFactory.getLogger(EasyOnlineImportRestPermissionEvaluatorPlugin.class);

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private EPersonService ePersonService;

    @Autowired
    private ItemService itemService;

    @Override
    public boolean hasDSpacePermission(Authentication authentication, Serializable targetId, String targetType,
            DSpaceRestPermission permission) {
        DSpaceRestPermission restPermission = DSpaceRestPermission.convert(permission);

        if (!DSpaceRestPermission.WRITE.equals(restPermission)
            || !StringUtils.equalsIgnoreCase(targetType, EasyOnlineImportRest.NAME)) {
            return false;
        }

        Request request = requestService.getCurrentRequest();
        Context context = ContextUtil.obtainContext(request.getServletRequest());
        EPerson ePerson = null;

        try {
            ePerson = ePersonService.findByEmail(context, (String) authentication.getPrincipal());
            UUID dsoUuid = UUID.fromString(targetId.toString());
            // anonymous user
            if (Objects.isNull(ePerson)) {
                return false;
            }
            Item item = itemService.find(context, dsoUuid);
            if (Objects.isNull(item)) {
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
                // item that represents Project community
                Item projectItem = itemService.find(context, UUID.fromString(uuid));
                Community projectCommunity = projectConsumerService.getProjectCommunity(context, projectItem);
                if (Objects.isNull(projectCommunity)) {
                    return false;
                }
                return projectConsumerService.isMemberOfFunding(context, context.getCurrentUser(),
                        projectCommunity);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

}
