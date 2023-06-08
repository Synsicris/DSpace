/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static com.jayway.jsonpath.JsonPath.read;
import static java.lang.String.join;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.dspace.project.util.ProjectConstants.FUNDER;
import static org.dspace.project.util.ProjectConstants.GROUP_POLICY_PLACEHOLDER;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_SHARED;
import static org.dspace.project.util.ProjectConstants.MD_PROJECT_RELATION;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.MediaType;

import org.dspace.app.rest.model.patch.Operation;
import org.dspace.app.rest.model.patch.ReplaceOperation;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.authorize.AuthorizeException;
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
import org.dspace.event.factory.EventServiceFactory;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.submit.consumer.ProjectEditGrantsConsumer;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite to verify the related entities creation via {@link ProjectEditGrantsConsumer}.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectEditGrantsConsumerIT extends AbstractControllerIntegrationTest {

    private static String[] consumers;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommunityService communityService;

    private Community projectsCommunity;
    private Community fundingRootCommunity;
    private Community fundingAComm;
    private Community projectCommunity;
    private Collection projectCollection;
    private Collection publicationsCollection;
    private Collection fundingProjectsCollection;
    private Collection projectPartnerCollection;
    private Item fundingItem;
    private Item projectPartner;
    private Item projectItem;

    private EPerson ePersonFunding;
    private EPerson ePersonProject;
    private Group funderGroup;
    private Group projectsCommunityGroup;
    private Group fundingCommunityGroup;

    private String projectPartnerProp;
    private String funderGroupProperty;
    private String projectFundingCommunityProp;

    @BeforeClass
    public static void setupConfiguration() {
        ConfigurationService configurationService =
            DSpaceServicesFactory.getInstance().getConfigurationService();
        consumers = configurationService.getArrayProperty("event.dispatcher.default.consumers");
        String newConsumers = consumers.length > 0 ? join(",", consumers) + "," + "projectedit" : "projectedit";
        configurationService.setProperty("event.dispatcher.default.consumers", newConsumers);
        EventServiceFactory.getInstance().getEventService().reloadConfiguration();
    }

    @AfterClass
    public static void restoreConfiguration() {
        ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        configurationService
            .setProperty(
                "event.dispatcher.default.consumers",
                join(",", consumers)
            );
        EventServiceFactory.getInstance().getEventService().reloadConfiguration();
    }

    @Test
    public void patchSharedProjectMetadataOfFundingReflectsPolicyOnRelatedEntities() throws Exception {
        loadProperties();

        try {

            createEnvironment();

            AtomicReference<Integer> idRef = new AtomicReference<>();
            try {
                String authToken = getAuthToken(ePersonProject.getEmail(), password);

                getClient(authToken)
                    .perform(
                        post("/api/submission/workspaceitems")
                            .param("owningCollection", publicationsCollection.getID().toString())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(
                        jsonPath(
                            "$._embedded.collection.id",
                            is(publicationsCollection.getID().toString())
                        )
                    )
                    .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

                authToken = getAuthToken(admin.getEmail(), password);

                List<Operation> ops = new ArrayList<Operation>();
                ReplaceOperation replaceOperation = new ReplaceOperation("/metadata/cris.project.shared", "project");
                ops.add(replaceOperation);
                String patchBody = getPatchContent(ops);

                getClient(authToken)
                    .perform(
                        patch("/api/core/items/" + fundingItem.getID())
                            .content(patchBody)
                            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid", Matchers.is(fundingItem.getID().toString())))
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "project", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    projectsCommunityGroup.getName(),
                                    projectsCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

                getClient(authToken)
                    .perform(
                        get("/api/core/items/" + projectPartner.getID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "project", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    projectsCommunityGroup.getName(),
                                    projectsCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

                getClient(authToken)
                    .perform(
                        get("/api/core/items/" + projectItem.getID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                    .andExpect(status().isOk())
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "project", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    projectsCommunityGroup.getName(),
                                    projectsCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

                ops = new ArrayList<Operation>();
                replaceOperation = new ReplaceOperation("/metadata/cris.project.shared", "funding");
                ops.add(replaceOperation);
                patchBody = getPatchContent(ops);

                getClient(authToken)
                    .perform(
                        patch("/api/core/items/" + fundingItem.getID())
                            .content(patchBody)
                            .contentType(MediaType.APPLICATION_JSON_PATCH_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid", Matchers.is(fundingItem.getID().toString())))
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "funding", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    fundingCommunityGroup.getName(),
                                    fundingCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

                getClient(authToken)
                    .perform(
                        get("/api/core/items/" + projectPartner.getID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "funding", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    fundingCommunityGroup.getName(),
                                    fundingCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

                getClient(authToken)
                    .perform(
                        get("/api/core/items/" + projectItem.getID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                    .andExpect(status().isOk())
                    .andExpect(
                        jsonPath(
                            "$.metadata",
                            allOf(
                                matchMetadata("cris.project.shared", "project", 0),
                                matchMetadata(
                                    "cris.policy.group",
                                    projectsCommunityGroup.getName(),
                                    projectsCommunityGroup.getID().toString(),
                                    0
                                )
                            )
                        )
                    );

            } finally {
                WorkspaceItemBuilder.deleteWorkspaceItem(idRef.get());
            }
        } finally {
            restoreProperties();
        }

    }

    protected void createEnvironment() throws SQLException, AuthorizeException {
        context.turnOffAuthorisationSystem();
        funderGroup = GroupBuilder.createGroup(context)
            .withName("Test Funder Group")
            .build();

        configurationService.setProperty("project.funder.group", funderGroup.getID());

        ePersonFunding = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Vins", "Funder")
            .withEmail("vins@funder.synsicris")
            .withPassword(password)
            .build();

        ePersonProject = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Vins", "Project")
            .withEmail("vins@project.synsicris")
            .withPassword(password)
            .build();

        projectsCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("Projects Community").build();


        configurationService.setProperty("project.parent-community-id", projectsCommunity.getID().toString());

        projectCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
            .withName("Project Name")
            .build();

        projectsCommunityGroup = GroupBuilder.createGroup(context)
            .withName("project_" + projectCommunity.getID().toString() + "_members_group")
            .addMember(ePersonProject)
            .build();

        fundingRootCommunity =
            CommunityBuilder.createSubCommunity(context, projectCommunity)
            .withName("Funding")
            .build();

        communityService.addMetadata(
            context, fundingRootCommunity,
            "synsicris", "funding", "community",
            Item.ANY, "true"
        );

        configurationService.setProperty("project.funding-community-name", fundingRootCommunity.getName());

        projectCollection =
            CollectionBuilder.createCollection(context, projectCommunity)
                .withName("Consortia")
                .withEntityType("Project")
                .withSubmitterGroup(projectsCommunityGroup)
                .build();

        projectItem =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Cool Project")
                .withPolicyGroup(GROUP_POLICY_PLACEHOLDER)
                .withSharedProject("project")
                .build();

        communityService
            .addMetadata(
                context, projectCommunity,
                MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element,
                MD_RELATION_ITEM_ENTITY.qualifier,
                null, projectItem.getName(),
                projectItem.getID().toString(),
                Choices.CF_ACCEPTED
            );

        fundingAComm =
            CommunityBuilder.createSubCommunity(context, fundingRootCommunity)
                .withName("Funding name")
                .build();

        fundingCommunityGroup = GroupBuilder.createGroup(context)
            .withName("funding_" + fundingAComm.getID().toString() + "_members_group")
            .addMember(ePersonFunding)
            .build();

        fundingProjectsCollection = CollectionBuilder.createCollection(context, fundingAComm)
            .withName("Projects")
            .withEntityType("Funding")
            .build();

        fundingItem = ItemBuilder.createItem(context, fundingProjectsCollection)
            .withTitle("Cool Funding Project")
            .withSynsicrisRelationProject(projectItem.getName(), projectItem.getID().toString())
            .withPolicyGroup(GROUP_POLICY_PLACEHOLDER)
            .withSharedProject(FUNDER)
            .build();

        communityService
            .addMetadata(
                context, fundingRootCommunity,
                MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element,
                MD_RELATION_ITEM_ENTITY.qualifier,
                null, fundingItem.getName(),
                fundingItem.getID().toString(),
                Choices.CF_ACCEPTED
            );

        projectPartnerCollection = CollectionBuilder.createCollection(context, fundingAComm)
            .withName("Project Partners")
            .withEntityType("projectpartner")
            .build();

        projectPartner = ItemBuilder.createItem(context, projectPartnerCollection)
            .withSynsicrisRelationFunding(fundingItem.getName(), fundingItem.getID().toString())
            .withSynsicrisRelationProject(projectItem.getName(), projectItem.getID().toString())
            .withPolicyGroup(GROUP_POLICY_PLACEHOLDER)
            .withSharedProject(FUNDER)
            .build();

        publicationsCollection =
            CollectionBuilder.createCollection(context, projectsCommunity)
                .withName("Publication Collection")
                .withSubmitterGroup(projectsCommunityGroup)
                .withTemplateItem()
                .build();

        configureProjectRelatedTemplateItem(publicationsCollection);
        context.restoreAuthSystemState();
    }

    protected void restoreProperties() {
        configurationService.setProperty("project.parent-community-id", projectPartnerProp);
        configurationService.setProperty("project.funder.group", funderGroupProperty);
        configurationService.setProperty("project.funding-community-name", projectFundingCommunityProp);
    }

    protected void loadProperties() {
        projectPartnerProp = configurationService.getProperty("project.parent-community-id");
        funderGroupProperty = configurationService.getProperty("project.funder.group");
        projectFundingCommunityProp = configurationService.getProperty("project.funding-community-name");
    }

    protected void configureProjectRelatedTemplateItem(Collection publicationsCollection) throws SQLException {
        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(
            context,
            templateItem,
            MD_POLICY_GROUP.schema,
            MD_POLICY_GROUP.element,
            MD_POLICY_GROUP.qualifier,
            null,
            GROUP_POLICY_PLACEHOLDER
        );
        itemService.addMetadata(
            context,
            templateItem,
            MD_POLICY_SHARED.schema,
            MD_POLICY_SHARED.element,
            MD_POLICY_SHARED.qualifier,
            null,
            FUNDER
        );
        itemService.addMetadata(
            context,
            templateItem,
            MD_PROJECT_RELATION.schema,
            MD_PROJECT_RELATION.element,
            MD_PROJECT_RELATION.qualifier,
            null,
            projectItem.getName(),
            projectItem.getID().toString(),
            Choices.CF_ACCEPTED
        );
    }

}