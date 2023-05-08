/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

/**
 * Collection of common {@link ItemValueExtractor}.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ItemValueExtractors {

    public static final ItemValueExtractor<String> uuidExtractor = new ItemUuidExtractor();

    private ItemValueExtractors() { }

}
