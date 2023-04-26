/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action;

import org.dspace.core.Context;

public interface VersionigActionInterface<T> {

    public void consumeAsync(Context c);

    public void store(Context c);

    public void consume(Context c);

}
