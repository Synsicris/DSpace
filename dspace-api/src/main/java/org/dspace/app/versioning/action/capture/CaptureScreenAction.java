/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.capture.service.factory.CaptureWebsiteServiceFactory;
import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * VersioningAction used to capture a screen
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public class CaptureScreenAction<T extends CapturableScreen> extends VersioningAction<T> {

    private Item item;
    private String bundleName;
    private boolean cleanBundle;
    private CaptureWebsiteService captureWebsiteService =
        CaptureWebsiteServiceFactory.getInstance().getCaptureWebsiteService();

    public CaptureScreenAction(T operation, Item item, String bundleName, boolean cleanBundle) {
        super(operation);
        this.item = item;
        this.bundleName = bundleName;
        this.cleanBundle = cleanBundle;
    }

    @Override
    public void consume(Context context) {
        // capture screen and stores it inside the item using
        // a target bundleName
        // capturableService.captureScreen(context, item, bundleName);
        try {
            this.captureWebsiteService.takeScreenshot(context, this.operation);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Item getItem() {
        return item;
    }

    public String getBundleName() {
        return bundleName;
    }

    public boolean isCleanBundle() {
        return cleanBundle;
    }

}
