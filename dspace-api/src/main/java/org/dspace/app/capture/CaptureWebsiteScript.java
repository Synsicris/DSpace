/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture;

import static org.dspace.app.capture.CaptureWebsiteProperties.DEFAULT_SCALE_FACTOR;
import static org.dspace.app.capture.CaptureWebsiteProperties.DEFAULT_TYPE;
import static org.dspace.app.capture.exception.CaptureWebsiteException.INVALID_COOKIE;
import static org.dspace.app.capture.exception.CaptureWebsiteException.INVALID_TOKEN;
import static org.dspace.app.capture.exception.CaptureWebsiteException.INVALID_URL;
import static org.dspace.app.capture.mapper.SimpleHeaderValueMappers.bearerToken;

import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.capture.exception.CaptureWebsiteException;
import org.dspace.app.capture.mapper.CapturableScreenHeaderValueMapper;
import org.dspace.app.capture.model.CapturableScreenBuilder;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.capture.service.factory.CaptureWebsiteServiceFactory;
import org.dspace.core.Context;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.utils.DSpace;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CaptureWebsiteScript extends DSpaceRunnable<CaptureWebsiteScriptConfiguration<CaptureWebsiteScript>> {

    protected ConfigurationService configurationService =
        DSpaceServicesFactory.getInstance().getConfigurationService();
    protected CaptureWebsiteService captureWebsiteService =
        CaptureWebsiteServiceFactory.getInstance().getCaptureWebsiteService();

    private boolean isHelp;
    private String url;
    private String token;
    private String cookie;
    private String remove;
    private String element;
    private String style;

    @Override
    public CaptureWebsiteScriptConfiguration<CaptureWebsiteScript> getScriptConfiguration() {
        return new DSpace()
                .getServiceManager()
                .getServiceByName("capture-website", CaptureWebsiteScriptConfiguration.class);
    }

    @Override
    public void setup() throws ParseException {
        isHelp = commandLine.hasOption("h");
        url = commandLine.getOptionValue("r");
        token = commandLine.getOptionValue("t");
        cookie = commandLine.getOptionValue("c");
        remove = commandLine.getOptionValue("x");
        element = commandLine.getOptionValue("e");
        style = commandLine.getOptionValue("s");

    }

    @Override
    public void internalRun() throws Exception {

        if (isHelp) {
            printHelp();
            return;
        }

        validate();

        Context context = new Context();
        context.turnOffAuthorisationSystem();

        try {
            process(context);
        } catch (Exception e) {
            context.restoreAuthSystemState();
            context.abort();
            throw e;
        } finally {
            context.restoreAuthSystemState();
            context.complete();
        }

        context.restoreAuthSystemState();
    }

    private void validate() throws CaptureWebsiteException {
        if (
            StringUtils.isBlank(url) || StringUtils.isEmpty(url)
        ) {
            handler.logError("URL not found or invalid!");
            throw new CaptureWebsiteException(
                INVALID_URL,
                "URL not found or invalid!"
            );
        }
        if (
            StringUtils.isBlank(token) || StringUtils.isEmpty(token)
            ) {
            handler.logError("Token not found or invalid!");
            throw new CaptureWebsiteException(
                INVALID_TOKEN,
                "Token not found or invalid!"
            );
        }
        if (
            StringUtils.isBlank(cookie) || StringUtils.isEmpty(cookie)
            ) {
            handler.logError("Cookie not found or invalid!");
            throw new CaptureWebsiteException(
                INVALID_COOKIE,
                "Cookie not found or invalid!"
            );
        }
    }

    private void process(Context context) throws Exception {
        this.captureWebsiteService.takeScreenshot(
            context,
            CapturableScreenBuilder
                .createCapturableScreen(
                    context,
                    new CapturableScreenConfiguration(
                        element, remove, style,
                        DEFAULT_TYPE, DEFAULT_SCALE_FACTOR,
                        getDefaultHeaders(token)
                    )
                )
                .withCookie(cookie)
                .withUrl(url)
                .computeHeaders()
                .build()
        );
    }

    public Map<String, CapturableScreenHeaderValueMapper> getDefaultHeaders(String token) {
        return Map.of(
            "Authorization",
            bearerToken(token)
        );
    }

}
