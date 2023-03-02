/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static com.jayway.jsonpath.JsonPath.read;
import static org.dspace.project.util.ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
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
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
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
    private GroupService groupService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommunityService communityService;

    private Collection publicationColl;

    private Collection projectPartnerCollAll;

    private Collection projectPartnerCollClosed;

    private Community projectCommunity;

    private Community projectFundingCommunity;

    private Community fundingAllCommunity;

    private Community fundingClosedCommunity;

    private EPerson user1;

    private EPerson user2;

    private Group projectMemberGroup;

    private Group fundingAllMemberGroup;

    private Group fundingClosedMemberGroup;

    private Item projectItem;

    private Item fundingAllItem;

    private Item fundingClosedItem;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        user1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        user2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        Community projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects").build();

        configurationService.setProperty("project.parent-community-id", projectsCommunity.getID().toString());

        projectCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                                      .withTitle("Project")
                                                      .build();

        projectFundingCommunity = CommunityBuilder.createSubCommunity(context, projectCommunity)
                                                         .withName("Funding").build();

        Collection projectColl = CollectionBuilder.createCollection(context, projectCommunity)
                                                  .withEntityType(ProjectConstants.PROJECT_ENTITY)
                                                  .withName("Project").build();

        projectItem = ItemBuilder.createItem(context, projectColl)
                                 .withTitle("Project")
                                 .withSharedProject(ProjectConstants.PROJECT)
                                 .build();

        communityService.addMetadata(context, projectCommunity, MD_RELATION_ITEM_ENTITY.schema,
            MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
            null, projectItem.getID().toString(), projectItem.getID().toString(), Choices.CF_ACCEPTED);

        String groupName = String.format(PROJECT_MEMBERS_GROUP_TEMPLATE, projectCommunity.getID().toString());
        projectMemberGroup = GroupBuilder.createGroup(context)
                                         .withName(groupName)
                                         .addMember(user1)
                                         .addMember(user2)
                                         .build();

        publicationColl = CollectionBuilder.createCollection(context, projectCommunity)
                                                  .withName("Publication Collection")
                                                  .withEntityType("Publication")
                                                  .withSubmitterGroup(projectMemberGroup)
                                                  .withTemplateItem()
                                                  .build();

        Item templateItem = publicationColl.getTemplateItem();
        itemService.addMetadata(context, templateItem, ProjectConstants.MD_POLICY_GROUP.schema,
                ProjectConstants.MD_POLICY_GROUP.element, ProjectConstants.MD_POLICY_GROUP.qualifier, null,
                "###CURRENTPROJECTGROUP.project.members###");
        itemService.addMetadata(context, templateItem, ProjectConstants.MD_POLICY_SHARED.schema,
                ProjectConstants.MD_POLICY_SHARED.element, ProjectConstants.MD_POLICY_SHARED.qualifier, null,
                ProjectConstants.PROJECT);
        itemService.addMetadata(
            context, templateItem, ProjectConstants.MD_PROJECT_RELATION.schema,
            ProjectConstants.MD_PROJECT_RELATION.element, ProjectConstants.MD_PROJECT_RELATION.qualifier,
            null, "###CURRENTPROJECT.project###"
        );

        context.restoreAuthSystemState();

    }

    @After
    public void tearDown() {
        configurationService.setProperty("project.parent-community-id", "");
    }

    private void buildFunding(AtomicReference<Community> fundingRef, AtomicReference<Collection> projectPartner,
            AtomicReference<Group> fundingGroup, String grants, EPerson ...users) throws Exception {
        context.turnOffAuthorisationSystem();

        Community fundingCommunity = CommunityBuilder.createSubCommunity(context, projectFundingCommunity)
                .withName("Funding All Grants").build();

        Collection fundingCollAll = CollectionBuilder.createCollection(context, fundingCommunity)
                                                     .withEntityType(ProjectConstants.FUNDING_ENTITY)
                                                     .withSubmissionDefinition("traditional")
                                                     .withTemplateItem()
                                                     .withName("Funding").build();

        Item templateItem = publicationColl.getTemplateItem();
        itemService.addMetadata(
            context, templateItem, ProjectConstants.MD_PROJECT_RELATION.schema,
            ProjectConstants.MD_PROJECT_RELATION.element,
            ProjectConstants.MD_PROJECT_RELATION.qualifier, null,
            "###CURRENTPROJECT.project###"
        );

        Item fundingItem = ItemBuilder.createItem(context, fundingCollAll)
                                        .withTitle("Funding All Grants")
                                        .withSharedProject(grants)
                                        .build();

        communityService.addMetadata(context, fundingCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, fundingItem.getID().toString(), fundingItem.getID().toString(), Choices.CF_ACCEPTED);
        fundingRef.set(fundingCommunity);

        String groupName = String.format(FUNDING_MEMBERS_GROUP_TEMPLATE, fundingCommunity.getID().toString());

        Group fundingMemberGroup = GroupBuilder.createGroup(context)
                                     .withName(groupName)
                                     .build();
        for (EPerson user : users) {
            groupService.addMember(context, fundingMemberGroup, user);
        }
        fundingGroup.set(fundingMemberGroup);

        Collection projectPartnerColl = CollectionBuilder.createCollection(context, fundingCommunity)
                                          .withSubmitterGroup(fundingMemberGroup)
                                          .withEntityType(ProjectConstants.PROJECTPARTNER_ENTITY)
                                          .withSubmissionDefinition("projectpartners")
                                          .withName("Project partners")
                                          .withTemplateItem().build();

        templateItem = projectPartnerColl.getTemplateItem();
        itemService.addMetadata(context, templateItem, ProjectConstants.MD_POLICY_SHARED.schema,
                ProjectConstants.MD_POLICY_SHARED.element,
                ProjectConstants.MD_POLICY_SHARED.qualifier, null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, ProjectConstants.MD_FUNDING_RELATION.schema,
                ProjectConstants.MD_FUNDING_RELATION.element,
                ProjectConstants.MD_FUNDING_RELATION.qualifier, null,
                "###CURRENTPROJECT.funding###");

        projectPartner.set(projectPartnerColl);

        context.restoreAuthSystemState();
    }

    @Test
    public void createProjectEntityTest() throws Exception {

        AtomicReference<Community> fundComm = new AtomicReference<Community>();
        AtomicReference<Collection> ppColl = new AtomicReference<Collection>();
        AtomicReference<Group> fundGroup = new AtomicReference<Group>();
        buildFunding(fundComm, ppColl, fundGroup, ProjectConstants.PROJECT, user1);

        fundingAllCommunity = fundComm.get();
        projectPartnerCollAll = ppColl.get();
        fundingAllMemberGroup = fundGroup.get();

        buildFunding(fundComm, ppColl, fundGroup, ProjectConstants.FUNDING, user1, user2);

        fundingClosedCommunity = fundComm.get();
        projectPartnerCollClosed = ppColl.get();
        fundingClosedMemberGroup = fundGroup.get();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        AtomicReference<Integer> idRef2 = new AtomicReference<>();
        try {

        String tokenEPerson1 = getAuthToken(user1.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(tokenEPerson1).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", publicationColl.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$._embedded.collection.id",
                                             is(publicationColl.getID().toString())))
                                .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(tokenEPerson1).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT, projectMemberGroup)
                                       )));

         String tokenEPerson2 = getAuthToken(user2.getEmail(), password);

         // create a workspaceitem explicitly in the publicationsCollection
         getClient(tokenEPerson2).perform(post("/api/submission/workspaceitems")
                                 .param("owningCollection", publicationColl.getID().toString())
                                 .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                 .andExpect(status().isCreated())
                                 .andExpect(jsonPath("$._embedded.collection.id",
                                              is(publicationColl.getID().toString())))
                                 .andDo(result -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));

          getClient(tokenEPerson2).perform(get("/api/submission/workspaceitems/" + idRef2.get()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                       ProjectConstants.PROJECT, projectMemberGroup)
                                        )));

        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef2.get());
        }
    }

    @Test
    public void createFundingEntityTest() throws Exception {

        AtomicReference<Community> fundComm = new AtomicReference<Community>();
        AtomicReference<Collection> ppColl = new AtomicReference<Collection>();
        AtomicReference<Group> fundGroup = new AtomicReference<Group>();
        buildFunding(fundComm, ppColl, fundGroup, ProjectConstants.PROJECT, user1);

        fundingAllCommunity = fundComm.get();
        projectPartnerCollAll = ppColl.get();
        fundingAllMemberGroup = fundGroup.get();

        buildFunding(fundComm, ppColl, fundGroup, ProjectConstants.FUNDING, user1, user2);

        fundingClosedCommunity = fundComm.get();
        projectPartnerCollClosed = ppColl.get();
        fundingClosedMemberGroup = fundGroup.get();

        AtomicReference<Integer> idRef1 = new AtomicReference<>();
        AtomicReference<Integer> idRef2 = new AtomicReference<>();
        try {

        String tokenEPerson1 = getAuthToken(user1.getEmail(), password);

        // create a workspaceitem explicitly in the publicationsCollection
        getClient(tokenEPerson1).perform(post("/api/submission/workspaceitems")
                                .param("owningCollection", projectPartnerCollAll.getID().toString())
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$._embedded.collection.id",
                                             is(projectPartnerCollAll.getID().toString())))
                                .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));

         getClient(tokenEPerson1).perform(get("/api/submission/workspaceitems/" + idRef1.get()))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                      ProjectConstants.PROJECT, projectMemberGroup)
                                       )));

         String tokenEPerson2 = getAuthToken(user2.getEmail(), password);

         // create a workspaceitem explicitly in the publicationsCollection
         getClient(tokenEPerson2).perform(post("/api/submission/workspaceitems")
                                 .param("owningCollection", projectPartnerCollClosed.getID().toString())
                                 .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                                 .andExpect(status().isCreated())
                                 .andExpect(jsonPath("$._embedded.collection.id",
                                              is(projectPartnerCollClosed.getID().toString())))
                                 .andDo(result -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));

          getClient(tokenEPerson2).perform(get("/api/submission/workspaceitems/" + idRef2.get()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", Matchers.is(WorkspaceItemMatcher.matchPolicyGroupAndSharedMetadata(
                                       ProjectConstants.FUNDING, fundingClosedMemberGroup)
                                        )));

        } finally {
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef1.get());
            WorkspaceItemBuilder.deleteWorkspaceItem(idRef2.get());
        }
    }
}