/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DCDate;
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
public class ProjectDatesEnhancer implements ItemEnhancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDatesEnhancer.class);

    @Autowired
    private ItemService itemService;

    private String sourceEntityType;

    private String targetEntityType;

    private String relationMetadataField;

    private String sourceStartDateMetadataField;
    private String sourceEndDateMetadataField;

    private String targetStartDateMetadataField;
    private String targetEndDateMetadataField;
    private String targetDurationMetadataField;

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

            validateItemEntityType(targetItem);

            updateTargetItemDates(context, targetItem, findRelatedEntityItems(context, targetItem));

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

    private List<Item> findRelatedEntityItems(Context context, Item item) throws SQLException, AuthorizeException {

        List<Item> relatedItems = new ArrayList<>();

        Iterator<Item> itemIterator =
            itemService.findByMetadataFieldAuthority(context, relationMetadataField,item.getID().toString());

        itemIterator.forEachRemaining(relatedItems::add);

        return relatedItems.stream()
                           .filter(item1 -> isItemEntityTypeEqualTo(item1, sourceEntityType))
                           .collect(Collectors.toList());
    }

    private void updateTargetItemDates(Context context, Item targetItem, List<Item> relatedItems)
        throws SQLException, AuthorizeException {

        boolean dateUpdated = false;
        Date targetStartDate = getTargetDateByMetadataField(targetItem, targetStartDateMetadataField);
        Date targetEndDate = getTargetDateByMetadataField(targetItem, targetEndDateMetadataField);

        Date sourceStartDate = getMinStartDate(relatedItems);
        Date sourceEndDate = getMaxStartDate(relatedItems);

        if (isTargetStartDateUpdateNeeded(targetStartDate, sourceStartDate)) {
            updateTargetItemDate(context, targetItem, sourceStartDate, targetStartDateMetadataField);
            dateUpdated = true;
        }
        if (isTargetEndDateUpdateNeeded(targetEndDate, sourceEndDate)) {
            updateTargetItemDate(context, targetItem, sourceEndDate, targetEndDateMetadataField);
            dateUpdated = true;
        }
        if (dateUpdated) {
            updateTargetItemDuration(context, targetItem, sourceStartDate, sourceEndDate);
        }
    }

    private Date getTargetDateByMetadataField(Item item, String metadataField) {

        MetadataValue metadataValue = getFirstMetadataValue(item, metadataField);

        return metadataValue != null && !metadataValue.getValue().isEmpty() ?
            new DCDate(metadataValue.getValue()).toDate() : null;
    }

    private Date getMinStartDate(List<Item> relatedItems) {

        return relatedItems.stream()
                           .map(item -> getFirstMetadataValue(item, sourceStartDateMetadataField))
                           .filter(metadataValue -> metadataValue != null && !metadataValue.getValue().isEmpty())
                           .map(metadataValue -> new DCDate(metadataValue.getValue()).toDate())
                           .min(Date::compareTo).orElse(null);
    }

    private Date getMaxStartDate(List<Item> relatedItems) {

        return relatedItems.stream()
                           .map(item -> getFirstMetadataValue(item, sourceEndDateMetadataField))
                           .filter(metadataValue -> metadataValue != null && !metadataValue.getValue().isEmpty())
                           .map(metadataValue -> new DCDate(metadataValue.getValue()).toDate())
                           .max(Date::compareTo).orElse(null);
    }

    private boolean isTargetStartDateUpdateNeeded(Date targetStartDate, Date sourceStartDate) {

        if (targetStartDate != null && sourceStartDate != null) {
            return sourceStartDate.compareTo(targetStartDate) < 0;
        } else {
            return targetStartDate == null && sourceStartDate != null;
        }
    }

    private boolean isTargetEndDateUpdateNeeded(Date targetEndDate, Date sourceEndDate) {

        if (targetEndDate != null && sourceEndDate != null) {
            return sourceEndDate.compareTo(targetEndDate) > 0;
        } else {
            return targetEndDate == null && sourceEndDate != null;
        }
    }

    private void updateTargetItemDate(Context context, Item item, Date date, String metadataField)
        throws SQLException, AuthorizeException {

        SimpleDateFormat dateIso = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateIso.format(date);

        updateMetadata(context, item, dateStr, metadataField);
    }

    private void updateTargetItemDuration(Context context, Item item, Date date1, Date date2)
        throws SQLException, AuthorizeException {

        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long duration = ChronoUnit.MONTHS.between(YearMonth.from(localDate1), YearMonth.from(localDate2));

        updateMetadata(context, item, String.valueOf(duration), targetDurationMetadataField);
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

    private void addMetadata(Context context, Item item, String date, String metadataField)
        throws SQLException, AuthorizeException {

        String[] fields = getElements(metadataField);
        itemService.addMetadata(context, item, fields[0], fields[1], fields[2], null, date);
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

    public String getSourceStartDateMetadataField() {
        return sourceStartDateMetadataField;
    }

    public void setSourceStartDateMetadataField(String sourceStartDateMetadataField) {
        this.sourceStartDateMetadataField = sourceStartDateMetadataField;
    }

    public String getSourceEndDateMetadataField() {
        return sourceEndDateMetadataField;
    }

    public void setSourceEndDateMetadataField(String sourceEndDateMetadataField) {
        this.sourceEndDateMetadataField = sourceEndDateMetadataField;
    }

    public String getTargetStartDateMetadataField() {
        return targetStartDateMetadataField;
    }

    public void setTargetStartDateMetadataField(String targetStartDateMetadataField) {
        this.targetStartDateMetadataField = targetStartDateMetadataField;
    }

    public String getTargetEndDateMetadataField() {
        return targetEndDateMetadataField;
    }

    public void setTargetEndDateMetadataField(String targetEndDateMetadataField) {
        this.targetEndDateMetadataField = targetEndDateMetadataField;
    }

    public String getTargetDurationMetadataField() {
        return targetDurationMetadataField;
    }

    public void setTargetDurationMetadataField(String targetDurationMetadataField) {
        this.targetDurationMetadataField = targetDurationMetadataField;
    }
}