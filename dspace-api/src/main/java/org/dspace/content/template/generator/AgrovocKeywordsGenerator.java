/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 *
 * @author Davide Negretti (davide.negretti at 4science.it)
 */
public class AgrovocKeywordsGenerator implements TemplateValueGenerator {

//    private static final Logger log = LoggerFactory.getLogger(AgrovocKeywordGenerator.class);
//
//    @Autowired
//    private CommunityService communityService;
//
//    @Autowired
//    private ConfigurationService configurationService;
//
//    @Autowired
//    private ItemService itemService;

    public AgrovocKeywordsGenerator() {
    }

    @Override
    public MetadataValueVO generator(Context context, Item targetItem, Item templateItem, String extraParams) {
    	
    	System.out.println("AGROVOC GENERATOR");
    	System.out.println(targetItem.getCollections().toString());

        return getProjectKeywords();
    }
    
    private MetadataValueVO getProjectKeywords() {
    	return new MetadataValueVO("");
    }
    


}
