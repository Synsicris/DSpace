/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.model;

import java.util.Map;

import org.dspace.app.capture.mapper.CapturableScreenHeaderValueMapper;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.capture.service.factory.CaptureWebsiteServiceFactory;
import org.dspace.core.Context;

public class CapturableScreenBuilder {

    private CaptureWebsiteService capturableWebsiteService =
        CaptureWebsiteServiceFactory.getInstance().getCaptureWebsiteService();

    private Context context;
    private CapturableScreen capturableScreen;

    protected CapturableScreenBuilder(Context context) {
        this.context = context;
    }

    public static CapturableScreenBuilder createCapturableScreen(
        Context context, CapturableScreenConfiguration configuration
    ) {
        CapturableScreenBuilder builder = new CapturableScreenBuilder(context);
        return builder.create(configuration);
    }

    private CapturableScreenBuilder create(CapturableScreenConfiguration configuration) {
        this.capturableScreen = new CapturableScreen(configuration);
        return this;
    }

    public CapturableScreenBuilder withCookie(String cookie) {
        this.capturableScreen.setCookie(cookie);
        return this;
    }

    public CapturableScreenBuilder withUrl(String url) {
        this.capturableScreen.setUrl(url);
        return this;
    }

    public CapturableScreenBuilder withHeadersConfiguration(
        Map<String, CapturableScreenHeaderValueMapper> headersConfiguration
    ) {
        this.capturableScreen
            .getConfiguration()
            .setHeadersConfiguration(headersConfiguration);
        return this;
    }

    public CapturableScreen build() {
        return this.capturableScreen;
    }

    public CapturableScreenBuilder computeHeaders() {
        this.capturableScreen.setHeaders(
            capturableWebsiteService.computeHeader(
                this.context,
                this.capturableScreen.getConfiguration()
            )
                .toString()
        );
        return this;
    }

}
