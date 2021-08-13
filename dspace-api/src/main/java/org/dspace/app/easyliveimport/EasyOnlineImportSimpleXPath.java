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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportSimpleXPath extends EasyOnlineImportXPath {

    private String path;

    @Override
    public String getValue(Document document) throws XPathExpressionException {
        NodeList nodes = extractNodes(path, document);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getNodeValue();
        }
        return StringUtils.EMPTY;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}