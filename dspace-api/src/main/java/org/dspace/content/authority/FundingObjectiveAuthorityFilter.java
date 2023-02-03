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
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
public class FundingObjectiveAuthorityFilter extends EntityTypeAuthorityFilter {

    private static final Logger log = LoggerFactory.getLogger(FundingObjectiveAuthorityFilter.class);

    private final RequestService requestService;
    private final CollectionService collectionService;
    private final ProjectConsumerService projectService;
    private final ItemService itemService;

    @Autowired
    public FundingObjectiveAuthorityFilter(RequestService requestService, CollectionService collectionService,
            ProjectConsumerService projectService, ItemService itemService) {
        super(List.of());
        this.requestService = requestService;
        this.collectionService = collectionService;
        this.projectService = projectService;
        this.itemService = itemService;
    }

    @Override
    protected List<String> createFilterQueries() {
        Request currentRequest = requestService.getCurrentRequest();
        if (currentRequest != null) {
            Context context = Optional.ofNullable(currentRequest.getServletRequest())
                .map(rq -> (Context) rq.getAttribute("dspace.context")).orElseGet(Context::new);

            return Optional.ofNullable(currentRequest.getHttpServletRequest())
                .map(hsr -> hsr.getParameter("collection"))
                .filter(StringUtils::isNotBlank)
                .map(collectionId -> getCallsFilterQuery(collectionId, context))
                .filter(StringUtils::isNotBlank)
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
        } else {
            return new ArrayList<String>();
        }
    }

    private String getCallsFilterQuery(String collectionId, Context context) {

        // set a fake query to not return result in case any conditions are found
        String filterQuery = null;

        Item projectItem = null;
        try {
            projectItem = projectService.getParentProjectItemByCollectionUUID(context, UUID.fromString(collectionId));

            if (projectItem != null) {
                List<MetadataValue> values = itemService.getMetadata(projectItem,
                        ProjectConstants.MD_RELATION_CALL.schema, ProjectConstants.MD_RELATION_CALL.element,
                        ProjectConstants.MD_RELATION_CALL.qualifier, null);

                for (MetadataValue value : values) {
                    if (StringUtils.isNotBlank(value.getAuthority())) {
                        String filter = ProjectConstants.MD_RELATION_CALL.toString() + "_authority:" +
                                value.getAuthority();
                        if (StringUtils.isBlank(filterQuery)) {
                            filterQuery = filter;
                        } else {
                            filterQuery += " OR " + filter;
                        }
                        String programmeFilterQuery = getProgrammeFilterQuery(context,
                                UUID.fromString(value.getAuthority()));
                        if (StringUtils.isNotBlank(programmeFilterQuery)) {
                            filterQuery += " OR " + programmeFilterQuery;
                        }
                    }

                }
            }
        } catch (SQLException e) {
            log.error("Error while trying to retrieve call conditions for collection {}: {}", collectionId,
                    e.getMessage());
            return "search.resourceid:null";
        }
        return StringUtils.isBlank(filterQuery) ? "search.resourceid:null" : filterQuery;
    }

    String getProgrammeFilterQuery(Context context, UUID callItemUUID) {
        String filterQuery = "";
        if (callItemUUID != null) {
            try {
                Item callItem = itemService.find(context, callItemUUID);

                if (callItem != null) {
                    List<MetadataValue> values = itemService.getMetadata(callItem,
                            ProjectConstants.MD_RELATION_CALLTOPROGRAMME.schema,
                            ProjectConstants.MD_RELATION_CALLTOPROGRAMME.element,
                            ProjectConstants.MD_RELATION_CALLTOPROGRAMME.qualifier, null);

                    for (MetadataValue value : values) {
                        if (StringUtils.isNotBlank(value.getAuthority())) {
                            String filter = ProjectConstants.MD_RELATION_FUNDINGOBJTOPROGRAMME.toString() +
                                    "_authority:" + value.getAuthority();
                            if (StringUtils.isBlank(filterQuery)) {
                                filterQuery = filter;
                            } else {
                                filterQuery += " OR " + filter;
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                log.error("Error while trying to retrieve call item for the given uuid: {}", callItemUUID,
                        e.getMessage());
                return filterQuery;
            }

        }

        return filterQuery;
    }
}
