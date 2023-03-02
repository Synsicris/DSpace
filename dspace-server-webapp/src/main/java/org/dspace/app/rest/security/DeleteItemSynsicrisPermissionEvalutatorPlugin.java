/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.security;

import static org.dspace.app.rest.model.ItemRest.NAME;
import static org.dspace.app.rest.security.DSpaceRestPermission.DELETE;
import static org.dspace.project.util.ProjectConstants.MEMBERS_ROLE;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.dspace.synsicris.security.SecuritySynsicrisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Synsicris custom PermissionEvalutatorPlugin that check
 * if the current user is a member of MEMBERS_ROLE group, so has permission to delete item.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
@Component
public class DeleteItemSynsicrisPermissionEvalutatorPlugin extends RestObjectPermissionEvaluatorPlugin {

    private static final Logger log = LoggerFactory.getLogger(DeleteItemSynsicrisPermissionEvalutatorPlugin.class);

    @Autowired
    private ItemService itemService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private SecuritySynsicrisService securitySynsicrisService;

    @Override
    public boolean hasDSpacePermission(Authentication authentication, Serializable targetId, String targetType,
            DSpaceRestPermission permission) {

        DSpaceRestPermission restPermission = DSpaceRestPermission.convert(permission);
        if (!DELETE.equals(restPermission) || !StringUtils.equalsIgnoreCase(targetType, NAME)) {
            return false;
        }

        Request request = requestService.getCurrentRequest();
        Context context = ContextUtil.obtainContext(request.getHttpServletRequest());

        // anonymous user
        if (Objects.isNull(context.getCurrentUser())) {
            return false;
        }

        try {
            Item item = itemService.find(context, UUID.fromString(targetId.toString()));
            if (Objects.isNull(item)) {
                return true;
            }
            return securitySynsicrisService.isMemberOfRoleGroupOfProject(context, item, MEMBERS_ROLE);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

}