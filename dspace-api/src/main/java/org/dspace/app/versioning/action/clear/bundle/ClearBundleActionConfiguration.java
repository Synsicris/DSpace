/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.clear.bundle;

import java.util.stream.Stream;

import org.dspace.app.versioning.action.VersioningActionConfiguration;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ClearBundleActionConfiguration
    extends VersioningActionConfiguration<String, ClearBundleAction> {

    public ClearBundleActionConfiguration(String configuration) {
        super(configuration);
    }

    @Override
    public Stream<ClearBundleAction> createAction(Context c, Item i) {
        return Stream.of(new ClearBundleAction(getBundleName(), i));
    }

    private String getBundleName() {
        return this.configuration;
    }

}
