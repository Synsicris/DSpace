/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ItemGrants feature. It can be used for verify that an user
 * has access to modify the grants of a specific item.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = ItemGrantsFeature.NAME,description =
    "It can be used for verify that an user has access to modify the grants of a specific item")
public class ItemGrantsFeature implements AuthorizationFeature {

    public static final String NAME = "canEditedGrants";

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private Utils utils;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        EPerson currentUser = context.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return false;
        }

        if (StringUtils.equals(object.getType(), ItemRest.NAME)) {
            Item item = getItem(context, object);
            if (isSharedOrFunder(context, item)) {
                return false;
            }
            EPerson submitter = item.getSubmitter();
            Community project = item.getOwningCollection().getCommunities().get(0);
            if (Objects.nonNull(submitter) && Objects.nonNull(project)) {
                List<Community> subProjects = projectConsumerService.getAllSubProjectsByUser(context,submitter,project);
                // to allow edit grants submitter MUST be member of only one subproject within a project
                if (Objects.isNull(subProjects) || subProjects.size() != 1) {
                    return false;
                }
                boolean isAdminOfSubProject = isAdminMemberOfSubProject(context, subProjects.get(0), currentUser);
                if (isAdminOfSubProject || submitter.getID().equals(currentUser.getID())) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private Item getItem(Context context, BaseObjectRest object) throws IllegalArgumentException, SQLException {
        DSpaceObject dSpaceObject = (DSpaceObject) utils.getDSpaceAPIObjectFromRest(context, (ItemRest) object);
        if (dSpaceObject.getType() == Constants.ITEM && Objects.nonNull(dSpaceObject)) {
            return ((Item) dSpaceObject);
        }
        return null;
    }

    private boolean isSharedOrFunder(Context context, Item item) {
        String value = itemService.getMetadataFirstValue(item, "cris", "project", "shared", Item.ANY);
        return StringUtils.isNotBlank(value) &&
             (StringUtils.equals(value, ProjectConstants.SHARED) ||
              StringUtils.equals(value, ProjectConstants.FUNDER) ||
              StringUtils.equals(value, ProjectConstants.FUNDER_PROGRAMME));
    }

    private boolean isAdminMemberOfSubProject(Context context, Community community, EPerson submitter)
            throws SQLException {
        StringBuilder memberGroupName = new StringBuilder("project_")
                                                  .append(community.getID().toString())
                                                  .append("_admin_group");
        Group group = groupService.findByName(context, memberGroupName.toString());
        if (groupService.isMember(context, submitter, group)) {
            return true;
        }
        return false;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] { ItemRest.CATEGORY + "." + ItemRest.NAME };
    }

}