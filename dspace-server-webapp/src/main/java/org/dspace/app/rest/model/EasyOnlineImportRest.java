/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.List;
import java.util.UUID;

import org.dspace.app.rest.RestResourceController;

/**
 * The EasyOnlineImport REST Resource
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportRest extends BaseObjectRest<UUID> {

    private static final long serialVersionUID = 3838848767422793748L;
    public static final String NAME = "easyonlineimport";
    public static final String CATEGORY = RestAddressableModel.INTEGRATION;
    public static final String TYPE = "easyonlineimportresult";

    private List<UUID> created;

    private List<UUID> modified;

    private String type;

    @Override
    public String getType() {
        return NAME;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public Class<RestResourceController> getController() {
        return RestResourceController.class;
    }

    public List<UUID> getCreated() {
        return created;
    }

    public void setCreated(List<UUID> created) {
        this.created = created;
    }

    public List<UUID> getModified() {
        return modified;
    }

    public void setModified(List<UUID> modified) {
        this.modified = modified;
    }

    public void setType(String type) {
        this.type = type;
    }

}