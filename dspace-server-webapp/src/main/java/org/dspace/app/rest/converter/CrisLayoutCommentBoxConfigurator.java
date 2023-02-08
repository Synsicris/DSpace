/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.model.CrisLayoutBoxConfigurationRest;
import org.dspace.app.rest.model.CrisLayoutBoxRelationConfigurationRest;
import org.dspace.core.Context;
import org.dspace.layout.CrisLayoutBox;
import org.dspace.layout.CrisLayoutBoxTypes;
import org.springframework.stereotype.Component;

/**
 *
 * This is the configurator for comment layout box
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
@Component
public class CrisLayoutCommentBoxConfigurator implements CrisLayoutBoxConfigurator {

    @Override
    public boolean support(CrisLayoutBox box) {
        return StringUtils.equals(box.getType(), CrisLayoutBoxTypes.COMMENT.name());
    }

    @Override
    public CrisLayoutBoxConfigurationRest getConfiguration(CrisLayoutBox box) {
        CrisLayoutBoxRelationConfigurationRest rest = new CrisLayoutBoxRelationConfigurationRest();
        StringBuilder discoveryConfiguration = new StringBuilder(CrisLayoutBoxTypes.COMMENT.name());
        discoveryConfiguration.append(".");
        discoveryConfiguration.append(box.getEntitytype().getLabel());
        discoveryConfiguration.append(".");
        discoveryConfiguration.append(box.getShortname());
        rest.setDiscoveryConfiguration(discoveryConfiguration.toString());
        return rest;
    }

    @Override
    public void configure(Context context, CrisLayoutBox box, CrisLayoutBoxConfigurationRest rest) {
        // NOTHING TO DO ....
    }

}
