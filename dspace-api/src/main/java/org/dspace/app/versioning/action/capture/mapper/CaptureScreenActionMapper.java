/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.versioning.action.capture.CaptureScreenAction;
import org.dspace.core.Context;

public abstract class CaptureScreenActionMapper {

    public abstract CaptureScreenAction<?> map(
        Context c, CapturableScreen capturableScreen
    );

}
