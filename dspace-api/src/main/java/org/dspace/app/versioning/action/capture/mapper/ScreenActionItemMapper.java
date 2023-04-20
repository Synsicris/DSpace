/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.dspace.content.Item;
import org.dspace.core.Context;

@FunctionalInterface
public abstract interface ScreenActionItemMapper {

    public abstract Item mapScreenActionItem(
        Context context, Item item, Item providedItem
    );

}
