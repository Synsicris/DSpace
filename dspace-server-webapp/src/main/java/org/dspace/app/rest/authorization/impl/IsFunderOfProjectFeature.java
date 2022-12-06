/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuthorizationFeature} to evaluate if the current
 * user is funder of the given project.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsFunderOfProjectFeature.NAME,
    description = "It can be used for verify that an user is funder of the given project")
public class IsFunderOfProjectFeature extends IsMemberOfProjectFeature {

    public static final String NAME = "isFunderOfProject";

    @Override
    protected String getRoleName() {
        return FUNDERS_ROLE;
    }

}
