/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.sql.SQLException;
import javax.xml.xpath.XPathExpressionException;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.w3c.dom.Document;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public interface EasyImportItemBuilder {

    public void updateItem(Context context, Item item, Document document) throws SQLException, XPathExpressionException;

}