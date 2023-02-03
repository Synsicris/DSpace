/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.dspace.app.rest.matcher.AuthorizationMatcher.matchAuthorization;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.authorization.impl.HasEditGrantsOnItem;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HasEditGrantsOnItemIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private Utils utils;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private AuthorizationFeature hasEditGrantsOnItem;

    private Item openGrantsEntity;

    private Item closeGrantsEntity;

    private Item withoutGrantsEntity;

    private Item wrongGrantsEntity;

    private Community testProject;

    private Group adminGroup;

    private Group membersGroup;

    @Before
    public void setup() {

        context.turnOffAuthorisationSystem();

        hasEditGrantsOnItem = authorizationFeatureService.find(HasEditGrantsOnItem.NAME);

        Community joinProjects = createCommunity("Joint projects");

        testProject = createSubCommunity("Test Project", joinProjects);

        adminGroup = GroupBuilder.createGroup(context)
            .withName("project_" + testProject.getID() + "_members_group")
            .addMember(admin)
            .addMember(eperson)
            .build();

        membersGroup = GroupBuilder.createGroup(context)
            .withName("funding_" + testProject.getID() + "_members_group")
            .addMember(eperson)
            .build();

        Collection fundingColl = createCollection("Fundings", ProjectConstants.PROJECT, testProject);
        openGrantsEntity = ItemBuilder.createItem(context, fundingColl)
            .withTitle("Test funding")
            .withAuthorityMetadata(ProjectConstants.MD_POLICY_GROUP.schema, ProjectConstants.MD_POLICY_GROUP.element,
                    ProjectConstants.MD_POLICY_GROUP.qualifier, adminGroup.getName(), adminGroup.getID().toString())
            .build();

        closeGrantsEntity =
            ItemBuilder.createItem(context, fundingColl)
                .withTitle("Test close funding")
                .withAuthorityMetadata(
                    ProjectConstants.MD_POLICY_GROUP.schema, ProjectConstants.MD_POLICY_GROUP.element,
                    ProjectConstants.MD_POLICY_GROUP.qualifier, membersGroup.getName(), membersGroup.getID().toString()
                )
                .build();

        withoutGrantsEntity = ItemBuilder.createItem(context, fundingColl)
                .withTitle("Test funding without grants")
                .build();

        wrongGrantsEntity = ItemBuilder.createItem(context, fundingColl)
                .withTitle("Test funding without grants")
                .withMetadata(ProjectConstants.MD_POLICY_GROUP.schema, ProjectConstants.MD_POLICY_GROUP.element,
                        ProjectConstants.MD_POLICY_GROUP.qualifier, "test wrong")
                .build();
        context.restoreAuthSystemState();

    }

    @Test
    public void testItemWithoutGrants() throws Exception {

        ItemRest itemRest = itemConverter.convert(wrongGrantsEntity, Projection.DEFAULT);

        String token = getAuthToken(eperson.getEmail(), password);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(eperson.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());
    }

    @Test
    public void testItemWithWrongGrants() throws Exception {

        ItemRest itemRest = itemConverter.convert(withoutGrantsEntity, Projection.DEFAULT);

        String token = getAuthToken(eperson.getEmail(), password);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(eperson.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());
    }

    @Test
    public void testItemWithOpenedGrants() throws Exception {

        ItemRest itemRest = itemConverter.convert(openGrantsEntity, Projection.DEFAULT);

        String token = getAuthToken(admin.getEmail(), password);

        Authorization expectedAuthorization = new Authorization(admin, hasEditGrantsOnItem, itemRest);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(admin.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations", hasItem(matchAuthorization(expectedAuthorization))));

        token = getAuthToken(eperson.getEmail(), password);

        expectedAuthorization = new Authorization(eperson, hasEditGrantsOnItem, itemRest);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(eperson.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations", hasItem(matchAuthorization(expectedAuthorization))));
    }

    @Test
    public void testItemWithClosedGrants() throws Exception {

        ItemRest itemRest = itemConverter.convert(closeGrantsEntity, Projection.DEFAULT);

        String token = getAuthToken(admin.getEmail(), password);


        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(admin.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations").doesNotExist());

        token = getAuthToken(eperson.getEmail(), password);

        Authorization expectedAuthorization = new Authorization(eperson, hasEditGrantsOnItem, itemRest);

        getClient(token).perform(get("/api/authz/authorizations/search/object")
            .param("uri", getItemUri(itemRest))
            .param("eperson", String.valueOf(eperson.getID()))
            .param("feature", HasEditGrantsOnItem.NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authorizations", hasItem(matchAuthorization(expectedAuthorization))));
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
