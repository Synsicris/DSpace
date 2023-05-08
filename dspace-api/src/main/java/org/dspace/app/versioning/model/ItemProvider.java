/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.model;

import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Provider interface that loads the item that we want to process
 * starting from a base item itself, the starting item is a `Project` entity.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public interface ItemProvider {

    public Stream<Item> retrieve(Context c, Item i);

}
