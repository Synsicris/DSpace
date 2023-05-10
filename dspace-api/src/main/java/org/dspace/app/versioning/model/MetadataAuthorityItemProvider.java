/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.model;

import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.dspace.authority.service.ItemSearchService;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This provider returns the linked items using a `metadataField` and its authority.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class MetadataAuthorityItemProvider implements ItemProvider {

    private static final Logger logger  = LoggerFactory.getLogger(MetadataAuthorityItemProvider.class);

    @Autowired
    private ItemSearchService itemSearchService;
    private ItemService itemService =
        ContentServiceFactory.getInstance().getItemService();
    private final String metadataField;

    public MetadataAuthorityItemProvider(String metadataField) {
        super();
        this.metadataField = metadataField;
    }

    @Override
    public Stream<Item> retrieve(Context c, Item i) {
        return this.itemService.getMetadataByMetadataString(i, metadataField)
            .stream()
            .filter(metadata ->
                StringUtils.isNotBlank(metadata.getAuthority()) &&
                StringUtils.isNotEmpty(metadata.getAuthority())
            )
            .map(metadata -> this.find(c, metadata.getAuthority()));
    }

    private Item find(Context c, String authority) {
        Item found = null;
        try {
            UUID itemId = UUID.fromString(authority);
            found = this.itemService.find(c, itemId);
        } catch (Exception e) {
            logger.warn("Cannot parse the authority " + authority);
            found = this.itemSearchService.search(c, authority);
        }
        return found;
    }

}
