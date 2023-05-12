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
    protected int maxRetries;

    public VersioningAction(T operation) {
        this(operation, MAX_RETRIES);
    }

    public VersioningAction(T operation, int maxRetries) {
        this.operation = operation;
        this.maxRetries = maxRetries;
    }

    @Override
    public abstract void consumeAsync(Context c);

    @Override
    public void retryAsync(Context c) {
        this.consumeAsync(c);
    }

    @Override
    public abstract void store(Context c);

    @Override
    public abstract void consume(Context c);

    @Override
    public void retry(Context c) {
        this.consume(c);
    }

    public T getOperation() {
        return operation;
    }

    @Override
    public int getMaxRetries() {
        return this.maxRetries;
    }

    @Override
    public String toString() {
        return new StringBuilder("VersioningAction=[operation=")
                .append(this.operation.toString())
                .append(",maxRetires=")
                .append(this.maxRetries)
                .append("]")
                .toString();
    }

}
