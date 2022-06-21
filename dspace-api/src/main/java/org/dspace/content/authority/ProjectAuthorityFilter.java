/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.authority;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.service.CollectionService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.RequestService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.services.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
public class ProjectAuthorityFilter extends EntityTypeAuthorityFilter {

    private static final Logger log = LoggerFactory.getLogger(ProjectAuthorityFilter.class);

    private final RequestService requestService;
    private final CollectionService collectionService;
    private final ConfigurationService config;

    @Autowired
    public ProjectAuthorityFilter(RequestService requestService,
                                   CollectionService collectionService) {
        super(List.of());
        this.config = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.requestService = requestService;
        this.collectionService = collectionService;
    }


    @Override
    protected List<String> createFilterQueries() {
        Request currentRequest = requestService.getCurrentRequest();
        if (currentRequest != null) {
            Context context = Optional.ofNullable(currentRequest.getServletRequest())
                .map(rq -> (Context) rq.getAttribute("dspace.context")).orElseGet(Context::new);

            List<String> filters = new ArrayList<>(Optional.ofNullable(currentRequest.getHttpServletRequest())
                .map(hsr -> hsr.getParameter("collection"))
                .filter(StringUtils::isNotBlank)
                .map(collectionId -> collectionsFilter(collectionId, context))
                .filter(StringUtils::isNotBlank)
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList));
            
            // exclude item versions from the authority search
            filters.add("-synsicris.uniqueid:*");

            return filters;
        } else {
            return new ArrayList<String>();
        }
    }

    private String collectionsFilter(String collectionId, Context context) {

        List<Community> communities;

        try {
            Collection collection = collectionService.find(context, UUID.fromString(collectionId));
            communities = collection.getCommunities();
        } catch (SQLException e) {
            log.error("Error while trying to extract communities for collection {}: {}", collectionId, e.getMessage());
            return "";
        }

        if (Objects.isNull(communities)) {
            return "";
        }
        if (communities.size() != 1) {
            log.warn("Collection {} has {} communities, unable to proceed", collectionId,
                communities.size());
            return "";
        }
        if (communities.get(0).getName().equals("Shared")) {
            String projectsCommunityId = config.getProperty("project.parent-community-id");
            return "location.comm:" + projectsCommunityId;
        } else {
            return "location.comm:" + communities.get(0).getID();
        }
    }
}
