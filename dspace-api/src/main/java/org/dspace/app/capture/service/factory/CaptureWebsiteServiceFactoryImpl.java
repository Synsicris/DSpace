/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service.factory;

import org.dspace.app.capture.service.CaptureWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CaptureWebsiteServiceFactoryImpl extends CaptureWebsiteServiceFactory {

    @Autowired(required = true)
    private CaptureWebsiteService captureWebsiteService;

    @Override
    public CaptureWebsiteService getCaptureWebsiteService() {
        return this.captureWebsiteService;
    }

}
