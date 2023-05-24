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
 * Base interface of an action that could be processed during the versioning process.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public interface VersionigActionInterface<T> {

    public static final int MAX_RETRIES = 3;

    public void consumeAsync(Context c);

    default void retryAsync(Context c) {
        consumeAsync(c);
    }

    public void store(Context c);

    public void consume(Context c);

    default void retry(Context c) {
        consume(c);
    }

    default int getMaxRetries() {
        return MAX_RETRIES;
    }

}
