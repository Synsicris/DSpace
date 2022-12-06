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
 * Implementation of {@link TemplateValueGenerator} that copies the Agrovoc keywords
 * from the parent project's item template
 *
 * @author Davide Negretti (davide.negretti at 4science.it)
 */
public class AgrovocKeywordsGenerator extends AbstractProjectGenerator {

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
                case ProjectConstants.PROJECT:
                    projectCommunity = getProjectCommunity(templateItem);
                    break;
                case ProjectConstants.FUNDING:
                    projectCommunity = getOwningCommunity(templateItem);
                    break;
                default:
                    throw new IllegalArgumentException("Unable to find mapper for : " + extraParams);
            }
            Item item = getProjectItem(context, projectCommunity);
            List<MetadataValue> values = itemService.getMetadata(item,
                    ProjectConstants.MD_AGROVOC.schema, ProjectConstants.MD_AGROVOC.element,
                    ProjectConstants.MD_AGROVOC.qualifier, null);

            return values.stream().map(value -> new MetadataValueVO(value)).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error while retreiving Agrovoc keywords for {}: {}",
                    templateItem.getTemplateItemOf().getID(), e.getMessage(), e);
            return new ArrayList<MetadataValueVO>();
        }

    }

    private Item getProjectItem(Context context, Community projectCommunity) throws SQLException {
        List<MetadataValue> values = communityService.getMetadata(projectCommunity,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.schema, ProjectConstants.MD_RELATION_ITEM_ENTITY.element,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier, null);
        return itemService.find(context, UUIDUtils.fromString(values.get(0).getAuthority()));
    }

}
