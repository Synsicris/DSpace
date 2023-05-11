/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.dspace.app.versioning.action.capture.CaptureScreenAction;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Functional interface that can be used to choose the item on which we need
 * to store data during the processing action phase.
 * Actually is used to determine where to store the {@link CaptureScreenAction} result.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@FunctionalInterface
public abstract interface ScreenActionItemMapper {

    public abstract Item mapScreenActionItem(
        Context context, Item item, Item providedItem
    );

}
