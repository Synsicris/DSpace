/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents.service;

import java.sql.SQLException;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.external.provider.impl.LiveImportDataProvider;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public interface UpdatePatentService {

    public boolean updatePatent(Context context, Item item, LiveImportDataProvider liveImportDataProvider)
            throws SQLException;

}