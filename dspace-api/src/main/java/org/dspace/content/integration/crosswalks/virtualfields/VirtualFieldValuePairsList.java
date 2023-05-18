/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.virtualfields;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link VirtualField} that elaborate a value-pairs list
 * originated metadata to take the label related to the stored-value.
 * (Example: @virtual.value_pair.metadataField.ValuePairListName@)
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class VirtualFieldValuePairsList implements VirtualField {

    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualFieldValuePairsList.class);

    private ItemService itemService;

    public VirtualFieldValuePairsList(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public String[] getMetadata(Context context, Item item, String fieldName) {
        String[] virtualFieldName = fieldName.split("\\.", 4);

        if (virtualFieldName.length != 4) {
            LOGGER.warn("Invalid value-pairs virtual field: " + fieldName);
            return new String[] {};
        }

        String metadataField = virtualFieldName[2].replaceAll("-", ".");
        String nameOfValuePairsList = virtualFieldName[3];

        return itemService.getMetadataByMetadataString(item, metadataField).stream()
                          .filter(metadataValue -> metadataValue.getValue() != null)
                          .flatMap(metadataValue ->
                                   getLabelValue(context, metadataValue.getValue(), nameOfValuePairsList).stream())
                          .toArray(String[]::new);
    }

    private Optional<String> getLabelValue(Context context, String value, String nameOfValuePairsList) {
        String label = EMPTY;
        try {
            DCInputsReader reader = getDCInputReaderByLocale(context);
            // Holds display/storage pairs
            List<String> valuePairsList = reader.getPairs(nameOfValuePairsList);
            label = getLabel(value, valuePairsList);
        } catch (DCInputsReaderException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return StringUtils.isNotBlank(label) ? Optional.of(label) : Optional.empty();
    }

    private DCInputsReader getDCInputReaderByLocale(Context context) throws DCInputsReaderException {
        Locale currentLocale = context.getCurrentLocale();
        Locale defaultLocale = I18nUtil.getDefaultLocale();
        return currentLocale.equals(defaultLocale) ? new DCInputsReader()
                                                   : new DCInputsReader(I18nUtil.getInputFormsFileName(currentLocale));
    }

    private String getLabel(String value, List<String> valuePairsList) {
        for (int i = 0 ; i < valuePairsList.size(); i++) {
            if (StringUtils.equals(value, valuePairsList.get(i))) {
                return valuePairsList.get(i - 1);
            }
        }
        return EMPTY;
    }

}
