/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicReference;

import org.dspace.app.rest.matcher.WorkspaceItemMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.WorkspaceItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite to verify the related entities creation via {@link ProjectCreateGrantsConsumer}.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectCreateGrantsConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommunityService communityService;

    @Test
    public void createWorkspaceItemWithSubmitterInSubProjectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        Community projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .addMember(ePerson2).build();

        Collection publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "project", "shared", null,
                                ProjectConstants.PROJECT);

        Community subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                                         .withName("Sub Projects Community").build();

        Community subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                                    .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson1).build();

        CollectionBuilder.createCollection(context, subprojectAComm)
                         .withSubmitterGroup(subprojectAGroup)
                         .withName("Collection Sub Project A").build();

        Community subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        CollectionBuilder.createCollection(context, subprojectAComm)
                         .withSubmitterGroup(subprojectBGroup)
                         .withName("Collection Sub Project B").build();

        configurationService.setProperty("project.funding-community-name", subprojectsCommunity.getName());

        Item publicItem1 = ItemBuilder.createItem(context, publicationsCollection)
                                      .withTitle("project_" + projectsCommunity.getID().toString() + "_name")
                                      .withSharedProject(ProjectConstants.FUNDING)
                                      .build();

        StringBuilder placeholder = new StringBuilder();
        placeholder.append("project_").append(publicItem1.getID().toString()).append("_item");

        communityService.addMetadata(context, projectsCommunity, "dc", "relation", "project", null,
                      placeholder.toString(), publicItem1.getID().toString(), Choices.CF_ACCEPTED);

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String tokenEPerson1 = getAuthToken(ePerson1.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(tokenEPerson1).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", publicationsCollection.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(tokenEPerson1).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT, projectsCommunityGroup.getName())
                                       )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }
    }

    @Test
    public void createWorkspaceItemWithSubmitterTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        Community projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .addMember(ePerson2).build();

        Collection publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "project", "shared", null,
                                ProjectConstants.PROJECT);

        Community subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                                         .withName("Sub Projects Community").build();

        Community subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                                    .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson1).build();

        CollectionBuilder.createCollection(context, subprojectAComm)
                         .withSubmitterGroup(subprojectAGroup)
                         .withName("Collection Sub Project A").build();

        Community subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .build();

        CollectionBuilder.createCollection(context, subprojectAComm)
                         .withSubmitterGroup(subprojectBGroup)
                         .withName("Collection Sub Project B").build();

        configurationService.setProperty("project.funding-community-name", subprojectsCommunity.getName());

        Item publicItem1 = ItemBuilder.createItem(context, publicationsCollection)
                                      .withTitle("project_" + projectsCommunity.getID().toString() + "_name")
                                      .withSharedProject(ProjectConstants.FUNDING)
                                      .build();

        StringBuilder placeholder = new StringBuilder();
        placeholder.append("project_").append(publicItem1.getID().toString()).append("_item");

        communityService.addMetadata(context, projectsCommunity, "dc", "relation", "project", null,
                      placeholder.toString(), publicItem1.getID().toString(), Choices.CF_ACCEPTED);

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String tokenEPerson2 = getAuthToken(ePerson2.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(tokenEPerson2).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", publicationsCollection.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$._embedded.collection.id",
                                                 is(publicationsCollection.getID().toString())))
                                .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(tokenEPerson2).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$", Matchers.is(
                                                          WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                                                   ProjectConstants.PROJECT,
                                                                   projectsCommunityGroup.getName())
                                                          )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }
    }
}