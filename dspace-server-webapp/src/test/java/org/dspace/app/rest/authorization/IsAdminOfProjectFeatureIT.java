/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.dspace.app.rest.matcher.AuthorizationMatcher.matchAuthorization;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.authorization.impl.IsAdminOfProjectFeature;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IsAdminOfProjectFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private Utils utils;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private AuthorizationFeature isAdminOfProject;

    private Item parentProjectEntity;

    private Community testProject;

    @Before
    public void setup() {

        context.turnOffAuthorisationSystem();

        isAdminOfProject = authorizationFeatureService.find(IsAdminOfProjectFeature.NAME);

        Community joinProjects = createCommunity("Joint projects");

        testProject = createSubCommunity("Test Project", joinProjects);

        GroupBuilder.createGroup(context)
            .withName("project_" + testProject.getID() + "_admin_group")
            .addMember(admin)
            .build();

        GroupBuilder.createGroup(context)
            .withName("project_" + testProject.getID() + "_members_group")
            .addMember(eperson)
            .build();
        
        Collection joinProject = createCollection("Joint projects", PROJECT_ENTITY, testProject);
        parentProjectEntity = ItemBuilder.createItem(context, joinProject)
            .withTitle("Test project")
            .build();

        context.restoreAuthSystemState();

    }

    @Test
    public void testWithProjectMember() throws Exception {

        ItemRest itemRest = itemConverter.convert(parentProjectEntity, Projection.DEFAULT);

        String token = getAuthToken(eperson.getEmail(), password);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(eperson.getID()))
            .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());
    }

    @Test
    public void testWithProjectAdmin() throws Exception {

        ItemRest itemRest = itemConverter.convert(parentProjectEntity, Projection.DEFAULT);

        String token = getAuthToken(admin.getEmail(), password);

        Authorization expectedAuthorization = new Authorization(admin, isAdminOfProject, itemRest);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(admin.getID()))
            .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations", hasItem(matchAuthorization(expectedAuthorization))));
    }

    @Test
    public void testWithOtherUser() throws Exception {

        ItemRest itemRest = itemConverter.convert(parentProjectEntity, Projection.DEFAULT);

        context.turnOffAuthorisationSystem();
        EPerson user = EPersonBuilder.createEPerson(context)
            .withEmail("test@user.it")
            .withPassword(password)
            .withCanLogin(true)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(user.getEmail(), password);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(user.getID()))
            .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());
    }

    @Test
    public void testWithSubProject() throws Exception {

        context.turnOffAuthorisationSystem();

        Community testSubProjects = createSubCommunity("Sub projects", testProject);
        Community subProject = createSubCommunity("sub_001", testSubProjects);
        Collection subPublications = createCollection("Sub Publications", "Publication", subProject);

        EPerson user = EPersonBuilder.createEPerson(context)
            .withEmail("test@user.it")
            .withPassword(password)
            .withCanLogin(true)
            .build();

        Item publication = ItemBuilder.createItem(context, subPublications)
            .withTitle("Publication")
            .withParentproject(parentProjectEntity.getName(), parentProjectEntity.getID().toString())
            .build();

        GroupBuilder.createGroup(context)
            .withName("funding_" + subProject.getID() + "_admin_group")
            .addMember(user)
            .build();

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(publication, Projection.DEFAULT);

        Authorization expectedAuthorization = new Authorization(admin, isAdminOfProject, itemRest);
        getClient(getAuthToken(admin.getEmail(), password))
            .perform(get("/api/authz/authorizations/search/object")
                .param("uri", getItemUri(itemRest))
                .param("eperson", String.valueOf(admin.getID()))
                .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations", hasItem(matchAuthorization(expectedAuthorization))));

        getClient(getAuthToken(eperson.getEmail(), password))
            .perform(get("/api/authz/authorizations/search/object")
                .param("uri", getItemUri(itemRest))
                .param("eperson", String.valueOf(eperson.getID()))
                .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());

        getClient(getAuthToken(user.getEmail(), password))
            .perform(get("/api/authz/authorizations/search/object")
                .param("uri", getItemUri(itemRest))
                .param("eperson", String.valueOf(user.getID()))
                .param("feature", IsAdminOfProjectFeature.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());

    }

    private String getItemUri(ItemRest itemRest) {
        return utils.linkToSingleResource(itemRest, "self").getHref();
    }

    private Community createCommunity(String name) {
        return CommunityBuilder.createCommunity(context)
            .withName(name)
            .build();
    }

    private Community createSubCommunity(String name, Community parent) {
        return CommunityBuilder.createSubCommunity(context, parent)
            .withName(name)
            .build();
    }

    private Collection createCollection(String name, String entityType, Community parent) {
        return CollectionBuilder.createCollection(context, parent)
            .withName(name)
            .withEntityType(entityType)
            .build();
    }
}
