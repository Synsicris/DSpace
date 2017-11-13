/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model.hateoas;

import org.dspace.app.rest.model.StatusRest;
import org.dspace.app.rest.utils.Utils;

@RelNameDSpaceResource(StatusRest.NAME)
public class StatusResource extends DSpaceResource<StatusRest> {
    public StatusResource(StatusRest data, Utils utils, String... rels) {
        super(data, utils, rels);
    }
}
