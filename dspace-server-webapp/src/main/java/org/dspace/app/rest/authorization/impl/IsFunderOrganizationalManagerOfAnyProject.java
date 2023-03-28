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
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
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
import org.springframework.stereotype.Component;

/**
 * Checks if the given user can assign/revoke project manager role to system user.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsFunderOrganizationalManagerOfAnyProject.NAME,
    description = "Used to verify if the given user able to assign/revoke project manager role to system user")
public class IsFunderOrganizationalManagerOfAnyProject implements AuthorizationFeature {

    public static final String NAME = "IsFunderOrganizationalManagerOfAnyProject";

    @Autowired
    private GroupService groupService;

    @Autowired
    private EPersonService ePersonService;

    @Autowired
    private ConfigurationService configurationService;

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
        String groupId = configurationService.getProperty("funder-organisational-managers.group");
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
