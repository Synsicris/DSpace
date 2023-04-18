/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture;

public class CapturableScreen {

    private String url;
    private String token;
    private String cookie;
    private CapturableScreenConfiguration configuration;

    public CapturableScreen(
        CapturableScreenConfiguration configuration, String url, String token, String cookie
    ) {
        this.configuration = configuration;
        this.url = url;
        this.token = token;
        this.cookie = cookie;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

}
