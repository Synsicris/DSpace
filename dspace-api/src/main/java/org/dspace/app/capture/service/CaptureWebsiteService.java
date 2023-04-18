/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service;

import java.io.InputStream;

import org.dspace.app.capture.CapturableScreen;

public interface CaptureWebsiteService {

    public static final String DEFAULT_TYPE = "jpeg";
    public static final String DEFAULT_SCALE_FACTOR = "1";
    public static final String NODE_PATH = "dspace.api.node.path";
    public static final String NODE = "node";
    public static final String CAPTURE_WEBSITE_PATH = "dspace.api.capture-website.path";
    public static final String CAPTURE_WEBSITE = "capture-website";


    /**
     * Methods that takes the screenshot of a target {@link CapturableScreen}
     *
     * @param capturableScreen
     * @throws Exception
     */
    void takeScreenshot(CapturableScreen capturableScreen) throws Exception;

    public InputStream getScreenshot(CapturableScreen capturableScreen) throws Exception;

}