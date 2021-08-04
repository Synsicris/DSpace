/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.project.util.ProjectConstants;
import org.dspace.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 *
 * @author Davide Negretti (davide.negretti at 4science.it)
 */
public class AgrovocKeywordsGenerator extends AbstractGenerator {

    private static final Logger log = LoggerFactory.getLogger(AgrovocKeywordsGenerator.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ItemService itemService;

    public AgrovocKeywordsGenerator() {
    }

    @Override
    public List<MetadataValueVO> generator(Context context, Item targetItem, Item templateItem, String extraParams) {
        try {
            Community projectCommunity;
            switch (extraParams) {
                case ProjectConstants.PARENTPROJECT:
                    projectCommunity = getParentProjectCommunity(templateItem);
                    break;
                case ProjectConstants.PROJECT:
                    projectCommunity = getProjectCommunity(templateItem);
                    break;
                default:
                    throw new IllegalArgumentException("Unable to find mapper for : " + extraParams);
            }
            Item item = getProjectItem(context, projectCommunity);
            List<MetadataValue> values = itemService.getMetadata(item,
                    ProjectConstants.MD_AGROVOC.SCHEMA, ProjectConstants.MD_AGROVOC.ELEMENT, 
                    ProjectConstants.MD_AGROVOC.QUALIFIER, null);

            return values.stream().map(value -> new MetadataValueVO(value)).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error while retreiving Agrovoc keywords for {}: {}",
                    templateItem.getTemplateItemOf().getID(), e.getMessage(), e);
            return new ArrayList<MetadataValueVO>();
        }

    }

    private Item getProjectItem(Context context, Community projectCommunity) throws SQLException {
        List<MetadataValue> values = communityService.getMetadata(projectCommunity,
                ProjectConstants.MD_PROJECT_ENTITY.SCHEMA, ProjectConstants.MD_PROJECT_ENTITY.ELEMENT,
                ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, null);
        return itemService.find(context, UUIDUtils.fromString(values.get(0).getAuthority()));
    }

}
