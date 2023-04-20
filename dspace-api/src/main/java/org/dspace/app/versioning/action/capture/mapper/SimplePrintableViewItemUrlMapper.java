/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.Map;

public class SimplePrintableViewItemUrlMapper extends SimpleParametersItemUrlMapper {

    public SimplePrintableViewItemUrlMapper(String baseUrl) {
        super(Map.of("view", "print"), new SimpleItemUrlMapper(baseUrl));
    }

}
