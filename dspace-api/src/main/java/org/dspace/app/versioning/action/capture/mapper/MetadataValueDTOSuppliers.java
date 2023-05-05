/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataFieldName;
import org.dspace.content.dto.MetadataValueDTO;

/**
 * Contains all the common {@link MetadataValueDTOSupplier} suppliers
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class MetadataValueDTOSuppliers {

    private MetadataValueDTOSuppliers() { }

    public static MetadataValueDTOSupplier getDcTypeSupplier(String value) {
        return (c, item) -> new MetadataValueDTO("dc", "type", null, Item.ANY, value);
    }

    public static MetadataValueDTOSupplier getSupplier(String metadata, String value) {
        if (StringUtils.isEmpty(metadata) || StringUtils.isEmpty(metadata)) {
            return null;
        }
        return getSupplier(new MetadataFieldName(metadata), value);
    }

    public static MetadataValueDTOSupplier getSupplier(MetadataFieldName field, String value) {
        return (c, item) -> new MetadataValueDTO(field.schema, field.element, field.qualifier, Item.ANY, value);
    }

}
