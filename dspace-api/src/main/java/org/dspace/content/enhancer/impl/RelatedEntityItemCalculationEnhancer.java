/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
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
 * Implementation of {@link ItemEnhancer} that update metadata values of items that its type equal to targetEntityType
 * and get information from related items that its type equal to sourceEntityType
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
public class RelatedEntityItemCalculationEnhancer implements ItemEnhancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedEntityItemCalculationEnhancer.class);

    @Autowired
    private ItemService itemService;

    private String sourceEntityType;

    private String targetEntityType;

    private String relationMetadataField;

    private String sourceAmountMetadataField;

    private String sourceCurrencyMetadataField;

    private String targetAmountMetadataField;

    private String targetCurrencyMetadataField;

    @Override
    public boolean canEnhance(Context context, Item item) {
        return sourceEntityType == null || sourceEntityType.equals(itemService.getEntityType(item));
    }

    @Override
    public void enhance(Context context, Item item) {

        try {

            MetadataValue relationMetadataValue = getFirstMetadataValue(item, relationMetadataField);

            if (relationMetadataValue == null) {
                return;
            }

            Item targetItem = findRelatedEntityItem(context, relationMetadataValue);

            if (targetItem == null) {
                return;
            }

            validateItem(targetItem);

            updateTargetItemAmount(context, targetItem, findRelatedEntityItems(context, targetItem));

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
        validateItemCurrency(item);
    }

    private void validateItemEntityType(Item item) {
        if (!isItemEntityTypeEqualTo(item, targetEntityType)) {
            String errorMessage = "item:" + item.getID() + " entity type not equal to " + targetEntityType;
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private boolean isItemEntityTypeEqualTo(Item item, String entityType) {
        MetadataValue metadataValue = getFirstMetadataValue(item, "dspace.entity.type");
        return metadataValue != null ? metadataValue.getValue().equals(entityType) : false;
    }

    private void validateItemCurrency(Item item) {

        String currency = getItemCurrency(item, targetCurrencyMetadataField);

        if (currency == null) {
            String errorMessage = "item:" + item.getID() + " doesn't contain currency";
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private List<Item> findRelatedEntityItems(Context context, Item item) throws SQLException, AuthorizeException {

        List<Item> relatedItems = new ArrayList<>();

        Iterator<Item> itemIterator =
            itemService.findByMetadataFieldAuthority(context, relationMetadataField,item.getID().toString());

        itemIterator.forEachRemaining(relatedItems::add);

        return relatedItems.stream()
                           .filter(item1 -> isItemEntityTypeEqualTo(item1, sourceEntityType))
                           .collect(Collectors.toList());
    }

    private void updateTargetItemAmount(Context context, Item targetItem, List<Item> relatedItems)
        throws SQLException, AuthorizeException {

        String targetCurrency = getItemCurrency(targetItem, targetCurrencyMetadataField);
        boolean currencyEqual = isRelatedItemsCurrencyEqualTo(relatedItems, targetCurrency);

        int amount = getSumOfAmount(relatedItems);
        boolean updateNeeded = isUpdateAmountNeeded(targetItem, amount);

        if (currencyEqual && updateNeeded) {
            updateMetadata(context, targetItem, String.valueOf(amount), targetAmountMetadataField);
        }
    }

    private String getItemCurrency(Item item, String metadataField) {
        MetadataValue metadataValue = getFirstMetadataValue(item, metadataField);
        return metadataValue != null ? metadataValue.getValue() : null;
    }

    private boolean isRelatedItemsCurrencyEqualTo(List<Item> items, String targetCurrency) {

        int size = items.size();
        int filteredSize = items.stream()
                                .map(item -> getItemCurrency(item, sourceCurrencyMetadataField))
                                .filter(currency -> currency != null)
                                .filter(currency -> currency.equals(targetCurrency))
                                .collect(Collectors.toList())
                                .size();

        return size == filteredSize;
    }

    private int getSumOfAmount(List<Item> items) {

        return items.stream()
                    .map(item -> getFirstMetadataValue(item, sourceAmountMetadataField))
                    .filter(metadataValue -> metadataValue != null && !TextUtils.isEmpty(metadataValue.getValue()))
                    .map(metadataValue -> parseInteger(metadataValue.getValue(), metadataValue.getDSpaceObject()))
                    .reduce(Integer::sum).orElse(0);
    }

    private boolean isUpdateAmountNeeded(Item targetItem, int amount) {
        MetadataValue metadataValue = getFirstMetadataValue(targetItem, targetAmountMetadataField);
        if (metadataValue != null && !TextUtils.isEmpty(metadataValue.getValue())) {
            return parseInteger(metadataValue.getValue(), metadataValue.getDSpaceObject()) != amount;
        }
        return true;
    }

    private int parseInteger(String value, DSpaceObject item) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            String errorMessage = "item:" + item.getID() + " contains incorrect amount value";
            LOGGER.error("An error occurs enhancing item with id {}: {}", item.getID(), errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private void updateMetadata(Context context, Item item, String value, String metadataField)
        throws SQLException, AuthorizeException {

        MetadataValue metadataValue = getFirstMetadataValue(item, metadataField);

        if (metadataValue != null) {
            clearAndAddMetadata(context, item, value, metadataValue);
        } else {
            addMetadata(context, item, value, metadataField);
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

    public String getTargetEntityType() {
        return targetEntityType;
    }

    public void setTargetEntityType(String targetEntityType) {
        this.targetEntityType = targetEntityType;
    }

    public String getRelationMetadataField() {
        return relationMetadataField;
    }

    public void setRelationMetadataField(String relationMetadataField) {
        this.relationMetadataField = relationMetadataField;
    }

    public String getSourceAmountMetadataField() {
        return sourceAmountMetadataField;
    }

    public void setSourceAmountMetadataField(String sourceAmountMetadataField) {
        this.sourceAmountMetadataField = sourceAmountMetadataField;
    }

    public String getSourceCurrencyMetadataField() {
        return sourceCurrencyMetadataField;
    }

    public void setSourceCurrencyMetadataField(String sourceCurrencyMetadataField) {
        this.sourceCurrencyMetadataField = sourceCurrencyMetadataField;
    }

    public String getTargetAmountMetadataField() {
        return targetAmountMetadataField;
    }

    public void setTargetAmountMetadataField(String targetAmountMetadataField) {
        this.targetAmountMetadataField = targetAmountMetadataField;
    }

    public String getTargetCurrencyMetadataField() {
        return targetCurrencyMetadataField;
    }

    public void setTargetCurrencyMetadataField(String targetCurrencyMetadataField) {
        this.targetCurrencyMetadataField = targetCurrencyMetadataField;
    }
}