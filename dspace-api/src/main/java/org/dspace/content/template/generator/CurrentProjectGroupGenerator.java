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

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link TemplateValueGenerator} that generates a value related to
 * the current project/funding group.
 * <p>
 * Syntax is: ###CURRENTPROJECT.[project|funding].[admin|members]###, so for example
 * ###CURRENTPROJECT.project.admin### will set metadata with value of admin group for the
 * parent project community.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 */
public class CurrentProjectGroupGenerator extends AbstractGenerator {

    private static final Logger log = LoggerFactory.getLogger(CurrentProjectGroupGenerator.class);

    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private ProjectConsumerServiceImpl projectService;

    @Autowired
    private ItemService itemService;

    public CurrentProjectGroupGenerator() {
    }

    @Override
    public List<MetadataValueVO> generator(Context context, Item targetItem, Item templateItem, String extraParams) {
        try {
            Community projectCommunity;
            String[] params = StringUtils.split(extraParams, "\\.");
            String scope = params[0];
            String role = "";
            if (params.length > 1) {
                role = params[1];
            }
            
            switch (scope) {
                case ProjectConstants.PROJECT:
                    projectCommunity = getProjectCommunity(templateItem);
                    break;
                case ProjectConstants.FUNDING:
                    projectCommunity = getOwningCommunity(templateItem);
                    break;
                default:
                    throw new IllegalArgumentException("Unable to find mapper for : " + extraParams);
            }
            
            return Arrays.asList(getProjectCommunityGroup(context, projectCommunity, role));
        } catch (Exception e) {
            log.error("Error while evaluating resource policies for collection {}: {}",
                templateItem.getTemplateItemOf().getID(), e.getMessage(), e);
            return new ArrayList<MetadataValueVO>();
        }
    }

    private MetadataValueVO getProjectCommunityGroup(Context context,  Community projectCommunity, String role) {
        if (projectCommunity == null) {
            return new MetadataValueVO("");
        }

        try {
            Group group = projectService.getProjectCommunityGroupByRole(context, projectCommunity, role);
            return new MetadataValueVO(group.getName(), UUIDUtils.toString(group.getID()), Choices.CF_ACCEPTED);
        } catch (SQLException e1) {
            return new MetadataValueVO("");
        }
    }
}
