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
import java.util.Optional;

import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ProjectGeneratorServiceImpl implements ProjectGeneratorService {

    @Autowired
    private ProjectConsumerServiceImpl projectService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public Community getOwningCommunity(Item templateItem) throws SQLException {
        Community parentCommunity = null;
        Collection owningCollection = templateItem.getTemplateItemOf();
        if (owningCollection == null) {
            // the item is a template item
            return null;
        }
        String[] commToSkip = configurationService.getArrayProperty("project.community-name.to-skip", new String[] {});
        parentCommunity = owningCollection.getCommunities().get(0);
        while (Arrays.stream(commToSkip).anyMatch(parentCommunity.getName()::equals)) {
            parentCommunity = parentCommunity.getParentCommunities().get(0);
        }
        return parentCommunity;
    }

    @Override
    public Community getProjectCommunity(Item templateItem) throws SQLException {
        Community owningCommunity = getOwningCommunity(templateItem);
        Community parentCommunity = owningCommunity.getParentCommunities().get(0);

        String parentCommId = configurationService.getProperty("project.parent-community-id", null);

        if (parentCommunity.getID().toString().equals(parentCommId)) {
            return owningCommunity;
        } else {
            return parentCommunity.getParentCommunities().get(0);
        }
    }

    @Override
    public Group getProjectCommunityGroup(Context context, Community community, String role) {

        if (community == null) {
            return null;
        }

        Group group = null;
        try {
            group = projectService.getProjectCommunityGroupByRole(context, community, role);
        } catch (SQLException e) {
            return null;
        }
        return group;
    }

    @Override
    public MetadataValueVO getProjectCommunityMetadata(Context context, Community community, String role) {
        return Optional.ofNullable(this.getProjectCommunityGroup(context, community, role))
            .map(group -> new MetadataValueVO(group.getName(), UUIDUtils.toString(group.getID()), Choices.CF_ACCEPTED))
            .orElse(new MetadataValueVO(""));
    }

    @Override
    public MetadataValueVO getFundingCommunityMetadata(Context context, Community community, String role) {
        if (community == null) {
            return new MetadataValueVO("");
        }

        Group group;
        try {
            group = projectService.getFundingCommunityGroupByRole(context, community, role);
        } catch (SQLException e) {
            return new MetadataValueVO("");
        }
        return new MetadataValueVO(group.getName(), UUIDUtils.toString(group.getID()), Choices.CF_ACCEPTED);
    }

}
