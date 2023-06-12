/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.exception;

public class CaptureWebsiteException extends Exception {
    public static final int INVALID_URL = 0b1;
    public static final int INVALID_TOKEN = 0b10;
    public static final int INVALID_COOKIE = 0b100;

    private int reason;

    public CaptureWebsiteException(int r, String message) {
        super(message);
        reason = r;
    }

    public int getReason() {
        return reason;
    }
}
