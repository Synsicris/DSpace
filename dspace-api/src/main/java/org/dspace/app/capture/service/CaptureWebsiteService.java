/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service;

import java.io.InputStream;
import java.util.Map;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.core.Context;

public interface CaptureWebsiteService {

    /**
     * Methods that takes the screenshot of a target {@link CapturableScreen}
     *
     * @param capturableScreen
     * @throws Exception
     */
    void takeScreenshot(Context c, CapturableScreen capturableScreen) throws Exception;

    public InputStream getScreenshot(Context c, CapturableScreen capturableScreen) throws Exception;

    public StringBuilder computeHeader(Context c, CapturableScreenConfiguration configuration);

    public Map<String, String> getExtensionsToMimeType();

    Process getScreenshotProcess(Context c, CapturableScreen capturableScreen) throws Exception;

}