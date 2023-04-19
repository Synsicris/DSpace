/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import org.dspace.core.Context;

/**
 * Action that encapsulates some data and needs to be
 * fired after a version has been created
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public abstract class VersioningAction<T> {

    private T operation;

    public VersioningAction(T operation) {
        this.operation = operation;
    }

    public abstract void consume(Context c, T operation);

    public T getOperation() {
        return operation;
    }

}
