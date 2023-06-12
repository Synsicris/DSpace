/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.clear.bundle;

import java.io.IOException;
import java.sql.SQLException;

import org.dspace.app.capture.saveservice.CapturedStreamSaveService;
import org.dspace.app.capture.service.factory.CaptureWebsiteServiceFactory;
import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Action used to clear a target bundle configured with the `operation` field.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ClearBundleAction extends VersioningAction<String> {

    private final CapturedStreamSaveService capturedStreamService =
        CaptureWebsiteServiceFactory.getInstance().getCapturedStreamSaveService();
    private final Item item;

    public ClearBundleAction(String operation, Item item) {
        super(operation);
        this.item = item;
    }

    @Override
    public void consumeAsync(Context context) { }

    @Override
    public void store(Context context) {
        try {
            this.capturedStreamService.deleteAllBitstreamFromTargetBundle(context, item, getBundleName());
        } catch (SQLException | AuthorizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void consume(Context c) {
        this.store(c);
    }

    private String getBundleName() {
        return this.operation;
    }

}
