/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action;

import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Configuration of an action that will be generated at runtime and can be
 * used during the versioning process.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <C>
 * @param <V>
 */
public abstract class VersioningActionConfiguration<C, V extends VersioningAction<?>> {

    protected final C configuration;

    public VersioningActionConfiguration(C configuration) {
        super();
        this.configuration = configuration;
    }

    public abstract Stream<V> createAction(Context c, Item i);


}
