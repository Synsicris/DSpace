/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service.factory;

import org.dspace.app.capture.saveservice.CapturedStreamSaveService;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 *
 * Factory that encapsulate {@link CaptureWebsiteService}
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class CaptureWebsiteServiceFactory {

    public static CaptureWebsiteServiceFactory getInstance() {
        return DSpaceServicesFactory.getInstance().getServiceManager()
                .getServiceByName("captureWebsiteFactory", CaptureWebsiteServiceFactory.class);
    }

    public abstract CaptureWebsiteService getCaptureWebsiteService();

    public abstract CapturedStreamSaveService getCapturedStreamSaveService();

}
