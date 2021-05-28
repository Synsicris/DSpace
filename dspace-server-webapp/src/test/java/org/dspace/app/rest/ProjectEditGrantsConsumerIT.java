/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static com.jayway.jsonpath.JsonPath.read;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadataDoesNotExist;
import static org.hamcrest.Matchers.allOf;
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
import org.dspace.builder.WorkspaceItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite to verify the related entities creation via {@link ProjectEditGrantsConsumer}.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectEditGrantsConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    private Community projectsCommunity;
    private Community subprojectsCommunity;
    private Community subprojectAComm;
    private Community subprojectBComm;
    private Collection publicationsCollection;
    @SuppressWarnings("unused")
    private Collection collectionSubProjectA;
    @SuppressWarnings("unused")
    private Collection collectionSubProjectB;

    @Test
    public void createWorkspaceItemWithSubmitterUsing_parentprojectTest() throws Exception {
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

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null,
                                ProjectConstants.PARENTPROJECT);

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
        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String authToken = getAuthToken(ePerson1.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(authToken).perform(post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PARENTPROJECT,
                                      "project_" + projectsCommunity.getID().toString() + "_members_group")
                                       )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }
    }

    @Test
    public void createWorkspaceItemWithSubmitterUsing_projectTest() throws Exception {
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

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null,
                                ProjectConstants.PROJECT);

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

        configurationService.setProperty("project.subproject-community-name", subprojectsCommunity.getName());
        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String authToken = getAuthToken(ePerson2.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(authToken).perform(post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT,
                                      "project_" + subprojectBComm.getID().toString() + "_members_group")
                                       )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }
    }


    @Test
    public void createWorkspaceItemWithAdminUsing_projectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null, ProjectConstants.PROJECT);

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(admin)
                       .build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectBComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        configurationService.setProperty("project.subproject-community-name", subprojectsCommunity.getName());

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String authToken = getAuthToken(admin.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(authToken).perform(post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT,
                                      "project_" + subprojectAComm.getID().toString() + "_members_group")
                                       )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }

    }

    @Test
    public void createWorkspaceItemWithAdminOfCommunityUsing_projectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community")
                                            .withAdminGroup(ePerson1)
                                            .build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(eperson).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null, ProjectConstants.PROJECT);

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity")
                                          .build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity")
                                          .withAdminGroup(ePerson1).build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson1)
                       .build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectBComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        configurationService.setProperty("project.subproject-community-name", subprojectsCommunity.getName());

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String authToken = getAuthToken(ePerson1.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(authToken).perform(post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT,
                                      "project_" + subprojectAComm.getID().toString() + "_members_group")
                                       )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }

    }

    @Test
    public void createWorkspaceItemWithGroupPolicyAndWithoutSharedMetadataTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        try {

        String authToken = getAuthToken(ePerson1.getEmail(), password);

        getClient(authToken).perform(post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationsCollection.getID().toString())))
                            .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", allOf(hasJsonPath("$._embedded.item.metadata", allOf(
                                   matchMetadataDoesNotExist("cris.workspace.shared"),
                                               matchMetadata("cris.policy.group", "GROUP_POLICY_PLACEHOLDER")
                                               )))));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
        }
    }

    @Test
    public void createWorkspaceItemWithSubmitterUsing_sharedValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Group sharedGroup = GroupBuilder.createGroup(context)
                                        .withName("Test Shared Group")
                                        .build();

        configurationService.setProperty("project.creation.group", sharedGroup.getID());

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                                         .withNameInMetadata("Mykhaylo", "Boychuk")
                                         .withEmail("mykhaylo.boychuk@email.com")
                                         .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null,
                                ProjectConstants.SHARED);

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef = new AtomicReference<>();
        try {
            String authToken = getAuthToken(ePerson1.getEmail(), password);

            getClient(authToken).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", publicationsCollection.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$._embedded.collection.id",
                                                 is(publicationsCollection.getID().toString())))
                                .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef.get()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher
                                          .matchPolicyGroupAndSharedMetadata(
                                                ProjectConstants.SHARED, sharedGroup.getName())
                                          )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef.get());
        }
    }

    @Test
    public void createWorkspaceItemWithSubmitterUsing_funderValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Group funderGroup = GroupBuilder.createGroup(context)
                                        .withName("Test Funder Group")
                                        .build();

        configurationService.setProperty("project.funder.group", funderGroup.getID());

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                                         .withNameInMetadata("Mykhaylo", "Boychuk")
                                         .withEmail("mykhaylo.boychuk@email.com")
                                         .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1)
                     .build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null,
                                ProjectConstants.FUNDER);

        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef = new AtomicReference<>();
        try {
            String authToken = getAuthToken(ePerson1.getEmail(), password);

            getClient(authToken).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", publicationsCollection.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$._embedded.collection.id",
                                                 is(publicationsCollection.getID().toString())))
                                .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(authToken).perform(get("/api/submission/workspaceitems/" + idRef.get()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher
                                          .matchPolicyGroupAndSharedMetadata(
                                                ProjectConstants.FUNDER, funderGroup.getName())
                                          )));
        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef.get());
        }
    }

}