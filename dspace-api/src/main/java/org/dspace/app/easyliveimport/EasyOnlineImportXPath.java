/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public abstract class EasyOnlineImportXPath {

    public abstract String getValue(Document document) throws XPathExpressionException;

    protected NodeList extractNodes(String path, Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        setNamespace(xpath);
        XPathExpression expr = xpath.compile(path);
        return (NodeList) expr.evaluate(document, XPathConstants.NODESET);
    }

    protected void setNamespace(XPath xpath) {
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
}