/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.core.Context;

/**
 * FunctionalInterface that will generate a {@link MetadataValueDTO} that can be used
 * inside the `CaptureScreenAction` to save additional information inside the
 * generated Bitstream of a DSpace web-page.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@FunctionalInterface
public abstract interface MetadataValueDTOSupplier {

    public MetadataValueDTO get(Context c, Item i);

}
