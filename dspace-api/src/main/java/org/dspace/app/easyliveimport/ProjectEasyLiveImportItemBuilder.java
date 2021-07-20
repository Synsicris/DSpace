/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectEasyLiveImportItemBuilder implements EasyImportItemBuilder {

    private static Logger log = LogManager.getLogger(ProjectEasyLiveImportItemBuilder.class);

    private Map<String, MetadataFieldConfig> tagToMetadataField;

    @Autowired
    private ItemService itemService;

    public ProjectEasyLiveImportItemBuilder(Map<String, MetadataFieldConfig> tagToMetadataField) {
        this.tagToMetadataField = tagToMetadataField;
    }

    @Override
    public void updateItem(Context context, Item item, Document document) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        setNamespace(xpath);
        try {
            for (String path : tagToMetadataField.keySet()) {
                XPathExpression expr = xpath.compile(path);
                NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                if (nodes.getLength() == 1) {
                    String value = nodes.item(0).getNodeValue();
                    MetadataFieldConfig f = tagToMetadataField.get(path);
                    itemService.replaceMetadata(context, item, f.getSchema(), f.getElement(), f.getQualifier(),
                                                null, value, null, Choices.CF_UNSET, 0);
                }
            }
        } catch (XPathExpressionException | SQLException e) {
            //TODO
        }
    }

    private void setNamespace(XPath xpath) {
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                return prefix.equals("xf") ? "http://ip.kp.dlr.de/Xfoerder" : null;
            }

            public Iterator<String> getPrefixes(String val) {
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }
        });
    }

    public Map<String, MetadataFieldConfig> getTagToMetadataField() {
        return tagToMetadataField;
    }

    public void setTagToMetadataField(Map<String, MetadataFieldConfig> tagToMetadataField) {
        this.tagToMetadataField = tagToMetadataField;
    }

}