/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.configuration.DiscoveryConfiguration;
import org.dspace.discovery.configuration.DiscoveryConfigurationService;
import org.dspace.discovery.configuration.DiscoveryRelatedItemConfiguration;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.discovery.utils.DiscoverQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provider that searches for facets configured using the `facetName` and `discoveryConfiguration`.
 * This is used just to check if we can process a graph screenshot, in case some data exists
 * the processing item wil be retrieved.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class FacetDiscoveryConfigurationItemProvider implements ItemProvider {

    @Autowired
    private DiscoverQueryBuilder queryBuilder;

    @Autowired
    private DiscoveryConfigurationService searchConfigurationService;

    @Autowired
    private SearchService searchService;

    protected final String facetName;
    protected final String discoveryConfiguration;

    public FacetDiscoveryConfigurationItemProvider(
        String discoveryConfiguration,
        String facetName
    ) {
        this.discoveryConfiguration = discoveryConfiguration;
        this.facetName = facetName;
    }

    @Override
    public Stream<Item> retrieve(Context c, Item i) {
        if (hasValidRelation(c, i)) {
            return Stream.of(i);
        }
        return Stream.ofNullable(null);
    }

    protected boolean hasValidRelation(Context c, Item i) {
        IndexableObject<?,?> scopeObject = new IndexableItem(i);
        DiscoveryConfiguration discoveryConfiguration = searchConfigurationService
            .getDiscoveryConfigurationByNameOrDso(this.discoveryConfiguration, scopeObject);
        boolean isRelatedItem = discoveryConfiguration != null &&
            discoveryConfiguration instanceof DiscoveryRelatedItemConfiguration;
        DiscoverResult searchResult = null;
        DiscoverQuery discoverQuery = null;
        try {
            discoverQuery = queryBuilder.buildFacetQuery(
                    c, scopeObject, discoveryConfiguration,
                    null, null, List.of(), (String) null, 1, 0L, facetName
            );
            if (isRelatedItem) {
                searchResult = searchService.search(c, discoverQuery);
            } else {
                searchResult = searchService.search(c, scopeObject, discoverQuery);
            }
        } catch (SearchServiceException e) {
//
        }
        return Optional.ofNullable(searchResult)
                    .map(s -> s.getFacetResult(facetName))
                    .filter(Objects::nonNull)
                    .map(l -> l.size() > 0)
                    .orElse(false);
    }

}
