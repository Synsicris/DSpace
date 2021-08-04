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
import java.util.Arrays;
import java.util.List;
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
 * Implementation of {@link TemplateValueGenerator} that generates a value related to
 * the current parentproject/project item.
 * <p>
 * Syntax is: ###CURRENTPROJECT.[parentproject|project]###, so for example
 * ###CURRENTPROJECT.parentproject### will set metadata with value of item that represent 
 * the parentproject entity.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 */
public class CurrentProjectGenerator extends AbstractGenerator {

    private static final Logger log = LoggerFactory.getLogger(CurrentProjectGenerator.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ItemService itemService;

    public CurrentProjectGenerator() {
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
            return Arrays.asList(getProjectEntityByCommunity(context, projectCommunity));
        } catch (Exception e) {
            log.error("Error while evaluating resource policies for collection {}: {}",
                templateItem.getTemplateItemOf().getID(), e.getMessage(), e);
            return new ArrayList<MetadataValueVO>();
        }
    }

    private MetadataValueVO getProjectEntityByCommunity(Context context, Community projectCommunity) {
        if (projectCommunity == null) {
            return new MetadataValueVO("");
        }
        List<MetadataValue> values = communityService.getMetadata(projectCommunity,
                ProjectConstants.MD_PROJECT_ENTITY.SCHEMA, ProjectConstants.MD_PROJECT_ENTITY.ELEMENT, 
                ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, null);
        if (values.isEmpty()) {
            return new MetadataValueVO("");
        } else {
            MetadataValue value = values.get(0);
            try {
                Item itemProject = itemService.find(context, UUIDUtils.fromString(value.getAuthority()));
                return new MetadataValueVO(itemProject.getName(), UUIDUtils.toString(itemProject.getID()),
                        value.getConfidence());
            } catch (SQLException e) {
                return new MetadataValueVO("");
            }
           
        }
    }
}
