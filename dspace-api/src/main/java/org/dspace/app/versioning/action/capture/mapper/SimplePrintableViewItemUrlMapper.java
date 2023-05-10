/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.Map;

/**
 * Simple URL mapper that adds the default `view=print` parameter
 * to load the printable view in the dspace-ui.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class SimplePrintableViewItemUrlMapper extends SimpleParametersItemUrlMapper {

    public SimplePrintableViewItemUrlMapper(String baseUrl) {
        super(Map.of("view", "print"), new SimpleItemUrlMapper(baseUrl));
    }

}
