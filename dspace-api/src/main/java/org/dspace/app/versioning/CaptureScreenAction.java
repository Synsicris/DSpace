/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.util.function.BiConsumer;

import org.dspace.app.capture.CapturableScreen;
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
    private BiConsumer<Context, Item> receiver;

    public CaptureScreenAction(T operation, Item item, String bundleName) {
        super(operation);
        this.item = item;
        this.bundleName = bundleName;
    }

    @Override
    public void consume(Context context, T t) {
        // capture screen and stores it inside the item using
        // a target bundleName
        // capturableService.captureScreen(context, item, bundleName);
        this.receiver.accept(context, item);
    }

}
