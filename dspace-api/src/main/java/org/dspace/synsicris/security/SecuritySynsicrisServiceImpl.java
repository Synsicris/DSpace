package org.dspace.synsicris.security;
import static org.dspace.content.Item.ANY;
import static org.dspace.project.util.ProjectConstants.MD_PROJECT_RELATION;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {SecuritySynsicrisService}
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
public class SecuritySynsicrisServiceImpl implements SecuritySynsicrisService {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Override
    public boolean isMemberOfRoleGroupOfProject(Context context, Item item, String roleGroupName) throws SQLException {
        List<MetadataValue> metadataValues = itemService.getMetadata(item, MD_PROJECT_RELATION.schema,
                                                                           MD_PROJECT_RELATION.element,
                                                                           MD_PROJECT_RELATION.qualifier,
                                                                           ANY);
        MetadataValue mv;
        if (CollectionUtils.isNotEmpty(metadataValues) && (mv = metadataValues.get(0)) != null) {
            Item projectItem = itemService.find(context, UUID.fromString(mv.getAuthority()));
            Group roleGroup = getCommunityRoleGroup(context, projectItem, roleGroupName);
            return Objects.nonNull(roleGroup)
                    ? groupService.isMember(context, context.getCurrentUser(), roleGroup)
                    : false;
        }
        return false;
    }

    private Group getCommunityRoleGroup(Context context, Item projectItem, String roleGroupName) throws SQLException {
        Community communityOfProject = projectConsumerService.getFirstOwningCommunity(context, projectItem);
        return projectConsumerService.getProjectCommunityGroupByRole(context, communityOfProject, roleGroupName);
    }

}