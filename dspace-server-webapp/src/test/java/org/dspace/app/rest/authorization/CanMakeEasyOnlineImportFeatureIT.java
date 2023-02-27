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

import org.dspace.app.rest.authorization.impl.CanMakeEasyOnlineImportFeature;
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
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This test deals with testing the feature {@link CanMakeEasyOnlineImportFeature}
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class CanMakeEasyOnlineImportFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private AuthorizationFeature canMakeEasyOnlineImportFeature;

    @Test
    @SuppressWarnings("unchecked")
    public void CanMakeEasyOnlineImportTest() throws Exception {

        String parentCommId = configurationService.getProperty("project.parent-community-id");

        try {

            context.turnOffAuthorisationSystem();

            EPerson userA =
                EPersonBuilder.createEPerson(context)
                    .withNameInMetadata("Mykhaylo", "Boychuk")
                    .withEmail("mykhaylo.boychuk@email.com")
                    .withPassword(password)
                    .build();

            Community projectsCommunity =
                CommunityBuilder.createCommunity(context)
                .withName("Projects Community")
                .build();

            configurationService.setProperty("project.parent-community-id", projectsCommunity.getID().toString());

            Community projectA =
                CommunityBuilder.createSubCommunity(context, projectsCommunity)
                    .withName("Community Project A")
                    .build();

            CollectionBuilder.createCollection(context, projectA)
                .withName("Project partners")
                .withEntityType("projectpartner")
                .withSubmitterGroup(userA)
                .build();

            Collection joinProjects =
                CollectionBuilder.createCollection(context, projectA)
                    .withName("Parent Project Collection Title")
                    .build();

            Item projectAItem =
                ItemBuilder.createItem(context, joinProjects)
                    .withTitle("project A")
                    .build();

            Community Projects =
                CommunityBuilder.createSubCommunity(context, projectA)
                    .withName("Projects")
                    .build();

            Community subprojectA =
                CommunityBuilder.createSubCommunity(context, Projects)
                    .withName("subproject A - project A all grants")
                    .build();

            Group subprojectAGroup =
                GroupBuilder.createGroup(context)
                    .withName("funding_" + subprojectA.getID().toString() + "_members_group")
                    .addMember(userA)
                    .build();

            Collection fundings =
                CollectionBuilder.createCollection(context, subprojectA)
                    .withName("Fundings")
                    .withSubmitterGroup(subprojectAGroup)
                    .withEntityType("Funding")
                    .build();

            Item projectItem =
                ItemBuilder.createItem(context, fundings)
                    .withTitle("Funding Item Title")
                    .withSynsicrisRelationProject(projectAItem.getName(), projectAItem.getID().toString())
                    .build();

            configurationService.setProperty("project.funding-community-name", Projects.getName());

            canMakeEasyOnlineImportFeature = authorizationFeatureService.find(CanMakeEasyOnlineImportFeature.NAME);

            context.restoreAuthSystemState();

            ItemRest projectRestItem = itemConverter.convert(projectItem, DefaultProjection.DEFAULT);

            String tokenAdmin = getAuthToken(admin.getEmail(), password);
            String tokenUserA = getAuthToken(userA.getEmail(), password);
            String tokenEPerson = getAuthToken(eperson.getEmail(), password);

            // define authorizations that we know must exists
            Authorization admin2projectItem = new Authorization(admin, canMakeEasyOnlineImportFeature, projectRestItem);
            Authorization userA2projectItem = new Authorization(userA, canMakeEasyOnlineImportFeature, projectRestItem);

            // define authorization that we know not exists
            Authorization eperson2projectItem =
                new Authorization(eperson, canMakeEasyOnlineImportFeature, projectRestItem);
            Authorization anonymous2projectItem =
                new Authorization(null, canMakeEasyOnlineImportFeature, projectRestItem);

            getClient(tokenAdmin).perform(get("/api/authz/authorizations/" + admin2projectItem.getID()))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$", Matchers.is(
                                                          AuthorizationMatcher.matchAuthorization(admin2projectItem))));

            getClient(tokenUserA).perform(get("/api/authz/authorizations/" + userA2projectItem.getID()))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$", Matchers.is(
                                                          AuthorizationMatcher.matchAuthorization(userA2projectItem))));

            getClient(tokenEPerson).perform(get("/api/authz/authorizations/" + eperson2projectItem.getID()))
                                   .andExpect(status().isNotFound());

            getClient().perform(get("/api/authz/authorizations/" + anonymous2projectItem.getID()))
                       .andExpect(status().isNotFound());

        } finally {
            configurationService.setProperty("project.parent-community-id", parentCommId);
        }


    }

}