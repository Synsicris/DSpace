/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action;

import org.dspace.core.Context;

/**
 * Action that encapsulates some data and needs to be
 * fired after a version has been created
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public abstract class VersioningAction<T> implements VersionigActionInterface<T> {

    protected T operation;

    public VersioningAction(T operation) {
        this.operation = operation;
    }

    @Override
    public abstract void consumeAsync(Context c);

    @Override
    public abstract void store(Context c);

    @Override
    public abstract void consume(Context c);

    public T getOperation() {
        return operation;
    }

}
