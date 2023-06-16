/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.authorization.impl.ItemGrantsFeature;
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
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite for the ItemGrants feature
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ItemGrantsFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemConverter itemConverter;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    @SuppressWarnings("unused")
    private Collection collectionSubProjectA;
    @SuppressWarnings("unused")
    private Collection collectionSubProjectB;
    private Community projectsCommunity;
    private Community projectCommunity;
    private Community subprojectsCommunity;
    private Community subprojectAComm;
    private Community subprojectBComm;
    private Collection publicationsCollection;
    private EPerson fundingACoord;
    private EPerson fundingAUser;
    private EPerson fundingBCoord;
    private EPerson fundingBUser;
    private Item publication;
    private Item fundingA;
    private Item fundingB;


    /**
     * this hold a reference to the test feature {@link ItemGrantsFeature}
     */
    private AuthorizationFeature itemGrantsFeature;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        itemGrantsFeature = authorizationFeatureService.find(ItemGrantsFeature.NAME);

        context.turnOffAuthorisationSystem();

        fundingACoord = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("FundingA", "Coord")
                .withEmail("fundingacord@email.com")
                .withPassword(password).build();

        fundingAUser = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("FundingA", "User")
                .withEmail("fundingauser@email.com")
                .withPassword(password).build();

        fundingBCoord = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("FundingB", "Coord")
                .withEmail("fundingbcord@email.com")
                .withPassword(password).build();

        fundingBUser = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("FundingB", "User")
                .withEmail("fundingbuser@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        projectCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                           .withName("Project test").build();

        Group projectCommunityGroup = GroupBuilder.createGroup(context)
                     .withName(String.format(
                             ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE,
                             projectCommunity.getID().toString()
                         ))
                     .addMember(fundingACoord)
                     .addMember(fundingAUser)
                     .addMember(fundingBCoord)
                     .addMember(fundingBUser).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectCommunityGroup)
                                                  .withTemplateItem().build();

        context.setCurrentUser(fundingBCoord);
        publication = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Test Title")
                               .withPolicyGroup("GROUP_POLICY_PLACEHOLDER")
                               .withSharedProject("project")
                               .build();

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectCommunity)
                                               .withName("Fundings").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group fundingMemberAGroup =
            GroupBuilder.createGroup(context)
               .withName(
                   String.format(
                       ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE,
                       subprojectAComm.getID().toString()
                   )
               )
               .addMember(fundingAUser)
               .build();

        GroupBuilder.createGroup(context)
            .withName(
                String.format(
                    ProjectConstants.FUNDING_COORDINATORS_GROUP_TEMPLATE,
                    subprojectAComm.getID().toString()
                )
            )
            .addMember(fundingACoord)
            .build();

        collectionSubProjectA =
            CollectionBuilder.createCollection(context, subprojectAComm)
                .withSubmitterGroup(fundingMemberAGroup)
                .withName("Collection Sub Project A")
                .withEntityType(ProjectConstants.FUNDING_ENTITY)
                .build();

        fundingA = ItemBuilder.createItem(context, collectionSubProjectA)
                .withTitle("Sub ProjectA")
                .withCrisPolicyGroup(projectCommunityGroup.getName(), projectCommunityGroup.getID().toString())
                .withSharedProject("project")
                .build();

        subprojectBComm =
            CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                .withName("Sub ProjectB of SubprojectsCommunity")
                .build();

        Group subprojectBGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE,
                        subprojectBComm.getID().toString()
                    )
                )
                .addMember(fundingBUser)
                .build();

        GroupBuilder.createGroup(context)
            .withName(
                String.format(
                    ProjectConstants.FUNDING_COORDINATORS_GROUP_TEMPLATE,
                    subprojectBComm.getID().toString()
                )
            )
            .addMember(fundingBCoord)
            .build();

        collectionSubProjectB =
            CollectionBuilder.createCollection(context, subprojectAComm)
                .withSubmitterGroup(subprojectBGroup)
                .withName("Collection Sub Project B")
                .withEntityType(ProjectConstants.FUNDING_ENTITY)
                .build();

        fundingB = ItemBuilder.createItem(context, collectionSubProjectA)
                .withTitle("Sub ProjectA")
                .withCrisPolicyGroup(subprojectBGroup.getName(), subprojectBGroup.getID().toString())
                .withSharedProject("funding")
                .build();

        configurationService.setProperty("project.parent-community-id", projectsCommunity.getID().toString());
        configurationService.setProperty("project.funding-community-name", subprojectsCommunity.getName());
        context.restoreAuthSystemState();

    }

    @Test
    public void shouldAuthorizeForCoordinator() throws Exception {

        ItemRest itemRestA = itemConverter.convert(fundingA, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(fundingACoord.getEmail(), password);

        Authorization authGrantsItem = new Authorization(fundingACoord, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.is(
                                           AuthorizationMatcher.matchAuthorization(authGrantsItem))));
    }

    @Test
    public void shouldAuthorizeForAdministrator() throws Exception {

        ItemRest itemRestA = itemConverter.convert(fundingA, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(admin.getEmail(), password);

        Authorization authGrantsItem = new Authorization(admin, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.is(
                                           AuthorizationMatcher.matchAuthorization(authGrantsItem))));
    }

    @Test
    public void shouldNotAuthorizeForWrongCoordinator() throws Exception {

        ItemRest itemRestA = itemConverter.convert(fundingA, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(fundingBCoord.getEmail(), password);

        Authorization authGrantsItem = new Authorization(fundingBCoord, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAuthorizeForMember() throws Exception {

        ItemRest itemRestA = itemConverter.convert(fundingA, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(fundingAUser.getEmail(), password);

        Authorization authGrantsItem = new Authorization(fundingAUser, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAuthorizeForNotFundingEntity() throws Exception {

        ItemRest itemRestA = itemConverter.convert(publication, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(fundingBCoord.getEmail(), password);

        Authorization authGrantsItem = new Authorization(fundingBCoord, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isNotFound());
    }

}