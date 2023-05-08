/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.stream.Stream;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Interface used to map a single item into a {@link CapturableScreen} object
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public interface CapturableScreenMapper {

    public Stream<CapturableScreen> mapToCapturableScreen(
        Context c, CapturableScreenConfiguration conf,
        Item item, String cookie
    );

}