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

public class SimpleItemProvider implements ItemProvider {

    @Override
    public Stream<Item> retrieve(Context c, Item i) {
        return Stream.of(i);
    }

}
