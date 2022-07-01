/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.discovery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Community;
import org.dspace.content.service.CommunityService;
import org.dspace.core.Context;
import org.dspace.discovery.indexobject.IndexableCommunity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The purpose of this plugin is to index synsicris.funding.community metadata for community.
 * 
 * @author Giuseppe Digilio (at 4science.it)
 */
public class SolrServiceIndexFundingCommunityPlugin implements SolrServiceIndexPlugin {

    private static final Logger log = org.apache.logging.log4j.LogManager
                                                .getLogger(SolrServiceIndexFundingCommunityPlugin.class);

    @Autowired(required = true)
    protected AuthorizeService authorizeService;
    @Autowired(required = true)
    protected CommunityService communityService;

    @Override
    public void additionalIndex(Context context, IndexableObject idxObj, SolrInputDocument document) {
        if (idxObj instanceof IndexableCommunity) {
            Community comm = ((IndexableCommunity) idxObj).getIndexedObject();
            if (comm != null) {
                String metadata = communityService.getMetadata(comm, "synsicris.funding.community");
                if (StringUtils.isNotBlank(metadata)) {
                    document.addField("search.fundingCommunity", metadata);
                }
            }
        }
    }

}