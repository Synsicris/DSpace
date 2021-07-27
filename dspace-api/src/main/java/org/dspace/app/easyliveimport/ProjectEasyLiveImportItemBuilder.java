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
import org.dspace.content.authority.Choices;
import org.dspace.content.service.ItemService;
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
                MetadataFieldConfig metadataField = xPathManagerToMetadataField.get(xPathManager);
                itemService.replaceMetadata(context, item, metadataField.getSchema(), metadataField.getElement(),
                            metadataField.getQualifier(), null, value, null, Choices.CF_UNSET, 0);
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