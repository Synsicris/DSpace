/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.springframework.stereotype.Component;

/**
 * Checks if the given user can assign/revoke project manager role to system user.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsFunderOrganizationalManagerOfAnyProject.NAME,
    description = "Used to verify if the given user is a funder organizational manager")
public class IsFunderOrganizationalManagerOfAnyProject extends IsFunderRoleOfAnyProject {

    public static final String NAME = "IsFunderOrganizationalManagerOfAnyProject";

    @Override
    public String getGroupID() {
        return configurationService.getProperty("funder-organisational-managers.group");
    }

}
