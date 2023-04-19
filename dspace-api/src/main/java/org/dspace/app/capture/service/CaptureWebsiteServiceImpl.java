/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.dspace.app.capture.CapturableScreen;
import org.dspace.app.capture.CapturableScreenConfiguration;
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
    public void takeScreenshot(CapturableScreen capturableScreen) throws Exception {
        ProcessBuilder procBuilder = new ProcessBuilder();
        String command = buildCommand(capturableScreen);
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
    public InputStream getScreenshot(CapturableScreen capturableScreen) throws Exception {
        ProcessBuilder procBuilder = new ProcessBuilder();
        String command = buildCommand(capturableScreen);
        boolean isWindows = this.isWindows();
        if (isWindows) {
            procBuilder.command("cmd.exe", "/c", command);
        } else {
            procBuilder.command("/bin/bash", "-c", command);
        }
        procBuilder.directory(new File(System.getProperty("user.home")));
        return procBuilder.start().getInputStream();
    }

    private String buildCommand(CapturableScreen capturableScreen) {
        CapturableScreenConfiguration configuration = capturableScreen.getConfiguration();
        // TODO: ADD configuration property
        return new StringBuilder(configurationService.getProperty(NODE_PATH, NODE))
            .append(" ")
            .append(configurationService.getProperty(CAPTURE_WEBSITE_PATH, CAPTURE_WEBSITE))
            .append(getUrlResource(capturableScreen.getUrl()))
            .append(getHeader(capturableScreen.getToken()))
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
        if (!url.startsWith("/")) {
            urlResource = new StringBuilder("/").append(urlResource);
        }
        return new StringBuilder(" ")
            .append(configurationService.getProperty("dspace.ui.url"))
            .append(urlResource);
    }

    protected StringBuilder getHeader(String token) {
        return new StringBuilder(" ")
            .append("--header=")
            .append("\"")
            .append("Authorization: Bearer ").append(token)
            .append("\"");
    }

    protected StringBuilder getCookie(String cookie) {
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
            .append(Optional.ofNullable(type).orElse(DEFAULT_TYPE))
            .append("\"");
    }

    protected StringBuilder getScaleFactor(String scale) {
        return new StringBuilder(" ")
            .append("--scale-factor=")
            .append(
                Optional.ofNullable(scale).
                    orElse(DEFAULT_SCALE_FACTOR + "")
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
