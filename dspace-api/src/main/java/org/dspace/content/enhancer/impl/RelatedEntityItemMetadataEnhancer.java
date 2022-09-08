/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer.impl;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.enhancer.ItemEnhancer;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.exception.SQLRuntimeException;
import org.dspace.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link ItemEnhancer} that update metadata values of items that its type equal to relatedEntityType
 * and get information from related items that its type equal to sourceEntityType
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
public class RelatedEntityItemMetadataEnhancer implements ItemEnhancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedEntityItemMetadataEnhancer.class);

    @Autowired
    private ItemService itemService;

    private String sourceEntityType;

    private String relatedEntityType;

    private String sourceItemMetadataField;

    private String relatedItemMetadataField;

    private String metadataField;

    @Override
    public boolean canEnhance(Context context, Item item) {
        return sourceEntityType == null || sourceEntityType.equals(itemService.getEntityType(item));
    }

    @Override
    public void enhance(Context context, Item item) {

        try {

            MetadataValue relationMetadataValue = getFirstMetadataValue(item, sourceItemMetadataField);

            if (relationMetadataValue == null) {
                return;
            }

            Item relatedItem = findRelatedEntityItem(context, relationMetadataValue);

            if (relatedItem == null) {
                return;
            }

            validateItem(relatedItem);

            updateItem(context, item, relatedItem);

        } catch (SQLException | AuthorizeException e) {
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private MetadataValue getFirstMetadataValue(Item item, String metadataField) {
        return itemService.getMetadataByMetadataString(item, metadataField)
                          .stream()
                          .findFirst()
                          .orElse(null);
    }

    private Item findRelatedEntityItem(Context context, MetadataValue metadataValue) {
        try {
            UUID relatedItemUUID = UUIDUtils.fromString(metadataValue.getAuthority());
            return relatedItemUUID != null ? itemService.find(context, relatedItemUUID) : null;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void validateItem(Item item) {
        validateItemEntityType(item);
    }

    private void validateItemEntityType(Item item) {
        if (!isItemEntityTypeEqualTo(item, relatedEntityType)) {
            String errorMessage = "item:" + item.getID() + " entity type not equal to " + relatedEntityType;
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private boolean isItemEntityTypeEqualTo(Item item, String entityType) {
        MetadataValue metadataValue = getFirstMetadataValue(item, "dspace.entity.type");
        return metadataValue != null ? metadataValue.getValue().equals(entityType) : false;
    }

    private void updateItem(Context context, Item item, Item relatedItem)
        throws SQLException, AuthorizeException {

        if (isRelatedItemHasAuthorityEqualTo(relatedItem, item.getID().toString())) {
            if (isUpdateMetadataNeeded(item, relatedItem)) {
                updateMetadata(context, item, relatedItem);
            }
        }
    }

    private boolean isRelatedItemHasAuthorityEqualTo(Item relatedItem, String uuid) {
        MetadataValue metadataValue = getFirstMetadataValue(relatedItem, relatedItemMetadataField);
        return metadataValue == null ? false : metadataValue.getAuthority().equals(uuid);
    }

    private boolean isUpdateMetadataNeeded(Item item, Item relatedItem) {
        MetadataValue sourceMetadataValue = getFirstMetadataValue(item, metadataField);
        MetadataValue relatedMetadataValue = getFirstMetadataValue(relatedItem, metadataField);

        if (relatedMetadataValue != null && sourceMetadataValue != null) {
            return !sourceMetadataValue.getValue().equals(relatedMetadataValue);
        }

        return relatedMetadataValue != null && sourceMetadataValue == null;
    }

    private void updateMetadata(Context context, Item item, Item relatedItem)
        throws SQLException, AuthorizeException {

        MetadataValue sourceMetadataValue = getFirstMetadataValue(item, metadataField);
        MetadataValue relatedMetadataValue = getFirstMetadataValue(relatedItem, metadataField);

        if (sourceMetadataValue != null) {
            clearAndAddMetadata(context, item, relatedMetadataValue.getValue(), sourceMetadataValue);
        } else {
            addMetadata(context, item, relatedMetadataValue.getValue(), metadataField);
        }
    }

    private void clearAndAddMetadata(Context context, Item item, String value, MetadataValue mValue)
        throws SQLException, AuthorizeException {

        itemService.clearMetadata(context, item, mValue.getSchema(), mValue.getElement(), mValue.getQualifier(),
            mValue.getLanguage());
        itemService.addMetadata(context, item, mValue.getSchema(), mValue.getElement(), mValue.getQualifier(),
            mValue.getLanguage(), value);
        itemService.update(context, item);
    }

    private void addMetadata(Context context, Item item, String value, String metadataField)
        throws SQLException, AuthorizeException {

        String[] fields = getElements(metadataField);
        itemService.addMetadata(context, item, fields[0], fields[1], fields[2], null, value);
        itemService.update(context, item);
    }

    private String[] getElements(String fieldName) {
        String[] tokens = StringUtils.split(fieldName, ".");

        int add = 4 - tokens.length;
        if (add > 0) {
            tokens = (String[]) ArrayUtils.addAll(tokens, new String[add]);
        }

        return tokens;
    }

    public String getSourceEntityType() {
        return sourceEntityType;
    }

    public void setSourceEntityType(String sourceEntityType) {
        this.sourceEntityType = sourceEntityType;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public String getSourceItemMetadataField() {
        return sourceItemMetadataField;
    }

    public void setSourceItemMetadataField(String sourceItemMetadataField) {
        this.sourceItemMetadataField = sourceItemMetadataField;
    }

    public String getRelatedItemMetadataField() {
        return relatedItemMetadataField;
    }

    public void setRelatedItemMetadataField(String relatedItemMetadataField) {
        this.relatedItemMetadataField = relatedItemMetadataField;
    }

    public String getMetadataField() {
        return metadataField;
    }

    public void setMetadataField(String metadataField) {
        this.metadataField = metadataField;
    }
}