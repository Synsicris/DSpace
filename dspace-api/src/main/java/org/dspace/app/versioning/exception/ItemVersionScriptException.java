/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.exception;

import org.dspace.app.versioning.ItemVersionScript;

/**
 * Exception that could occur in the {@link ItemVersionScript} process.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ItemVersionScriptException extends Exception {

    private static final long serialVersionUID = -84347073313040112L;

    public static final int MISSING_ITEM_UUID = 0b01;
    public static final int WORKSPACE_FOUND = 0b10;
    public static final int WOFKFLOW_FOUND = 0b100;

    private int reason;

    public ItemVersionScriptException(int r, String message) {
        super(message);
        reason = r;
    }

    public int getReason() {
        return reason;
    }

}
