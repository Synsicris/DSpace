/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.EXTERNAL_READERS_ROLE;

import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuthorizationFeature} to evaluate if the current
 * user is externalreader of the given project.
 * This means that the user is part of the template group `project_{uuid}_externalreaders_group`
 * that contains the two programme groups `programme_{uuid}_members` and `programme_{uuid}_managers`.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsExternalreaderOfProjectFeature.NAME,
    description = "It can be used for verify that an user is reader of the given project")
public class IsExternalreaderOfProjectFeature extends IsMemberOfProjectFeature {

    public static final String NAME = "isExternalreaderOfProject";

    @Override
    protected String getRoleName() {
        return EXTERNAL_READERS_ROLE;
    }

}
