/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

public class SimpleItemUrlMapper extends ItemUrlMapper {

    private static final String DSPACE_UI_URL = "dspace.ui.url";
    protected ConfigurationService configurationService =
        DSpaceServicesFactory.getInstance().getConfigurationService();
    protected final String dspaceURL;

    public SimpleItemUrlMapper(String baseUrl) {
        super(baseUrl);
        this.dspaceURL = this.configurationService.getProperty(DSPACE_UI_URL);
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        return mapIdToUrl(item.getID());
    }

    protected String mapIdToUrl(UUID uuid) {
        return Stream.of(
            this.dspaceURL,
            this.baseUrl,
            uuid.toString()
        ).collect(Collectors.joining("/"));
    }

}
