/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.saveservice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.dspace.app.versioning.CaptureScreenAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Service interface class for the CapturedStream (screen shot) objects.
 * The implementation of this class is responsible for business logic
 * that allows managing the CapturedStream into a target item
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public interface CapturedStreamSaveService {

    /**
     * Save bitstream using configuration of CaptureScreenAction
     * 
     * @param context              DSpace context object
     * @param is                   InputStream
     * @param csa                  CaptureScreenAction object
     * @throws IOException         If IO error
     * @throws SQLException        If database error
     * @throws AuthorizeException  If authorization error
     */
    public void saveScreenIntoItem(Context context, InputStream is, CaptureScreenAction<?> csa)
            throws IOException, SQLException, AuthorizeException;

    /**
     * Delete all bitstreams of target item by provided bundle name
     * 
     * @param context              DSpace context object
     * @param targetItem           Item whose bundles are to be deleted
     * @param bundleName           Name of the bundle to be deleted
     * @throws SQLException        If database error
     * @throws AuthorizeException  If authorization error
     * @throws IOException         If IO error
     */
    public void deleteAllBitstreamFromTargetBundle(Context context, Item targetItem, String bundleName)
            throws SQLException, AuthorizeException, IOException;

}
