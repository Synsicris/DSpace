/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.util.List;

import com.amazonaws.util.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

public class ItemRelationUrlMapper extends ItemUrlMapper {

    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();

    protected String relationMd;

    public ItemRelationUrlMapper(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        if (StringUtils.isNullOrEmpty(relationMd) || item == null || item.getMetadata().isEmpty()) {
            return null;
        }
        List<MetadataValue> relations = this.itemService.getMetadataByMetadataString(item, relationMd);
        if (relations.isEmpty()) {
            return null;
        }
        return null;
    }

}
