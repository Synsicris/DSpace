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
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsFunderReaderOfAnyProject.NAME,
    description = "Used to verify if the given user is a funder reader")
public class IsFunderReaderOfAnyProject extends IsFunderRoleOfAnyProject {

    public static final String NAME = "isFunderReaderOfAnyProject";

    public String getGroupID() {
        return super.configurationService.getProperty("funders-readers.group");
    }

}
