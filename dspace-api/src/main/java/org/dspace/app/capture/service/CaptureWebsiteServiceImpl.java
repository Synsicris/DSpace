/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service;

import static org.dspace.app.capture.CaptureWebsiteProperties.CAPTURE_WEBSITE_NODE_PATH;
import static org.dspace.app.capture.CaptureWebsiteProperties.CAPTURE_WEBSITE_PATH;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.dspace.app.capture.CaptureWebsiteProperties;
import org.dspace.app.capture.mapper.CapturableScreenHeaderValueMapper;
import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

public class CaptureWebsiteServiceImpl implements CaptureWebsiteService {

    private Map<String, String> extensionToMimeType;

    @Autowired
    protected ConfigurationService configurationService;

    private boolean isWindows() {
        return System.getProperty("os.name")
            .toLowerCase().startsWith("windows");
    }

    /* (non-Javadoc)
     * @see org.dspace.app.capture.service.CaptureWebsiteService#takeScreenshot(org.dspace.app.capture.CapturableScreen)
     */
    @Override
    public void takeScreenshot(Context c, CapturableScreen capturableScreen) throws Exception {
        ProcessBuilder procBuilder = new ProcessBuilder();
        String command = buildCommand(c, capturableScreen);
        boolean isWindows = this.isWindows();
        if (isWindows) {
            procBuilder.command("cmd.exe", "/c", command);
        } else {
            procBuilder.command("/bin/bash", "-c", command);
        }
        procBuilder.directory(new File(System.getProperty("user.home")));
        Process process = procBuilder.start();
        StreamGobbler streamGobbler =
            new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        assert exitCode == 0;
        future.get();
    }

    /* (non-Javadoc)
     * @see org.dspace.app.capture.service.CaptureWebsiteService#takeScreenshot(org.dspace.app.capture.CapturableScreen)
     */
    @Override
    public InputStream getScreenshot(Context c, CapturableScreen capturableScreen) throws Exception {
        ProcessBuilder procBuilder = new ProcessBuilder();
        String command = buildCommand(c, capturableScreen);
        boolean isWindows = this.isWindows();
        if (isWindows) {
            procBuilder.command("cmd.exe", "/c", command);
        } else {
            procBuilder.command("/bin/bash", "-c", command);
        }
        procBuilder.directory(new File(System.getProperty("user.home")));
        return procBuilder.start().getInputStream();
    }

    private String buildCommand(Context c, CapturableScreen capturableScreen) {
        CapturableScreenConfiguration configuration = capturableScreen.getConfiguration();
        return new StringBuilder(
                configurationService.getProperty(CAPTURE_WEBSITE_NODE_PATH, CaptureWebsiteProperties.NODE)
            )
            .append(" ")
            .append(configurationService.getProperty(CAPTURE_WEBSITE_PATH, CaptureWebsiteProperties.CAPTURE_WEBSITE))
            .append(getUrlResource(capturableScreen.getUrl()))
            .append(computeHeader(c, configuration))
            .append(getCookie(capturableScreen.getCookie()))
            .append(getRemoveElements(configuration.getRemoveElements()))
            .append(getElement(configuration.getElement()))
            .append(getStyle(configuration.getStyle()))
            .append(getType(configuration.getType()))
            .append(getScaleFactor(configuration.getScale()))
            .toString();
    }

    protected StringBuilder getUrlResource(String url) {
        StringBuilder urlResource = new StringBuilder(url);
        String uiUrl = configurationService.getProperty("dspace.ui.url");
        if (!url.startsWith(uiUrl)) {
            urlResource = new StringBuilder(uiUrl);
            if (!url.startsWith("/")) {
                urlResource = urlResource.append("/").append(urlResource);
            }
        }
        return new StringBuilder(" ").append(urlResource);
    }

    @Override
    public StringBuilder computeHeader(Context c, CapturableScreenConfiguration configuration) {
        if (configuration == null || configuration.getHeadersConfiguration() == null) {
            return new StringBuilder();
        }
        return computeHeaders(c, configuration.getHeadersConfiguration())
                .entrySet()
                .stream()
                .map(header -> generateStringParameter("header", header.getKey(), header.getValue()))
                .collect(
                    Collectors.reducing(
                        new StringBuilder(" "),
                        (s1, s2) -> s1.append(" ").append(s2)
                    )
                );
    }

    public StringBuilder generateStringParameter(String parameterName, String headerName, String headerValue) {
        return new StringBuilder("--").append(parameterName).append("=")
            .append("\"")
            .append(headerName).append(": ").append(headerValue)
            .append("\"");
    }

    public Map<String, String> computeHeaders(Context c, Map<String, CapturableScreenHeaderValueMapper> headers) {
        return headers
            .entrySet()
            .stream()
            .collect(
                Collectors.groupingBy(
                    entry -> entry.getKey(),
                    Collectors.mapping(
                        entry -> entry.getValue().apply(c),
                        Collectors.joining(",")
                    )
                )
            );
    }

    protected StringBuilder getCookie(String cookie) {
        if (StringUtils.isBlank(cookie) || StringUtils.isEmpty(cookie)) {
            return new StringBuilder();
        }
        return new StringBuilder(" ")
                .append("--cookie=")
                .append("\"")
                .append("DSPACE-XSRF-COOKIE=").append(cookie)
                .append("\"");
    }

    protected StringBuilder getRemoveElements(String remove) {
        if (StringUtils.isBlank(remove) || StringUtils.isEmpty(remove)) {
            return new StringBuilder();
        }
        return new StringBuilder(" ")
            .append("--remove-elements=")
            .append("\"")
            .append(remove)
            .append("\"");
    }

    protected StringBuilder getElement(String element) {
        if (StringUtils.isBlank(element) || StringUtils.isEmpty(element)) {
            return new StringBuilder();
        }
        return new StringBuilder(" ")
            .append("--element=")
            .append("\"")
            .append(element)
            .append("\"");
    }

    protected StringBuilder getStyle(String style) {
        if (StringUtils.isBlank(style) || StringUtils.isEmpty(style)) {
            return new StringBuilder();
        }
        return new StringBuilder(" ")
            .append("--style=")
            .append("\"")
            .append(style)
            .append("\"");
    }

    protected StringBuilder getType(String type) {
        return new StringBuilder(" ")
            .append("--type=")
            .append("\"")
            .append(Optional.ofNullable(type).orElse(CaptureWebsiteProperties.DEFAULT_TYPE))
            .append("\"");
    }

    protected StringBuilder getScaleFactor(String scale) {
        return new StringBuilder(" ")
            .append("--scale-factor=")
            .append(
                Optional.ofNullable(scale).orElse(CaptureWebsiteProperties.DEFAULT_SCALE_FACTOR + "")
            );
    }

    public void setExtensionToMimeType(Map<String, String> extensionToMimeType) {
        this.extensionToMimeType = extensionToMimeType;
    }

    @Override
    public Map<String, String> getExtensionsToMimeType() {
        return this.extensionToMimeType;
    }

}
