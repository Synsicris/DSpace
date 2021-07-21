/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportConcatValuesXPath extends EasyOnlineImportXPath {

    private List<String> paths;

    @Override
    public String getValue(Document document) {
        StringBuilder result = new StringBuilder();
        try {
            for (String path : paths) {
                NodeList nodes = extractNodes(path, document);
                if (nodes.getLength() > 0) {
                    if (StringUtils.isBlank(result.toString())) {
                        result.append(nodes.item(0).getNodeValue());
                    } else {
                        result.append(", " + nodes.item(0).getNodeValue());
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

}