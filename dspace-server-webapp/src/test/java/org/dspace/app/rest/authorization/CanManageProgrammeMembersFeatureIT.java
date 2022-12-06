/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;
import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.authorization.impl.CanManageProgrammeMembersFeature;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.matcher.AuthorizationMatcher;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.DefaultProjection;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CanManageProgrammeMembersFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private Utils utils;

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private Item programmeA;
    private ItemRest programmeARest;
    private Community communityA;
    private Collection collectionA;
    private AuthorizationFeature canManageProgrammeMembersFeature;

    final String feature = CanManageProgrammeMembersFeature.NAME;

    private Group programmeMembersGroup;
    private Group programmeManagersGroup;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        context.turnOffAuthorisationSystem();

        canManageProgrammeMembersFeature = authorizationFeatureService.find(CanManageProgrammeMembersFeature.NAME);

        communityA =
            CommunityBuilder.createCommunity(context)
                .withName("communityA")
                .build();

        collectionA =
            CollectionBuilder.createCollection(context, communityA)
                .withName("collectionA")
                .build();

        programmeA =
            ItemBuilder.createItem(context, collectionA)
                .withTitle("itemA")
                .withEntityType(PROGRAMME)
                .build();

        context.restoreAuthSystemState();

        programmeARest = itemConverter.convert(programmeA, Projection.DEFAULT);
    }

    @Test
    public void anonymousHasNotAccessTest() throws Exception {
        getClient().perform(
            get("/api/authz/authorizations/search/object")
                .param("embed", "feature")
                .param("feature", feature)
                .param("uri", utils.linkToSingleResource(programmeARest, "self").getHref())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", is(0)))
            .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    public void epersonHasNotAccessTest() throws Exception {
        String epersonToken = getAuthToken(eperson.getEmail(), password);
        getClient(epersonToken).perform(
            get("/api/authz/authorizations/search/object")
                .param("embed", "feature")
                .param("feature", feature)
                .param("uri", utils.linkToSingleResource(programmeARest, "self").getHref())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", is(0)))
            .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    public void adminItemWithoutManagersGroupNotAuthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();
        programmeManagersGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeA.getID().toString()));
        this.groupService.delete(context, programmeManagersGroup);
        context.restoreAuthSystemState();
        context.commit();

        String adminToken = getAuthToken(admin.getEmail(), password);
        getClient(adminToken).perform(
            get("/api/authz/authorizations/search/object")
                .param("embed", "feature")
                .param("feature", feature)
                .param("uri", utils.linkToSingleResource(programmeARest, "self").getHref())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", is(0)))
            .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    public void adminItemWithoutMembersGroupNotAuthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();
        programmeMembersGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeA.getID().toString()));
        this.groupService.delete(context, programmeMembersGroup);
        context.restoreAuthSystemState();
        context.commit();

        String adminToken = getAuthToken(admin.getEmail(), password);
        getClient(adminToken).perform(
            get("/api/authz/authorizations/search/object")
                .param("embed", "feature")
                .param("feature", feature)
                .param("uri", utils.linkToSingleResource(programmeARest, "self").getHref())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", is(0)))
            .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    public void adminItemSuccessTest() throws Exception {
        String adminToken = getAuthToken(admin.getEmail(), password);
        getClient(adminToken).perform(
            get("/api/authz/authorizations/search/object")
                .param("embed", "feature")
                .param("feature", feature)
                .param("uri", utils.linkToSingleResource(programmeARest, "self").getHref())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", greaterThan(0)))
            .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void canManageProgrammeMembersFeatureTest() throws Exception {
        context.turnOffAuthorisationSystem();
        EPerson programmeManagerA =
            EPersonBuilder.createEPerson(context)
                .withEmail("testComAdminA@test.com")
                .withPassword(password)
                .build();

        EPerson programmeManagerB =
            EPersonBuilder.createEPerson(context)
                .withEmail("testComBdminA@test.com")
                .withPassword(password)
                .build();

        EPerson programmeMemberA =
            EPersonBuilder.createEPerson(context)
                .withEmail("testCol1Admin@test.com")
                .withPassword(password)
                .build();

        EPerson programmeMemberB =
            EPersonBuilder.createEPerson(context)
                .withEmail("testCol2Admin@test.com")
                .withPassword(password)
                .build();

        Item programmeB =
            ItemBuilder.createItem(context, collectionA)
                .withTitle("ProgrammeB")
                .withIssueDate("2021-04-19")
                .withAuthor("Doe, John")
                .withEntityType(PROGRAMME)
                .build();

        context.commit();

        programmeMembersGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeA.getID().toString()));

        programmeManagersGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeA.getID().toString()));

        Group programmeBManagerGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeB.getID().toString()));

        Group programmeBMembersGroup =
            this.groupService
                .findByName(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeB.getID().toString()));

        this.groupService.addMember(context, programmeManagersGroup, programmeManagerA);
        this.groupService.addMember(context, programmeMembersGroup, programmeMemberA);
        this.groupService.addMember(context, programmeBMembersGroup, programmeManagerA);
        this.groupService.addMember(context, programmeBMembersGroup, programmeMemberA);

        this.groupService.addMember(context, programmeBManagerGroup, programmeManagerB);
        this.groupService.addMember(context, programmeBMembersGroup, programmeMemberB);
        this.groupService.addMember(context, programmeMembersGroup, programmeManagerB);
        this.groupService.addMember(context, programmeMembersGroup, programmeMemberB);

        context.commit();

        context.restoreAuthSystemState();

        ItemRest programmeBRest = itemConverter.convert(programmeB, DefaultProjection.DEFAULT);
        ItemRest programmeARest = itemConverter.convert(programmeA, DefaultProjection.DEFAULT);

        String tokenProgrammeManagerA = getAuthToken(programmeManagerA.getEmail(), password);
        String tokenProgrammeManagerB = getAuthToken(programmeManagerB.getEmail(), password);
        String tokenProgrammeMemberA = getAuthToken(programmeMemberA.getEmail(), password);
        String tokenProgrammeMemberB = getAuthToken(programmeMemberB.getEmail(), password);

        // define authorizations that we know must exists
        Authorization programmeManagerAProgrammeA =
            new Authorization(programmeManagerA, canManageProgrammeMembersFeature, programmeARest);
        Authorization programmeManagerBProgrammeB =
            new Authorization(programmeManagerB, canManageProgrammeMembersFeature, programmeBRest);

        // define authorization that we know not exists
        Authorization programmeMemberAProgrammeA =
            new Authorization(programmeMemberA, canManageProgrammeMembersFeature, programmeARest);
        Authorization programmeMemberAProgrammeB =
            new Authorization(programmeMemberA, canManageProgrammeMembersFeature, programmeBRest);
        Authorization programmeManagerAProgrammeB =
            new Authorization(programmeManagerA, canManageProgrammeMembersFeature, programmeBRest);
        Authorization programmeMemberBProgrammeB =
            new Authorization(programmeMemberB, canManageProgrammeMembersFeature, programmeBRest);
        Authorization programmeMemberBProgrammeA =
            new Authorization(programmeMemberB, canManageProgrammeMembersFeature, programmeARest);
        Authorization programmeManagerBProgrammeA =
            new Authorization(programmeManagerB, canManageProgrammeMembersFeature, programmeARest);

        getClient(tokenProgrammeManagerA)
            .perform(get("/api/authz/authorizations/" + programmeManagerAProgrammeA.getID()))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath(
                    "$", Matchers.is(
                        AuthorizationMatcher.matchAuthorization(programmeManagerAProgrammeA)
                    )
                )
            );

        getClient(tokenProgrammeManagerB)
            .perform(get("/api/authz/authorizations/" + programmeManagerBProgrammeB.getID()))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath(
                    "$", Matchers.is(
                        AuthorizationMatcher.matchAuthorization(programmeManagerBProgrammeB)
                    )
                )
            );

        getClient(tokenProgrammeMemberA)
            .perform(get("/api/authz/authorizations/" + programmeMemberAProgrammeA.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenProgrammeMemberA)
            .perform(get("/api/authz/authorizations/" + programmeMemberAProgrammeB.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenProgrammeMemberB)
            .perform(get("/api/authz/authorizations/" + programmeMemberBProgrammeB.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenProgrammeMemberB)
            .perform(get("/api/authz/authorizations/" + programmeMemberBProgrammeA.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenProgrammeManagerA)
            .perform(get("/api/authz/authorizations/" + programmeManagerAProgrammeB.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenProgrammeManagerB)
            .perform(get("/api/authz/authorizations/" + programmeManagerBProgrammeA.getID()))
            .andExpect(status().isNotFound());
    }

}