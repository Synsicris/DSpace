/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.mapper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * This class computes the header value for a given property,
 * if the property is not found, the defaultValue will be used instead.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class PropertiesHeaderValueMapper implements CapturableScreenHeaderValueMapper {

    private ConfigurationService configurationService =
        DSpaceServicesFactory.getInstance().getConfigurationService();
    private final String propertyName;
    private final String defaultValue;
    private final String prefix;

    PropertiesHeaderValueMapper(
        String propertyName, String defaultValue, String prefix
    ) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.prefix = prefix;
    }

    PropertiesHeaderValueMapper(
        String propertyName, String defaultValue
    ) {
        this(propertyName, defaultValue, null);
    }

    @Override
    public String apply(Context t) {
        if (this.configurationService.hasProperty(propertyName)) {
            return Stream.of(
                    this.prefix,
                    this.configurationService.getProperty(propertyName)
                )
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
        }
        return this.defaultValue;
    }

}
