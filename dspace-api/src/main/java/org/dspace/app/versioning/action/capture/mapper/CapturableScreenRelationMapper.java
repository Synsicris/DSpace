/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenBuilder;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

/**
 * Translates a relation metadata of a given {@link Item} into a {@link CapturableScreen},
 * this can led to multiple mapped objects.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CapturableScreenRelationMapper implements CapturableScreenMapper {

    protected ItemService itemService =
        ContentServiceFactory.getInstance().getItemService();
    protected String relationMd;
    protected ItemUrlMapper mapper;

    @Override
    public Stream<CapturableScreen> mapToCapturableScreen(
        Context c, CapturableScreenConfiguration conf, Item item, String cookie
    ) {
        return this.itemService.getMetadataByMetadataString(item, relationMd)
            .stream()
            .map(relation -> mapRelatedItem(c, relation))
            .map(url ->
                CapturableScreenBuilder.createCapturableScreen(c, conf)
                    .withUrl(url)
                    .withCookie(cookie)
                    .computeHeaders()
                    .build()
            );
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
