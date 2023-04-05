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

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.SiteRest;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Checks if the given user can assign/revoke project manager role to system user.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
public abstract class IsFunderRoleOfAnyProject implements AuthorizationFeature {

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected EPersonService ePersonService;

    @Autowired
    protected ConfigurationService configurationService;

    abstract public String getGroupID();

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        Group group = getGroup(context);
        if (Objects.isNull(group)) {
            return false;
        }
        EPerson ePerson = getEPerson(context, object);
        return groupService.isMember(context, ePerson, group);
    }

    private Group getGroup(Context context) throws SQLException {
        String groupId = getGroupID();
        Group group = null;
        if (StringUtils.isNotEmpty(groupId)) {
            group = groupService.find(context, UUID.fromString(groupId));
        }
        return group;
    }

    private EPerson getEPerson(Context context, BaseObjectRest object) throws SQLException {
        EPerson ePerson = context.getCurrentUser();
        if (!Objects.isNull(object) && object instanceof EPersonRest) {
            ePerson = ePersonService.find(context, UUID.fromString(String.valueOf(object.getId())));
        }
        return ePerson;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {
            EPersonRest.CATEGORY + "." + EPersonRest.NAME,
            SiteRest.CATEGORY + "." + SiteRest.NAME
        };
    }

}
