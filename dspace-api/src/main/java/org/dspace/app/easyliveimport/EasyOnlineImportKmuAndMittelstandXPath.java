/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.dspace.util.SimpleMapConverter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportKmuAndMittelstandXPath extends EasyOnlineImportXPath {

    private Map<String, String> fieldToPath;

    private SimpleMapConverter simpleMapConverter;

    @Override
    public String getValue(Document document) throws XPathExpressionException {
        NodeList kmuNodes = extractNodes(fieldToPath.get("KMU"), document);
        NodeList mittelstandNodes = extractNodes(fieldToPath.get("Mittelstand"), document);
        if (kmuNodes.getLength() == 0 && mittelstandNodes.getLength() == 0) {
            NodeList rechtsformNodes = extractNodes(fieldToPath.get("Rechtsform"), document);
            if (rechtsformNodes.getLength() > 0) {
                String value = rechtsformNodes.item(0).getNodeValue();
                if (StringUtils.isNotBlank(value)) {
                    return simpleMapConverter.getValue(value);
                }
            }
        }
        String value = StringUtils.EMPTY;
        if (kmuNodes.getLength() > 0) {
            value = kmuNodes.item(0).getNodeValue();
        } else {
            value = mittelstandNodes.item(0).getNodeValue();
        }
        return StringUtils.isNotBlank(value) ? simpleMapConverter.getValue(value) : value;
    }

    public Map<String, String> getFieldToPath() {
        return fieldToPath;
    }

    public void setFieldToPath(Map<String, String> fieldToPath) {
        this.fieldToPath = fieldToPath;
    }

    public SimpleMapConverter getSimpleMapConverter() {
        return simpleMapConverter;
    }

    public void setSimpleMapConverter(SimpleMapConverter simpleMapConverter) {
        this.simpleMapConverter = simpleMapConverter;
    }

}