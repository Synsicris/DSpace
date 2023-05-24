/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * Base url mapper that translates an item to the corresponding URL of
 * on the dspace ui.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class AbstractItemUrlMapper implements ItemUrlMapper {

    private static final String DSPACE_UI_URL = "dspace.ui.url";

    protected String baseUrl;
    protected ConfigurationService configurationService =
        DSpaceServicesFactory.getInstance().getConfigurationService();
    protected final String dspaceURL;

    public AbstractItemUrlMapper(String baseUrl) {
        this.baseUrl = baseUrl;
        this.dspaceURL = this.configurationService.getProperty(DSPACE_UI_URL);
    }

    @Override
    public abstract String mapToUrl(Context context, Item item);

}