/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.virtualfields;

import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.DCInputAuthority;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link VirtualField} that translates {@code value-pair}
 * and {@code vocabulary-fields} into displayable labels.
 * Internally uses the {@link ChoiceAuthorityService} to translate them.
 * <br/>
 * <br/>
 * (Example: {@code @virtual.value_pair.metadataField@})
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class VirtualFieldValuePairsList implements VirtualField {

    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualFieldValuePairsList.class);

    @Autowired
    private ItemService itemService;
    @Autowired
    private ChoiceAuthorityService choiceAuthorityService;

    @Override
    public String[] getMetadata(Context context, Item item, String fieldName) {
        String[] virtualFieldName = fieldName.split("\\.", 3);

        if (virtualFieldName.length != 3) {
            LOGGER.warn("Invalid value-pairs virtual field: " + fieldName);
            return new String[] {};
        }

        String metadataField = virtualFieldName[2].replaceAll("-", ".");
        Locale locale =
            Optional.ofNullable(context.getCurrentLocale())
                .orElse(I18nUtil.getDefaultLocale());

        return itemService.getMetadataByMetadataString(item, metadataField)
            .stream()
            .map(metadataValue -> getDisplayableLabel(item, metadataValue, locale.getLanguage()))
            .toArray(String[]::new);
    }

    protected String getDisplayableLabel(Item item, MetadataValue metadataValue, String language) {
        return getLabelForVocabulary(item, metadataValue, language)
            .or(() -> getLabelForValuePair(item, metadataValue, language))
            .orElse(metadataValue.getValue());
    }

    private Optional<String> getLabelForVocabulary(Item item, MetadataValue metadataValue, String language) {
        return getValidLabel(
            Optional.ofNullable(metadataValue)
            .filter(mv -> StringUtils.isNotBlank(mv.getAuthority()))
            .map(mv -> getVocabulary(item, mv, language))
        );
    }

    private Optional<String> getLabelForValuePair(Item item, MetadataValue metadataValue, String language) {
        return getValidLabel(
            Optional.ofNullable(metadataValue)
                .filter(mv -> StringUtils.isNotBlank(mv.getValue()))
                .map(mv -> getValuePair(item, mv, language))
        );
    }

    private String getVocabulary(Item item, MetadataValue metadataValue, String language) {
        try {
            return this.choiceAuthorityService
                .getLabel(
                    metadataValue, item.getType(),
                    item.getOwningCollection(), language
                );
        } catch (Exception e) {
            LOGGER.warn("Error while retrieving the vocabulary for: " +
                metadataValue.getMetadataField().toString(), e
            );
        }
        return null;
    }


    private String getValuePair(Item item, MetadataValue metadataValue, String language) {
        try {
            return this.choiceAuthorityService
                .getLabel(
                    metadataValue.getMetadataField().toString(), item.getType(),
                    item.getOwningCollection(), metadataValue.getValue(), language
                );
        } catch (Exception e) {
            LOGGER.warn(
                "Error while retrievingthe value-pair for: " +
                    metadataValue.getMetadataField().toString(),
                e
            );
        }
        return null;
    }

    private Optional<String> getValidLabel(Optional<String> label) {
        return label.filter(this::isValidLabel);
    }

    private boolean isValidLabel(String s) {
        return s != null && !s.contains(DCInputAuthority.UNKNOWN_KEY);
    }

}
