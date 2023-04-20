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

public class CapturableScreenConfiguration {

    protected String element;
    protected String removeElements;
    protected String style;
    protected String type;
    protected String scale;
    protected Map<String, CapturableScreenHeaderValueMapper> headersConfiguration;

    protected CapturableScreenConfiguration(
        String element, String removeElements, String style,
        String type, String scale
    ) {
        super();
        this.element = element;
        this.removeElements = removeElements;
        this.style = style;
        this.type = type;
        this.scale = scale;
    }

    public CapturableScreenConfiguration(
        String element, String removeElements, String style,
        String type, String scale, Map<String, CapturableScreenHeaderValueMapper> headersConfiguration
    ) {
        super();
        this.element = element;
        this.removeElements = removeElements;
        this.style = style;
        this.type = type;
        this.scale = scale;
        this.headersConfiguration = headersConfiguration;
    }

    public String getElement() {
        return element;
    }

    public String getRemoveElements() {
        return removeElements;
    }

    public String getStyle() {
        return style;
    }

    public String getType() {
        return type;
    }

    public String getScale() {
        return scale;
    }

    public Map<String, CapturableScreenHeaderValueMapper> getHeadersConfiguration() {
        return headersConfiguration;
    }

    protected void setHeadersConfiguration(Map<String, CapturableScreenHeaderValueMapper> headersConfiguration) {
        this.headersConfiguration = headersConfiguration;
    }


}
