/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.dspace.project.util.ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.authorization.impl.DeleteFeature;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.matcher.AuthorizationMatcher;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.DefaultProjection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.project.util.ProjectConstants;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for the canDelete authorization feature of Synsicris cases.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class DeleteFeatureSynsicrisIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemConverter itemConverter;
    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private AuthorizationFeature canDeleteFeature;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        context.turnOffAuthorisationSystem();

        canDeleteFeature = authorizationFeatureService.find(DeleteFeature.NAME);

        context.restoreAuthSystemState();
    }

    @Test
    public void canDeleteCommentTest() throws Exception {
        context.turnOffAuthorisationSystem();
        EPerson userA = EPersonBuilder.createEPerson(context)
                                      .withNameInMetadata("Roman", "Boychuk")
                                      .withEmail("roman.boychuk@example.com")
                                      .withPassword(password)
                                      .build();

        EPerson userB = EPersonBuilder.createEPerson(context)
                                      .withNameInMetadata("Misha", "Boychuk")
                                      .withEmail("misha.boychuk@example.com")
                                      .withPassword(password)
                                      .build();

        // create projects community
        Community projectsCommunity = CommunityBuilder.createCommunity(context)
                                                      .withName("Projects")
                                                      .build();
        // create shared community
        Community sharedCommunity = CommunityBuilder.createCommunity(context)
                                                      .withName("Shared")
                                                      .build();
        // crate project community
        Community projectACommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                                      .withName("project A")
                                                      .build();
        // create project collection
        Collection projectCollection = CollectionBuilder.createCollection(context, projectACommunity)
                                                        .withName("Consortia")
                                                        .withEntityType(ProjectConstants.PROJECT_ENTITY)
                                                        .build();

        // create comment collection
        Collection commentCollection = CollectionBuilder.createCollection(context, sharedCommunity)
                                                        .withName("Consortia")
                                                        .withEntityType(ProjectConstants.COMMENT_ENTITY)
                                                        .build();

        var groupName = String.format(PROJECT_FUNDERS_GROUP_TEMPLATE, projectACommunity.getID().toString());
        GroupBuilder.createGroup(context)
                    .withName(groupName)
                    .addMember(userA)
                    .build();

        Item project = ItemBuilder.createItem(context, projectCollection)
                                  .withTitle("project A")
                                  .build();

        Item comment = ItemBuilder.createItem(context, commentCollection)
                                  .withTitle("Test comment")
                                  .withRelationCommentProject(project.getName(), project.getID().toString())
                                  .build();

        ItemRest commentRest = itemConverter.convert(comment, DefaultProjection.DEFAULT);
        context.restoreAuthSystemState();

        // define authorizations that we know must exists
        Authorization userAToComment = new Authorization(userA, canDeleteFeature, commentRest);
        Authorization adminToComment = new Authorization(admin, canDeleteFeature, commentRest);

        // define authorization that we know not exists
        Authorization userBToComment = new Authorization(userB, canDeleteFeature, commentRest);
        Authorization anonymousToComment = new Authorization(null, canDeleteFeature, commentRest);

        String tokenUserA = getAuthToken(userA.getEmail(), password);
        String tokenUserB = getAuthToken(userB.getEmail(), password);
        String tokenAdmin = getAuthToken(admin.getEmail(), password);

        getClient(tokenUserA).perform(get("/api/authz/authorizations/" + userAToComment.getID()))
                             .andExpect(status().isOk())
                             .andExpect(jsonPath("$", Matchers.is(
                                        AuthorizationMatcher.matchAuthorization(userAToComment))));

        getClient(tokenAdmin).perform(get("/api/authz/authorizations/" + adminToComment.getID()))
                             .andExpect(status().isOk())
                             .andExpect(jsonPath("$", Matchers.is(
                                        AuthorizationMatcher.matchAuthorization(adminToComment))));

        getClient(tokenUserB).perform(get("/api/authz/authorizations/" + userBToComment.getID()))
                             .andExpect(status().isNotFound());

        getClient().perform(get("/api/authz/authorizations/" + anonymousToComment.getID()))
                   .andExpect(status().isNotFound());
    }

    @Test
    public void canDeleteEntityTest() throws Exception {
        context.turnOffAuthorisationSystem();
        EPerson userA = EPersonBuilder.createEPerson(context)
                                      .withNameInMetadata("Roman", "Boychuk")
                                      .withEmail("roman.boychuk@example.com")
                                      .withPassword(password)
                                      .build();

        EPerson userB = EPersonBuilder.createEPerson(context)
                                      .withNameInMetadata("Misha", "Boychuk")
                                      .withEmail("misha.boychuk@example.com")
                                      .withPassword(password)
                                      .build();

        // create projects community
        Community projectsCommunity = CommunityBuilder.createCommunity(context)
                                                    .withName("Projects")
                                                    .build();
        // crate project community
        Community projectACommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                                      .withName("project A")
                                                      .build();
        // create project collection
        Collection projectCollection = CollectionBuilder.createCollection(context, projectACommunity)
                                                        .withName("Consortia")
                                                        .withEntityType(ProjectConstants.PROJECT_ENTITY)
                                                        .build();
        // create Publications collection
        Collection collection = CollectionBuilder.createCollection(context, projectACommunity)
                                                 .withName("Publications")
                                                 .withEntityType("Publication")
                                                 .build();

        var groupName = String.format(PROJECT_MEMBERS_GROUP_TEMPLATE, projectACommunity.getID().toString());
        GroupBuilder.createGroup(context)
                    .withName(groupName)
                    .addMember(userA)
                    .build();

        Item project = ItemBuilder.createItem(context, projectCollection)
                                  .withTitle("project A")
                                  .build();

        Item publication = ItemBuilder.createItem(context, collection)
                                      .withTitle("New Publication A")
                                      .withSynsicrisRelationProject(project.getName(), project.getID().toString())
                                      .build();

        ItemRest publicationRest = itemConverter.convert(publication, DefaultProjection.DEFAULT);
        context.restoreAuthSystemState();

        // define authorizations that we know must exists
        Authorization userAToPublication = new Authorization(userA, canDeleteFeature, publicationRest);
        Authorization adminToPublication = new Authorization(admin, canDeleteFeature, publicationRest);

        // define authorization that we know not exists
        Authorization userBToPublication = new Authorization(userB, canDeleteFeature, publicationRest);
        Authorization anonymousToPublication = new Authorization(null, canDeleteFeature, publicationRest);

        String tokenUserA = getAuthToken(userA.getEmail(), password);
        String tokenUserB = getAuthToken(userB.getEmail(), password);
        String tokenAdmin = getAuthToken(admin.getEmail(), password);

        getClient(tokenUserA).perform(get("/api/authz/authorizations/" + userAToPublication.getID()))
                             .andExpect(status().isOk())
                             .andExpect(jsonPath("$", Matchers.is(
                                        AuthorizationMatcher.matchAuthorization(userAToPublication))));

        getClient(tokenAdmin).perform(get("/api/authz/authorizations/" + adminToPublication.getID()))
                             .andExpect(status().isOk())
                             .andExpect(jsonPath("$", Matchers.is(
                                        AuthorizationMatcher.matchAuthorization(adminToPublication))));

        getClient(tokenUserB).perform(get("/api/authz/authorizations/" + userBToPublication.getID()))
                             .andExpect(status().isNotFound());

        getClient().perform(get("/api/authz/authorizations/" + anonymousToPublication.getID()))
                   .andExpect(status().isNotFound());
    }

}
