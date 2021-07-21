/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.dspace.util.SimpleMapConverter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportMapConverterXPath extends EasyOnlineImportXPath {

    private String path;

    private SimpleMapConverter simpleMapConverter;

    @Override
    public String getValue(Document document) {
        try {
            NodeList nodes = extractNodes(path, document);
            if (nodes.getLength() > 0) {
                String value = nodes.item(0).getNodeValue();
                if (StringUtils.isNotBlank(value)) {
                    return simpleMapConverter.getValue(value);
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SimpleMapConverter getSimpleMapConverter() {
        return simpleMapConverter;
    }

    public void setSimpleMapConverter(SimpleMapConverter simpleMapConverter) {
        this.simpleMapConverter = simpleMapConverter;
    }

}