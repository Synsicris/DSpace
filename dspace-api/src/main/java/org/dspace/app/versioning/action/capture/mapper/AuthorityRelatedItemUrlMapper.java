/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

public class AuthorityRelatedItemUrlMapper extends SimpleItemUrlMapper {

    private final ItemService itemService =
        ContentServiceFactory.getInstance().getItemService();
    private final String relationMetadata;
    private final String suffix;

    public AuthorityRelatedItemUrlMapper(
        String baseUrl, String relationMetadata, String suffix
    ) {
        super(baseUrl);
        this.relationMetadata = relationMetadata;
        this.suffix = suffix;
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        List<MetadataValue> metadata =
            this.itemService.getMetadataByMetadataString(item, relationMetadata);
        if (metadata.isEmpty()) {
            return null;
        }
        return Stream.of(
                super.mapIdToUrl(
                    UUID.fromString(metadata.get(0).getAuthority())
                ),
                suffix
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining("/"));
    }

}
