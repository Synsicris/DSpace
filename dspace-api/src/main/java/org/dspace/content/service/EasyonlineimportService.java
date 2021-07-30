/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.service;
import java.sql.SQLException;
import javax.xml.xpath.XPathExpressionException;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.w3c.dom.Document;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public interface EasyonlineimportService {

    public void importFile(Context context, Item item, Document document, String entityType)
            throws SQLException, XPathExpressionException;

}