/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.sql.SQLException;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.authority.Choice;
import org.dspace.content.authority.ChoiceAuthority;
import org.dspace.content.authority.Choices;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectEasyLiveImportItemBuilder implements EasyImportItemBuilder {

    private Map<EasyOnlineImportXPath, MetadataFieldConfig> xPathManagerToMetadataField;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ChoiceAuthorityService cas;

    public ProjectEasyLiveImportItemBuilder(
            Map<EasyOnlineImportXPath, MetadataFieldConfig> xPathManagerToMetadataField) {
        this.xPathManagerToMetadataField = xPathManagerToMetadataField;
    }

    @Override
    public void updateItem(Context context, Item item, Document document)
            throws SQLException, XPathExpressionException {
        for (EasyOnlineImportXPath xPathManager : xPathManagerToMetadataField.keySet()) {
            String value = cleanText(xPathManager.getValue(document));
            if (StringUtils.isNotBlank(value)) {
                String authorityValue = null;
                MetadataFieldConfig metadataField = xPathManagerToMetadataField.get(xPathManager);
                String authorityName =
                    cas.getChoiceAuthorityName(
                        metadataField.getSchema(), metadataField.getElement(),
                        metadataField.getQualifier(), Constants.ITEM, item.getOwningCollection()
                    );
                if (StringUtils.isNotBlank(authorityName)) {
                    ChoiceAuthority authority = cas.getChoiceAuthorityByAuthorityName(authorityName);
                    Choices choices = authority.getBestMatch(value, context.getCurrentLocale().toString());
                    if (choices.values.length > 0) {
                        Choice choice = choices.values[0];
                        value = choice.value;
                        if (StringUtils.isNotBlank(choice.authority)) {
                            authorityValue = authorityName + ":" + choice.authority;
                        }
                    }
                }
                itemService.replaceMetadata(context, item, metadataField.getSchema(), metadataField.getElement(),
                            metadataField.getQualifier(), null, value, authorityValue,
                            StringUtils.isNotBlank(authorityValue) ? Choices.CF_ACCEPTED : Choices.CF_UNSET , 0);
            }
        }
    }

    private String cleanText(String value) {
        if (value.contains("\n")) {
            value = value.replaceAll("\n", " ");
        }
        if (value.contains("\t")) {
            value = value.replaceAll("\t", "");
        }
        return value.trim();
    }

}