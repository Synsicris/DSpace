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

public interface CapturableScreenMapper {

    public Stream<CapturableScreen> mapToCapturableScreen(
        Context c, CapturableScreenConfiguration conf,
        Item item, String cookie
    );

}