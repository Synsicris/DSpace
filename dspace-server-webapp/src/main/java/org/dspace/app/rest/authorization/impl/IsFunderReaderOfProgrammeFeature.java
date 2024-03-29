/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.springframework.stereotype.Component;

/**
 * Checks if the given user is in the `programme_%s_readers_group` group of
 * a target project-entity.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@Component
@AuthorizationFeatureDocumentation(name = IsFunderReaderOfProgrammeFeature.NAME,
    description = "Checks if the given user is in the `programme_%s_readers_group` group a target project-entity")
public class IsFunderReaderOfProgrammeFeature extends AbstractProgrammeGroupFeature {

    public static final String NAME = "isFunderReaderOfProgramme";

    @Override
    protected String getRoleName() {
        return READERS_ROLE;
    }

}
