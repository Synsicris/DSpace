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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.InProgressSubmission;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.factory.ItemAuthorityServiceFactory;
import org.dspace.content.authority.service.ItemAuthorityService;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.ReloadableEntity;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.servicemanager.DSpaceKernelImpl;
import org.dspace.servicemanager.DSpaceKernelInit;
import org.dspace.services.ConfigurationService;
import org.dspace.services.RequestService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.services.model.Request;
import org.dspace.util.ItemAuthorityUtils;
import org.dspace.util.UUIDUtils;
import org.dspace.utils.DSpace;

/**
 * Sample authority to link a dspace item with another (i.e a publication with
 * the corresponding dataset or viceversa)
 *
 * @author Andrea Bollini
 * @author Giusdeppe Digilio
 * @version $Revision $
 */
public class ItemAuthority implements ChoiceAuthority, LinkableEntityAuthority {
    private static final Logger log = Logger.getLogger(ItemAuthority.class);

    /** the name assigned to the specific instance by the PluginService, @see {@link NameAwarePlugin} **/
    private String authorityName;

    private DSpace dspace = new DSpace();

    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    
    private CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();

    private SearchService searchService = dspace.getServiceManager().getServiceByName(
        "org.dspace.discovery.SearchService", SearchService.class);

    private ItemAuthorityServiceFactory itemAuthorityServiceFactory = dspace.getServiceManager().getServiceByName(
            "itemAuthorityServiceFactory", ItemAuthorityServiceFactory.class);

    private ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

    /**
     * The service manager kernel
     */
    private static transient DSpaceKernelImpl kernelImpl = DSpaceKernelInit.getKernel(null);;

    // punt!  this is a poor implementation..
    @Override
    public Choices getBestMatch(String text, String locale) {
        return getMatches(text, 0, 2, locale);
    }

    /**
     * Match a proposed value against existend DSpace item applying an optional
     * filter query to limit the scope only to specific item types
     */
    @Override
    public Choices getMatches(String text, int start, int limit, String locale) {
        Context context = new Context();
        if (limit <= 0) {
            limit = 20;
        }

        SolrClient solr = searchService.getSolrSearchCore().getSolr();
        String relationshipType = getLinkedEntityType();
        ItemAuthorityService itemAuthorityService = itemAuthorityServiceFactory.getInstance(relationshipType);
        String luceneQuery = itemAuthorityService.getSolrQuery(text);

//        DiscoverQuery discoverQuery = new DiscoverQuery();
        SolrQuery discoverQuery = new SolrQuery();
        String dspaceObjectFilterQueries = "search.resourcetype:" + Item.class.getSimpleName() +
          " OR search.resourcetype:" + WorkspaceItem.class.getSimpleName();

        discoverQuery.addFilterQuery(dspaceObjectFilterQueries);

        if (StringUtils.isNotBlank(relationshipType)) {
            String filter = "relationship.type:" + relationshipType;
            discoverQuery.addFilterQuery(filter);
        }

        // Add filter to limit search within the community which the given collection, if any, belongs
        if (kernelImpl != null) {
            RequestService requestService = kernelImpl.getServiceManager().getServiceByName(
                    RequestService.class.getName(), RequestService.class);
    
            if (requestService != null && requestService.getCurrentRequest() != null) {
            	Request request = requestService.getCurrentRequest();
            	if (request.getHttpServletRequest() != null) {
            		String collectionUUID = request.getHttpServletRequest().getParameter("collection");
            		if (collectionUUID != null) {
            		    Collection collection;
            		    try {
            		        collection = collectionService.find(context, UUIDUtils.fromString(collectionUUID));
            		    } catch (Exception e) {
            		        collection = null;
            		    }
            		    if (collection != null) {
            		        List<Community> list;
            		        try {
            		            list = collection.getCommunities();
            		        } catch (SQLException e) {
            		            list = new ArrayList<Community>();
            		        }
            		        String communityFilters  = "";
            		        for (int i = 0; i < list.size(); i++) {
            		            Community community = list.get(i);
            		            communityFilters += "location.comm:" + community.getID().toString();
            		            if (i < (list.size() - 1)) {
            		                communityFilters += " OR "; 
            		            }
            		            
            		        }
            		        discoverQuery.addFilterQuery(communityFilters);
            		    }
            		}
            	}
            }
        }

        discoverQuery
            .setQuery(luceneQuery);
        discoverQuery.setStart(start);
        discoverQuery.setRows(limit);

        try {
            QueryResponse queryResponse = solr.query(discoverQuery);
            List<Choice> choiceList = queryResponse.getResults()
                .stream()
                .map(doc ->  {
                    String resourceType = (String) doc.getFieldValue("search.resourcetype");
                    String resourceId;
                    if (resourceType.equals(WorkspaceItem.class.getSimpleName())) {
                        resourceId = ((String) ((ArrayList<String>) doc.getFieldValue("inprogress.item")).get(0))
                                .replace("Item-", "");
                    } else {
                        resourceId = (String) doc.getFieldValue("search.resourceid");
                    }
                    String title = ((ArrayList<String>) doc.getFieldValue("dc.title")).get(0);
//                    Map<String, String> extras = ItemAuthorityUtils.buildExtra(getPluginInstanceName(), doc);
                    Map<String, String> extras = new HashMap<String, String>();
                    return new Choice(resourceId, title, title, extras);
                }).collect(Collectors.toList());

            Choice[] results = new Choice[choiceList.size()];
            results = choiceList.toArray(results);
            long numFound = queryResponse.getResults().getNumFound();

            return new Choices(results, start, (int) numFound, Choices.CF_AMBIGUOUS,
                               numFound > (start + limit), 0);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Choices(Choices.CF_UNSET);
        }
    }

    @Override
    public String getLabel(String key, String locale) {
        String title = key;
        if (key != null) {
            Context context = null;
            try {
                context = new Context();
                DSpaceObject dso = itemService.find(context, UUIDUtils.fromString(key));
                if (dso != null) {
                    title = dso.getName();
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return key;
            }
        }
        return title;
    }

    @Override
    public String getLinkedEntityType() {
        return configurationService.getProperty("cris.ItemAuthority." + authorityName + ".relationshipType");
    }

    public void setPluginInstanceName(String name) {
        authorityName = name;
    }

    @Override
    public String getPluginInstanceName() {
        return authorityName;
    }
}
