/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.model;

import java.io.InputStream;

import org.dspace.content.Item;

public class BundleStreamDTO {

    private InputStream stream;
    private Item item;
    private String bundleName;

    public BundleStreamDTO(InputStream stream, Item item, String bundleName) {
        super();
        this.stream = stream;
        this.item = item;
        this.bundleName = bundleName;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

}
