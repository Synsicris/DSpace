/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

import org.dspace.app.capture.CapturableScreen;
import org.dspace.app.capture.CapturableScreenConfiguration;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

public class CapturableScreenRelationMapper implements CapturableScreenMapper {

    protected ItemService itemService =
        ContentServiceFactory.getInstance().getItemService();
    protected String relationMd;
    protected ItemUrlMapper mapper;

    @Override
    public Stream<CapturableScreen> mapToCapturableScreen(
        Context c, CapturableScreenConfiguration conf, Item item, String token, String cookie
    ) {
        return this.itemService.getMetadataByMetadataString(item, relationMd)
            .stream()
            .map(relation -> mapRelatedItem(c, relation))
            .map(url -> new CapturableScreen(conf, url, token, cookie));
    }

    private String mapRelatedItem(Context c, MetadataValue relation) {
        return mapper.mapToUrl(c, findRelatedItem(c, relation));
    }

    private Item findRelatedItem(Context c, MetadataValue md) {
        try {
            return this.itemService.find(c, UUID.fromString(md.getAuthority()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
