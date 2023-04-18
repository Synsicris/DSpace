/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.util.stream.Stream;

import org.dspace.app.capture.CapturableScreen;
import org.dspace.app.capture.CapturableScreenConfiguration;
import org.dspace.content.Item;
import org.dspace.core.Context;

public interface CapturableScreenMapper {

    public Stream<CapturableScreen> mapToCapturableScreen(
        Context c, CapturableScreenConfiguration conf, Item item, String token, String cookie
    );

}