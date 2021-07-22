/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import javax.xml.xpath.XPathExpressionException;

import org.dspace.app.easyliveimport.EasyImportItemBuilder;
import org.dspace.content.service.EasyonlineimportService;
import org.dspace.core.Context;
import org.w3c.dom.Document;

public class EasyonlineimportServiceImpl implements EasyonlineimportService {

    private Map<String, EasyImportItemBuilder> entityType2itemBuilder;

    public EasyonlineimportServiceImpl(Map<String, EasyImportItemBuilder> entityType2itemBuilder) {
        this.entityType2itemBuilder = entityType2itemBuilder;
    }

    @Override
    public void importFile(Context context, Item item, Document document, String entityType)
            throws SQLException, XPathExpressionException {
        EasyImportItemBuilder easyImportItemBuilder = entityType2itemBuilder.get(entityType);
        if (Objects.nonNull(easyImportItemBuilder)) {
            easyImportItemBuilder.updateItem(context, item, document);
        }
    }

    public Map<String, EasyImportItemBuilder> getEntityType2itemBuilder() {
        return entityType2itemBuilder;
    }

    public void setEntityType2itemBuilder(Map<String, EasyImportItemBuilder> entityType2itemBuilder) {
        this.entityType2itemBuilder = entityType2itemBuilder;
    }

}