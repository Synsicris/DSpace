/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer;

import java.util.StringTokenizer;

/**
 * Abstract implementation of {@link ItemEnhancer} that provide common structure
 * for all the item enhancers.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public abstract class AbstractItemEnhancer implements ItemEnhancer {

    private String virtualQualifier;

    public String getVirtualQualifier() {
        return virtualQualifier;
    }

    public void setVirtualQualifier(String virtualQualifier) {
        this.virtualQualifier = virtualQualifier;
    }

    protected String getVirtualMetadataField() {
        StringTokenizer dcf = new StringTokenizer(virtualQualifier, ".");
        if (dcf.countTokens() > 1) {
            return virtualQualifier;
        } else {
            return VIRTUAL_METADATA_SCHEMA + "." + VIRTUAL_METADATA_ELEMENT + "." + virtualQualifier;
        }
    }

    protected String getVirtualSourceMetadataField() {
        StringTokenizer dcf = new StringTokenizer(virtualQualifier, ".");
        if (dcf.countTokens() > 1) {
            return virtualQualifier;
        } else {
            return VIRTUAL_METADATA_SCHEMA + "." + VIRTUAL_SOURCE_METADATA_ELEMENT + "." + virtualQualifier;
        }
    }

}
