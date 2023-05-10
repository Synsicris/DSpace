/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.model;

public class CapturableScreen {

    private String url;
    private String cookie;
    private String headers;
    private CapturableScreenConfiguration configuration;

    protected CapturableScreen(
        CapturableScreenConfiguration configuration
    ) {
        super();
        this.configuration = configuration;
    }

    public CapturableScreen(
        CapturableScreenConfiguration configuration, String url, String cookie, String headers
    ) {
        this(configuration);
        this.url = url;
        this.cookie = cookie;
        this.headers = headers;
    }

    public CapturableScreenConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CapturableScreenConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

}
