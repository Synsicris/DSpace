/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.synsicris.security;

import java.sql.SQLException;
import java.util.List;

import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service abstract class for checking custom authorizations
 * used in PermissionEvaluetorPlugin or AuthorizationFeature.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
public abstract class SecuritySynsicrisDeleteService {

    @Autowired
    protected ItemService itemService;
    @Autowired
    protected GroupService groupService;
    @Autowired
    protected ProjectConsumerService projectConsumerService;

    /**
     * This method check if the user being to context can delete the item
     * 
     * @param context   DSpace context object
     * @param item      Item to check
     */
    public abstract boolean canDelete(Context context, Item item);

    /**
     * @return list of names with supported RoleGroup
     */
    public abstract List<String> getSupportedRoleGroup();

    /**
     * return RoleGroup if it exists related to project root community
     * 
     * @param context          DSpace context object
     * @param projectItem      Project item
     * @param roleGroupName    Name of RoleGroup
     * @throws SQLException    If there's a database problem
     */
    protected Group getCommunityGroupByRole(Context context, Item projectItem, String roleGroupName)
            throws SQLException {
        Community communityOfProject = projectConsumerService.getFirstOwningCommunity(context, projectItem);
        return projectConsumerService.getProjectCommunityGroupByRole(context, communityOfProject, roleGroupName);
    }

}