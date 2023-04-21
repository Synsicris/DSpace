/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class SimpleItemUrlMapper extends AbstractItemUrlMapper {

    public SimpleItemUrlMapper(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        return mapIdToUrl(item.getID());
    }

    protected String mapIdToUrl(UUID uuid) {
        return Stream.of(
            this.dspaceURL,
            this.baseUrl,
            uuid.toString()
        ).collect(Collectors.joining("/"));
    }

}
