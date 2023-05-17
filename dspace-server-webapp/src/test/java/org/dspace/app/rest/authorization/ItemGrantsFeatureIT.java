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
    private Community subprojectsCommunity;
    private Community subprojectAComm;
    private Community subprojectBComm;
    private Collection publicationsCollection;


    /**
     * this hold a reference to the test feature {@link ItemGrantsFeature}
     */
    private AuthorizationFeature itemGrantsFeature;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        itemGrantsFeature = authorizationFeatureService.find(ItemGrantsFeature.NAME);
    }

    @Test
    public void itemGrantsFeatureTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .addMember(ePerson2).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        context.setCurrentUser(ePerson1);
        Item item = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Test Title")
                               .withPolicyGroup("GROUP_POLICY_PLACEHOLDER")
                               .withSharedProject("project")
                               .build();

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

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
               .addMember(ePerson1)
               .build();

        GroupBuilder.createGroup(context)
            .withName(
                String.format(
                    ProjectConstants.FUNDING_COORDINATORS_GROUP_TEMPLATE,
                    subprojectAComm.getID().toString()
                )
            )
            .addMember(ePerson1)
            .build();

        collectionSubProjectA =
            CollectionBuilder.createCollection(context, subprojectAComm)
                .withSubmitterGroup(fundingMemberAGroup)
                .withName("Collection Sub Project A")
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
                .addMember(ePerson2)
                .build();

        collectionSubProjectB =
            CollectionBuilder.createCollection(context, subprojectAComm)
                .withSubmitterGroup(subprojectBGroup)
                .withName("Collection Sub Project B")
                .build();

        configurationService.setProperty("project.funding-community-name", subprojectsCommunity.getName());
        context.restoreAuthSystemState();

        ItemRest itemRestA = itemConverter.convert(item, DefaultProjection.DEFAULT);

        String tokenEPerson1 = getAuthToken(ePerson1.getEmail(), password);

        Authorization authGrantsItem = new Authorization(ePerson1, itemGrantsFeature, itemRestA);

        getClient(tokenEPerson1).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.is(
                                           AuthorizationMatcher.matchAuthorization(authGrantsItem))));
    }

    @Test
    public void itemGrantsFeatureNotFoundTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .addMember(ePerson2).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item item = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Test Title")
                               .withPolicyGroup("GROUP_POLICY_PLACEHOLDER")
                               .withSharedProject("project")
                               .build();

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson1).build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        configurationService.setProperty("project.funding-community-name", subprojectsCommunity.getName());
        context.restoreAuthSystemState();

        ItemRest itemRestA = itemConverter.convert(item, DefaultProjection.DEFAULT);

        String tokenEperson = getAuthToken(ePerson1.getEmail(), password);

        Authorization authGrantsItem = new Authorization(ePerson1, itemGrantsFeature, itemRestA);

        getClient(tokenEperson).perform(get("/api/authz/authorizations/" + authGrantsItem.getID()))
                               .andExpect(status().isNotFound());
    }

}