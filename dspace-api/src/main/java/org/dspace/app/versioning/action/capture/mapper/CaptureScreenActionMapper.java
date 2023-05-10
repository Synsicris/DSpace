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

/**
 * Translates a {@link CapturableScreen} into {@link CaptureScreenAction} that will
 * be taken during the versioning process.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class CaptureScreenActionMapper {

    public abstract CaptureScreenAction<?> map(
        Context c, CapturableScreen capturableScreen
    );

}
