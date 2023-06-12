/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Computes a static url using the `dspaceURL` and a given `baseURL`.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class StaticItemUrlMapper extends AbstractItemUrlMapper {

    public StaticItemUrlMapper(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        return Stream.of(
            this.dspaceURL,
            this.baseUrl
        )
        .collect(Collectors.joining("/"));
    }

}
