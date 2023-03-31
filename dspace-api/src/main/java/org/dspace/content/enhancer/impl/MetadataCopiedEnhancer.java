/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer.impl;

import static org.dspace.core.CrisConstants.PLACEHOLDER_PARENT_METADATA_VALUE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.MetadataFieldName;
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
 * Implementation of {@link ItemEnhancer} which copies metadata values from item to its relatedItems
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
public class MetadataCopiedEnhancer implements ItemEnhancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataCopiedEnhancer.class);

    @Autowired
    private ItemService itemService;

    private String sourceEntityType;

    private String relatedEntityType;

    private String relationMetadataField;

    private List<String> originalMetadataFields;

    private List<String> copiedMetadataFields;

    @Override
    public boolean canEnhance(Context context, Item item) {
        return sourceEntityType == null || sourceEntityType.equals(itemService.getEntityType(item));
    }

    @Override
    public void enhance(Context context, Item item) {

        try {

            validateMetadataFields(originalMetadataFields, copiedMetadataFields);

            List<MetadataValue> relationMetadataValues =
                itemService.getMetadataByMetadataString(item, relationMetadataField);

            if (CollectionUtils.isNotEmpty(relationMetadataValues)) {
                updateRelatedItem(context, item, relationMetadataValues);
            } else {
                updateRelatedItems(context, item);
            }

        } catch (SQLException | AuthorizeException e) {
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void validateMetadataFields(List<String> originalMetadataFields, List<String> copiedMetadataFields) {
        if (CollectionUtils.isEmpty(originalMetadataFields) ||
            CollectionUtils.isEmpty(copiedMetadataFields)) {
            throw new RuntimeException("original MetadataFields or copied MetadataFields can't be empty");
        }

        if (originalMetadataFields.size() != copiedMetadataFields.size()) {
            throw new RuntimeException("original and copied MetadataFields must contain the same number of metadata");
        }
    }

    private void updateRelatedItem(Context context, Item item,
                                   List<MetadataValue> metadataValues) throws SQLException, AuthorizeException {

        for (MetadataValue metadataValue : metadataValues) {
            Item relatedItem = findRelatedEntityItem(context, metadataValue);
            if (relatedItem != null) {
                updateItem(context, item, relatedItem, metadataValue.getPlace());
            }
        }

        clearMetadataIfNeeded(context, item, copiedMetadataFields, metadataValues.size());
    }

    private Item findRelatedEntityItem(Context context, MetadataValue metadataValue) {
        try {
            UUID relatedItemUUID = UUIDUtils.fromString(metadataValue.getAuthority());
            return relatedItemUUID != null ? itemService.find(context, relatedItemUUID) : null;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void clearMetadataIfNeeded(Context context, Item item,
                                       List<String> metadataFields, int size) throws SQLException {

        for (String metadataField : metadataFields) {

            if (size == getMetadataValuesCount(item, metadataField)) {
                continue;
            }

            clearMetadata(context, item, metadataField, size);
        }
    }

    private long getMetadataValuesCount(Item item, String metadataField) {
        return itemService.getMetadataByMetadataString(item, metadataField)
                          .stream()
                          .count();
    }

    private void clearMetadata(Context context, Item item, String metadataName, int startIndex) throws SQLException {

        MetadataFieldName mf = new MetadataFieldName(metadataName);

        List<MetadataValue> metadataValues =
            itemService.getMetadata(item, mf.schema, mf.element, mf.qualifier, Item.ANY);

        metadataValues = metadataValues.stream()
                                       .filter(metadataValue ->
                                           metadataValue.getPlace() >= startIndex)
                                       .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(metadataValues)) {
            itemService.removeMetadataValues(context, item, metadataValues);
        }
    }

    private void updateRelatedItems(Context context, Item item) throws SQLException, AuthorizeException {
        List<Item> relatedItems = findRelatedEntityItems(context, item);
        for (Item relatedItem : relatedItems) {
            List<MetadataValue> relationMetadataValues =
                itemService.getMetadataByMetadataString(relatedItem, relationMetadataField);

            if (CollectionUtils.isNotEmpty(relationMetadataValues)) {
                updateRelatedItem(context, relatedItem, relationMetadataValues);
            }
        }
    }

    private List<Item> findRelatedEntityItems(Context context, Item item) throws SQLException, AuthorizeException {

        List<Item> relatedItems = new ArrayList<>();

        Iterator<Item> itemIterator =
            itemService.findByMetadataFieldAuthority(context, relationMetadataField,item.getID().toString());

        itemIterator.forEachRemaining(relatedItems::add);

        return relatedItems.stream()
                           .filter(relatedItem -> isItemEntityTypeEqualTo(relatedItem, relatedEntityType))
                           .collect(Collectors.toList());
    }

    private boolean isItemEntityTypeEqualTo(Item item, String entityType) {
        return itemService.getEntityType(item).equals(entityType);
    }

    private void updateItem(Context context, Item item, Item relatedItem, int place) throws SQLException {

        for (int i = 0 ; i < originalMetadataFields.size() ; i++) {
            if (isUpdateMetadataNeeded(item, relatedItem,
                originalMetadataFields.get(i), copiedMetadataFields.get(i), place)) {
                updateMetadata(context, item, relatedItem,
                    originalMetadataFields.get(i), copiedMetadataFields.get(i), place);
            }
        }
    }

    private boolean isUpdateMetadataNeeded(Item item, Item relatedItem,
                                           String originalMetadataField,
                                           String copiedMetadataField, int place) {

        MetadataValue originalMetadataValue = getFirstMetadataValue(relatedItem, originalMetadataField);
        MetadataValue copiedMetadataValue = getMetadataValueAtPlace(item, copiedMetadataField, place);

        if (copiedMetadataValue != null && originalMetadataValue != null) {
            return !isMetadataValueEqual(copiedMetadataValue, originalMetadataValue);
        } else if (copiedMetadataValue != null && originalMetadataValue == null) {
            return !copiedMetadataValue.getValue().equals(PLACEHOLDER_PARENT_METADATA_VALUE);
        }

        return true;
    }

    private MetadataValue getMetadataValueAtPlace(Item item, String metadataField, int place) {
        return itemService.getMetadataByMetadataString(item, metadataField)
                          .stream()
                          .filter(metadataValue -> metadataValue.getPlace() == place)
                          .findFirst()
                          .orElse(null);
    }

    private MetadataValue getFirstMetadataValue(Item item, String metadataField) {
        return itemService.getMetadataByMetadataString(item, metadataField)
                          .stream()
                          .findFirst()
                          .orElse(null);
    }

    private boolean isMetadataValueEqual(MetadataValue sourceMetadataValue, MetadataValue relatedMetadataValue) {
        return StringUtils.equals(sourceMetadataValue.getValue(), relatedMetadataValue.getValue()) &&
            StringUtils.equals(sourceMetadataValue.getAuthority(), relatedMetadataValue.getAuthority()) &&
            sourceMetadataValue.getConfidence() == relatedMetadataValue.getConfidence();
    }

    private void updateMetadata(Context context, Item item, Item relatedItem, String originalMetadataField,
                                String copiedMetadataField, int place) throws SQLException {

        MetadataValue originalMetadataValue = getFirstMetadataValue(relatedItem, originalMetadataField);
        MetadataValue copiedMetadataValue = getMetadataValueAtPlace(item, copiedMetadataField, place);

        if (copiedMetadataValue != null && originalMetadataValue != null) {
            replaceMetadata(context, item, originalMetadataValue, copiedMetadataValue, place);
        } else if (copiedMetadataValue != null && originalMetadataValue == null) {
            replaceMetadata(context, item, copiedMetadataValue, PLACEHOLDER_PARENT_METADATA_VALUE, place);
        } else if (copiedMetadataValue == null && originalMetadataValue != null) {
            addMetadata(context, item, originalMetadataValue, copiedMetadataField, place);
        } else {
            addMetadata(context, item, copiedMetadataField, PLACEHOLDER_PARENT_METADATA_VALUE, place);
        }
    }

    private void replaceMetadata(Context context, Item item,
                                 MetadataValue originalMetadataValue,
                                 MetadataValue copiedMetadataValue, int place) throws SQLException {

        String metadataField = copiedMetadataValue.getMetadataField().toString('.');
        itemService.removeMetadataValues(context, item, List.of(copiedMetadataValue));
        addMetadata(context, item, originalMetadataValue, metadataField, place);
    }

    private void replaceMetadata(Context context, Item item,
                                 MetadataValue copiedMetadataValue, String value, int place) throws SQLException {

        String metadataField = copiedMetadataValue.getMetadataField().toString('.');
        itemService.removeMetadataValues(context, item, List.of(copiedMetadataValue));
        addMetadata(context, item, metadataField, value, place);
    }

    private void addMetadata(Context context, Item item,
                             MetadataValue mValue, String metadataName, int place) throws SQLException {

        MetadataFieldName mf = new MetadataFieldName(metadataName);
        itemService.addMetadata(context, item, mf.schema, mf.element, mf.qualifier, mValue.getLanguage(),
            mValue.getValue(), mValue.getAuthority(), mValue.getConfidence(), place);
    }

    private void addMetadata(Context context, Item item,
                             String metadataField, String value, int place) throws SQLException {

        MetadataFieldName mf = new MetadataFieldName(metadataField);
        itemService.addMetadata(context, item, mf.schema, mf.element, mf.qualifier, null, value, null, -1, place);
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

    public String getRelationMetadataField() {
        return relationMetadataField;
    }

    public void setRelationMetadataField(String relationMetadataField) {
        this.relationMetadataField = relationMetadataField;
    }

    public List<String> getOriginalMetadataFields() {
        return originalMetadataFields;
    }

    public void setOriginalMetadataFields(List<String> originalMetadataFields) {
        this.originalMetadataFields = originalMetadataFields;
    }

    public List<String> getCopiedMetadataFields() {
        return copiedMetadataFields;
    }

    public void setCopiedMetadataFields(List<String> copiedMetadataFields) {
        this.copiedMetadataFields = copiedMetadataFields;
    }
}