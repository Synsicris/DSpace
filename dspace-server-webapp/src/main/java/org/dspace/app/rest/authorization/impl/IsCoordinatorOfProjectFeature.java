/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.COORDINATORS_ROLE;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuthorizationFeature} to evaluate if the current
 * user is coordinator of the given project.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsCoordinatorOfProjectFeature.NAME,
    description = "It can be used for verify that an user is coordinator of the given project")
public class IsCoordinatorOfProjectFeature extends IsMemberOfProjectFeature {

    public static final String NAME = "isCoordinatorOfProject";

    @Override
    protected String getRoleName() {
        return COORDINATORS_ROLE;
    }

}
