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
import static junit.framework.TestCase.assertEquals;
import static org.dspace.builder.CollectionBuilder.createCollection;
import static org.dspace.builder.CommunityBuilder.createCommunity;
import static org.dspace.builder.CommunityBuilder.createSubCommunity;
import static org.dspace.project.util.ProjectConstants.DEFAULT_CURRENCY;
import static org.dspace.project.util.ProjectConstants.DEFAULT_STATUS;
import static org.dspace.project.util.ProjectConstants.FUNDER_PROJECT_MANAGERS_GROUP;
import static org.dspace.project.util.ProjectConstants.GROUP_POLICY_PLACEHOLDER;
import static org.dspace.project.util.ProjectConstants.MD_CURRENCY;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_SHARED;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
import static org.dspace.project.util.ProjectConstants.PROJECT;
import static org.dspace.project.util.ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.TEMPLATE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.matcher.CollectionMatcher;
import org.dspace.app.rest.matcher.CommunityMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringEndsWith;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class CommunitySynsicrisWorkflowIT extends AbstractControllerIntegrationTest {

    private static final String project_template_funders_group =
        String.format(
            PROJECT_FUNDERS_GROUP_TEMPLATE,
            TEMPLATE
        );
    private static final String project_template_readers_group =
        String.format(
            PROJECT_READERS_GROUP_TEMPLATE,
            TEMPLATE
        );
    private static final String project_template_coordinators_group =
        String.format(
            PROJECT_COORDINATORS_GROUP_TEMPLATE,
            TEMPLATE
        );
    private static final String project_template_members_group =
        String.format(
            PROJECT_MEMBERS_GROUP_TEMPLATE,
            TEMPLATE
        );

    private static final String project_template_groups_name = "project.template.groups-name";
    private static final String project_template_add_user_groups = "project.template.add-user-groups";
    private String funder_project_managers_group = FUNDER_PROJECT_MANAGERS_GROUP;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Autowired
    private InstallItemService installItemService;

    @Test
    public void cloneCommunityRootTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Group adminGroup = GroupBuilder.createGroup(context).build();
        Group funderGroup = GroupBuilder.createGroup(context).build();
        Group membersGroup = GroupBuilder.createGroup(context).build();
        Group readGroup = GroupBuilder.createGroup(context).build();
        Group projAdminGroup = GroupBuilder.createGroup(context).build();

        Group collectionGroupA =
            GroupBuilder.createGroup(context)
                .withName("Group A")
                .build();

        Group collectionGroupB =
            GroupBuilder.createGroup(context)
                .withName("Group B")
                .build();

        groupService.setName(adminGroup, project_template_coordinators_group);
        groupService.setName(funderGroup, project_template_funders_group);
        groupService.setName(membersGroup, project_template_members_group);
        groupService.setName(readGroup, project_template_readers_group);
        groupService.setName(projAdminGroup, funder_project_managers_group);

        configurationService.setProperty(
            project_template_groups_name,
            List.of(
                project_template_funders_group,
                project_template_coordinators_group,
                project_template_readers_group,
                project_template_members_group
            )
        );

        parentCommunity = createCommunity(context)
                .withName("Parent Community")
                .withAdminGroup(projAdminGroup)
                .build();

        Community child1 = createSubCommunity(context, parentCommunity)
            .withName("Sub Community 1")
            .build();

        Community child2 = createSubCommunity(context, parentCommunity)
            .withName("Sub Community 2")
            .build();

        Collection col = createCollection(context, parentCommunity)
            .withName("Collection of parent Community")
            .withSubmitterGroup(membersGroup)
            .withAdminGroup(adminGroup)
            .withWorkflowGroup(1, collectionGroupA)
            .withWorkflowGroup(2, collectionGroupB)
            .build();

        Collection child1Col1 = createCollection(context, child1)
            .withName("Child 1 Collection 1")
            .build();
        Collection child2Col1 = createCollection(context, child2)
            .withName("Child 2 Collection 1")
            .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server/api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isCreated())
                     .andDo(result -> idRef
                             .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))))
                     .andExpect(jsonPath("$", Matchers.allOf(
                             hasJsonPath("$.name", is("My new Community")),
                             hasJsonPath("$.id", is(idRef.get().toString())),
                             hasJsonPath("$.id", not(parentCommunity.getID().toString()))
                             )))
                     .andExpect(jsonPath("$._embedded.collections._embedded.collections", Matchers.contains(
                             CollectionMatcher.matchClone(col)
                             )))
                    .andExpect(jsonPath("$._embedded.subcommunities._embedded.subcommunities",
                               Matchers.containsInAnyOrder(
                                        Matchers.allOf(CommunityMatcher.matchClone(child1),
                                                 hasJsonPath("$._embedded.collections._embedded.collections",
                                                 Matchers.contains(CollectionMatcher.matchClone(child1Col1)))),
                                        Matchers.allOf(CommunityMatcher.matchClone(child2),
                                                 hasJsonPath("$._embedded.collections._embedded.collections",
                                                 Matchers.contains(CollectionMatcher.matchClone(child2Col1))))
                            )));

            Community newCommunity = communityService.find(context, idRef.get());
            assertEquals("My new Community", newCommunity.getName());
            assertNotEquals(parentCommunity.getID(), newCommunity.getID());

            String funders_Group =
                project_template_funders_group.replaceAll(
                    "template",
                    newCommunity.getID().toString()
                );
            String admin_Group =
                project_template_coordinators_group.replaceAll("template", newCommunity.getID().toString());
            String members_Group =
                project_template_members_group.replaceAll(
                    "template",
                    newCommunity.getID().toString()
                );
            String read_Group = project_template_readers_group.replaceAll("template", newCommunity.getID().toString());

            Group groupAdmin = groupService.findByName(context, admin_Group);
            Group groupMembers = groupService.findByName(context, members_Group);
            Group groupRead = groupService.findByName(context, read_Group);
            Group groupFunders = groupService.findByName(context, funders_Group);

            assertEquals(groupFunders.getName(), funders_Group);
            assertEquals(groupAdmin.getName(), admin_Group);
            assertEquals(groupMembers.getName(), members_Group);
            assertEquals(groupRead.getName(), read_Group);

            MatcherAssert.assertThat(
                groupFunders.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );
            MatcherAssert.assertThat(
                groupAdmin.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );
            MatcherAssert.assertThat(
                groupMembers.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );
            MatcherAssert.assertThat(
                groupRead.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );

            List<Community> communities = newCommunity.getSubcommunities();
            assertEquals(2, communities.size());
            Community firstChild = communities.get(0);
            Community secondChild = communities.get(1);
            boolean child1Found = StringUtils.equals(firstChild.getName(), child1.getName())
                                                     || StringUtils.equals(secondChild.getName(), child1.getName());
            boolean child2Found = StringUtils.equals(firstChild.getName(), child2.getName())
                                                     || StringUtils.equals(secondChild.getName(), child2.getName());
            assertTrue(child1Found);
            assertTrue(child2Found);
            assertNotEquals(firstChild.getID(), child1.getID());
            assertNotEquals(firstChild.getID(), child2.getID());
            assertEquals(1, firstChild.getCollections().size());
            assertEquals(1, secondChild.getCollections().size());

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    @Test
    public void cloneProjectCommunity() throws Exception {
        context.turnOffAuthorisationSystem();

        Group adminGroup =
            GroupBuilder.createGroup(context)
                .withName(funder_project_managers_group)
                .build();

        Community cloneTarget =
            CommunityBuilder.createCommunity(context)
                .withName("Community to hold cloned communities")
                .withAdminGroup(adminGroup)
                .build();

        GroupBuilder.createGroup(context).withName(project_template_coordinators_group).build();
        GroupBuilder.createGroup(context).withName(project_template_funders_group).build();
        GroupBuilder.createGroup(context).withName(project_template_members_group).build();
        GroupBuilder.createGroup(context).withName(project_template_readers_group).build();

        configurationService.setProperty(
            project_template_groups_name,
            List.of(
                project_template_funders_group,
                project_template_coordinators_group,
                project_template_readers_group,
                project_template_members_group
            )
        );
        configurationService.setProperty(project_template_add_user_groups, List.of(project_template_funders_group));
        configurationService.setProperty("project.parent-community-id", cloneTarget.getID().toString());

        Community parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();

        Community child1 =
            CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community 1")
                .build();

        Community child2 =
            CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community 2")
                .build();

        Collection col =
            CollectionBuilder.createCollection(context, parentCommunity)
                .withName("Collection of parent Community")
                .withTemplateItem()
                .build();

        final Item colTemplateItem = col.getTemplateItem();
        this.itemService
            .setMetadataSingleValue(context, colTemplateItem, MD_POLICY_GROUP, null, GROUP_POLICY_PLACEHOLDER);
        this.itemService.setMetadataSingleValue(context, colTemplateItem, MD_POLICY_SHARED, null, PROJECT);
        this.itemService.setMetadataSingleValue(
            context, colTemplateItem, MD_CURRENCY, null, DEFAULT_CURRENCY
        );
        this.itemService.setMetadataSingleValue(
            context, colTemplateItem, ProjectConstants.MD_PROJECT_STATUS, null, DEFAULT_STATUS
        );
        this.itemService.update(context, colTemplateItem);
        col = context.reloadEntity(col);

        Collection child1Col1 =
            CollectionBuilder.createCollection(context, child1)
                .withName("Child 1 Collection 1")
                .build();
        Collection child2Col1 =
            CollectionBuilder.createCollection(context, child2)
                .withName("Child 2 Collection 1")
                .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .param("parent", cloneTarget.getID().toString())
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isCreated())
                     .andDo(result -> idRef
                             .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))))
                     .andExpect(jsonPath("$", Matchers.allOf(
                             hasJsonPath("$.name", is("My new Community")),
                             hasJsonPath("$.id", is(idRef.get().toString())),
                             hasJsonPath("$.id", not(parentCommunity.getID().toString()))
                             )))
                     .andExpect(jsonPath("$._embedded.collections._embedded.collections", Matchers.contains(
                             CollectionMatcher.matchClone(col)
                             )))
                    .andExpect(jsonPath("$._embedded.subcommunities._embedded.subcommunities",
                               Matchers.containsInAnyOrder(
                                        Matchers.allOf(CommunityMatcher.matchClone(child1),
                                                 hasJsonPath("$._embedded.collections._embedded.collections",
                                                 Matchers.contains(CollectionMatcher.matchClone(child1Col1)))),
                                        Matchers.allOf(CommunityMatcher.matchClone(child2),
                                                 hasJsonPath("$._embedded.collections._embedded.collections",
                                                 Matchers.contains(CollectionMatcher.matchClone(child2Col1))))
                            )));
            cloneTarget = context.reloadEntity(cloneTarget);
            Community subCommunityOfCloneTarget = cloneTarget.getSubcommunities().get(0);
            assertEquals(subCommunityOfCloneTarget.getID().toString(), idRef.toString());
            assertEquals("My new Community", subCommunityOfCloneTarget.getName());
            assertNotEquals(parentCommunity.getID(), idRef.toString());
            List<Community> communities = subCommunityOfCloneTarget.getSubcommunities();
            List<Collection> collections = subCommunityOfCloneTarget.getCollections();

            String funders_Group = project_template_funders_group.replaceAll("template", idRef.toString());
            String admin_Group = project_template_coordinators_group.replaceAll("template", idRef.toString());
            String members_Group = project_template_members_group.replaceAll("template", idRef.toString());
            String read_Group = project_template_readers_group.replaceAll("template", idRef.toString());

            Group groupAdmin = groupService.findByName(context, admin_Group);
            Group groupMembers = groupService.findByName(context, members_Group);
            Group groupRead = groupService.findByName(context, read_Group);
            Group groupFunders = groupService.findByName(context, funders_Group);

            assertEquals(groupFunders.getName(), funders_Group);
            assertEquals(groupAdmin.getName(), admin_Group);
            assertEquals(groupMembers.getName(), members_Group);
            assertEquals(groupRead.getName(), read_Group);

            MatcherAssert.assertThat(
                groupFunders.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );
            MatcherAssert.assertThat(
                groupAdmin.getMembers(),
                not(
                    Matchers.hasItem(
                            hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );
            MatcherAssert.assertThat(
                groupMembers.getMembers(),
                not(
                    Matchers.hasItem(
                        hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );
            MatcherAssert.assertThat(
                groupRead.getMembers(),
                not(
                    Matchers.hasItem(
                        hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );

            assertEquals(2, communities.size());
            assertEquals(1, collections.size());
            Community firstChild = communities.get(0);
            Community secondChild = communities.get(1);
            boolean child1Found = StringUtils.equals(firstChild.getName(), child1.getName())
                                                     || StringUtils.equals(secondChild.getName(), child1.getName());
            boolean child2Found = StringUtils.equals(firstChild.getName(), child2.getName())
                                                     || StringUtils.equals(secondChild.getName(), child2.getName());
            assertTrue(child1Found);
            assertTrue(child2Found);
            assertNotEquals(firstChild.getID(), child1.getID());
            assertNotEquals(firstChild.getID(), child2.getID());
            assertEquals(1, firstChild.getCollections().size());
            assertEquals(1, secondChild.getCollections().size());

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    @Test
    public void cloneProjectCommunityPolicy() throws Exception {
        context.turnOffAuthorisationSystem();

        Group adminGroup =
            GroupBuilder.createGroup(context)
                .withName(funder_project_managers_group)
                .build();

        Community sharedCoummunity =
            CommunityBuilder.createCommunity(context)
                .withName("Shared")
                .build();

        Community projectsCommunityHolder =
            CommunityBuilder.createCommunity(context)
                .withName("Community to hold cloned communities")
                .withAdminGroup(adminGroup)
                .build();

        Collection researchProfileCollection =
            CollectionBuilder.createCollection(context, sharedCoummunity)
            .withName("Persons")
            .withAdminGroup(adminGroup)
            .build();

        GroupBuilder.createGroup(context).withName(project_template_coordinators_group).build();
        GroupBuilder.createGroup(context).withName(project_template_funders_group).build();
        GroupBuilder.createGroup(context).withName(project_template_members_group).build();
        GroupBuilder.createGroup(context).withName(project_template_readers_group).build();

        configurationService.setProperty(
            project_template_groups_name,
            List.of(
                project_template_funders_group,
                project_template_coordinators_group,
                project_template_readers_group,
                project_template_members_group
            )
        );
        configurationService.setProperty(project_template_add_user_groups, List.of(project_template_funders_group));
        configurationService.setProperty("project.parent-community-id", projectsCommunityHolder.getID().toString());
        configurationService
            .setProperty("researcher-profile.collection.uuid", researchProfileCollection.getID().toString());

        Community projectTemplateCommunity =
            CommunityBuilder.createCommunity(context).withName("project-template").build();

        Community funding =
            CommunityBuilder.createSubCommunity(context, projectTemplateCommunity).withName("Funding")
                .build();

        Collection consortia =
            CollectionBuilder.createCollection(context, projectTemplateCommunity)
                .withName("Consortia")
                .withEntityType(ProjectConstants.PROJECT_ENTITY)
                .withTemplateItem()
                .build();

        final Item colTemplateItem = consortia.getTemplateItem();
        this.itemService
            .setMetadataSingleValue(context, colTemplateItem, MD_POLICY_GROUP, null, GROUP_POLICY_PLACEHOLDER);
        this.itemService
            .setMetadataSingleValue(context, colTemplateItem, ProjectConstants.MD_PROJECT_STATUS, null, DEFAULT_STATUS);
        this.itemService.setMetadataSingleValue(context, colTemplateItem, MD_POLICY_SHARED, null, PROJECT);
        this.itemService.setMetadataSingleValue(context, colTemplateItem, MD_CURRENCY, null, DEFAULT_CURRENCY);
        String templateItemName = "template_item";
        this.itemService.setMetadataSingleValue(context, colTemplateItem, "dc", "title", null, null, templateItemName);
        this.itemService.update(context, colTemplateItem);

        WorkspaceItem workspaceItem = workspaceItemService.create(context, consortia, true);
        Item consortiaItem = installItemService.installItem(context, workspaceItem);
        String consortiaItemName = "project_item_name";
        this.itemService.setMetadataSingleValue(context, consortiaItem, "dc", "title", null, null, consortiaItemName);
        this.itemService.addMetadata(
            context, consortiaItem, "synsicris", "relation", "workingplan", null, "Workingplan",
            "will be referenced::SB-ID::wp_001", Choices.CF_ACCEPTED
        );

        communityService.addMetadata(
            context,
            projectTemplateCommunity,
            MD_RELATION_ITEM_ENTITY.schema,
            MD_RELATION_ITEM_ENTITY.element,
            MD_RELATION_ITEM_ENTITY.qualifier,
            null,
            consortiaItemName,
            consortiaItem.getID().toString(),
            Choices.CF_ACCEPTED
        );

        this.communityService.update(context, projectTemplateCommunity);

        consortia = context.reloadEntity(consortia);
        projectTemplateCommunity = context.reloadEntity(projectTemplateCommunity);
        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(
                post("/api/core/communities")
                    .param("projection", "full")
                    .param("name", "My new Community")
                    .param("parent", projectsCommunityHolder.getID().toString())
                    .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                    .content("https://localhost:8080/server//api/core/communities/" + projectTemplateCommunity.getID())
            )
                .andExpect(
                    status()
                        .isCreated()
                )
                .andDo(
                    result -> idRef
                        .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id")))
                )
                .andExpect(
                    jsonPath(
                        "$", Matchers.allOf(
                            hasJsonPath("$.name", is("My new Community")),
                            hasJsonPath("$.id", is(idRef.get().toString())),
                            hasJsonPath("$.id", not(projectTemplateCommunity.getID().toString()))
                        )
                    )
                )
                .andExpect(
                    jsonPath(
                        "$.metadata.['synsicris.relation.entity_item'][*].value",
                        hasItem(
                            equalTo("My new Community")
                        )
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.collections._embedded.collections", Matchers.contains(
                            CollectionMatcher.matchClone(consortia)
                        )
                    )
                );

            projectsCommunityHolder = context.reloadEntity(projectsCommunityHolder);
            Community subCommunityOfCloneTarget = projectsCommunityHolder.getSubcommunities().get(0);
            assertEquals(subCommunityOfCloneTarget.getID().toString(), idRef.toString());
            assertEquals("My new Community", subCommunityOfCloneTarget.getName());
            assertNotEquals(projectTemplateCommunity.getID(), idRef.toString());
            List<Community> communities = subCommunityOfCloneTarget.getSubcommunities();
            List<Collection> collections = subCommunityOfCloneTarget.getCollections();

            List<MetadataValue> metadata =
                this.communityService.getMetadata(
                    subCommunityOfCloneTarget,
                    "synsicris.relation.entity_item",
                    Item.ANY
                );
            assertEquals(1, metadata.size());
            MetadataValue newConsortiaRef = metadata.get(0);
            UUID uuidTemplate = UUID.fromString(newConsortiaRef.getAuthority());
            Item newConsortiaItem = this.itemService.find(context, uuidTemplate);
            this.containsMetadataValue(
                newConsortiaItem,
                "cris",
                "policy",
                "group",
                StringEndsWith.endsWith("_members_group")
            );

            String funders_Group = project_template_funders_group.replaceAll("template", idRef.toString());
            String admin_Group = project_template_coordinators_group.replaceAll("template", idRef.toString());
            String members_Group = project_template_members_group.replaceAll("template", idRef.toString());
            String read_Group = project_template_readers_group.replaceAll("template", idRef.toString());

            Group groupAdmin = groupService.findByName(context, admin_Group);
            Group groupMembers = groupService.findByName(context, members_Group);
            Group groupRead = groupService.findByName(context, read_Group);
            Group groupFunders = groupService.findByName(context, funders_Group);

            assertEquals(groupFunders.getName(), funders_Group);
            assertEquals(groupAdmin.getName(), admin_Group);
            assertEquals(groupMembers.getName(), members_Group);
            assertEquals(groupRead.getName(), read_Group);

            MatcherAssert.assertThat(
                groupFunders.getMembers(),
                Matchers.hasItem(
                    hasProperty("email", equalTo(admin.getEmail()))
                )
            );
            MatcherAssert.assertThat(
                groupAdmin.getMembers(),
                not(
                    Matchers.hasItem(
                        hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );
            MatcherAssert.assertThat(
                groupMembers.getMembers(),
                not(
                    Matchers.hasItem(
                        hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );
            MatcherAssert.assertThat(
                groupRead.getMembers(),
                not(
                    Matchers.hasItem(
                        hasProperty("email", equalTo(admin.getEmail()))
                    )
                )
            );

            assertEquals(1, communities.size());
            assertEquals(1, collections.size());
            Community firstChild = communities.get(0);
            boolean child1Found =
                StringUtils.equals(firstChild.getName(), funding.getName());
            assertTrue(child1Found);
            assertNotEquals(firstChild.getID(), funding.getID());
            assertEquals(0, firstChild.getCollections().size());

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    public void containsMetadataValue(
        Item newConsortiaItem,
        String schemaName,
        String element,
        String qualifier,
        Matcher<String> valueMatcher
    ) {
        MatcherAssert
            .assertThat(
                newConsortiaItem.getMetadata(),
                allOf(
                    hasItem(
                        allOf(
                            hasProperty(
                                "metadataField",
                                allOf(
                                    hasProperty(
                                        "metadataSchema",
                                        hasProperty(
                                            "name",
                                            equalTo(schemaName)
                                        )
                                    ),
                                    hasProperty(
                                        "element",
                                        equalTo(element)
                                    ),
                                    hasProperty(
                                        "qualifier",
                                        equalTo(qualifier)
                                    )
                                )
                            ),
                            hasProperty(
                                "value",
                                valueMatcher
                            )
                        )
                    )
                )
            );
    }

    @Test
    public void cloneCommunityWrongUUIDTest() throws Exception {

        String tokenAdmin = getAuthToken(admin.getEmail(), password);

        getClient(tokenAdmin).perform(post("/api/core/communities").param("projection", "full")
                             .contentType(MediaType.parseMediaType(
                                TEXT_URI_LIST_VALUE))
                             .content("https://localhost:8080/server//api/core/communities/" + UUID.randomUUID()))
                             .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test99() throws Exception {
        context.turnOffAuthorisationSystem();
        Community cloneTarget = CommunityBuilder.createCommunity(context)
                                                .withName("Community to hold cloned communities").build();
        configurationService.setProperty("project.parent-community-id", cloneTarget.getID().toString());

        Community parentCommunity = CommunityBuilder.createCommunity(context)
                                                    .withName("Parent Community").build();

        Community child1 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 1").build();

        Community child2 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 2").build();

        Collection col = CollectionBuilder.createCollection(context, child1)
                                          .withName("Projects").build();

        Item publicItem1 = ItemBuilder.createItem(context, col)
                                      .withTitle("project_" + parentCommunity.getID().toString() + "_name")
                                      .build();

        StringBuilder placeholder = new StringBuilder();
        placeholder.append("project_").append(publicItem1.getID().toString()).append("_item");

        communityService.addMetadata(context, parentCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                                     null, placeholder.toString());

        Item itemAuthor = ItemBuilder.createItem(context, col)
                                     .withTitle("Michele, Boychuk")
                                     .build();

        Item itemAuthor2 = ItemBuilder.createItem(context, col)
                                      .withTitle("Giorgio, Shultz")
                                      .build();

        Item itemAuthor3 = ItemBuilder.createItem(context, col)
                                      .withTitle("Tommaso, Gattari")
                                      .build();

        ItemBuilder.createItem(context, col)
                   .withTitle("Title item 2")
                   .withAuthor("Michele, Boychuk", itemAuthor.getID().toString())
                   .withAuthor("Giorgio, Shultz", itemAuthor2.getID().toString())
                   .build();

        ItemBuilder.createItem(context, col)
                   .withTitle("Title item 3")
                   .withAuthor("Tommaso, Gattari", itemAuthor3.getID().toString())
                   .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .param("parent", cloneTarget.getID().toString())
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isCreated())
                     .andDo(result -> idRef
                             .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))))
                     .andExpect(jsonPath("$", Matchers.allOf(
                             hasJsonPath("$.name", is("My new Community")),
                             hasJsonPath("$.id", is(idRef.get().toString())),
                             hasJsonPath("$.id", not(parentCommunity.getID().toString()))
                             )));

            cloneTarget = context.reloadEntity(cloneTarget);
            Community subCommunityOfCloneTarget = cloneTarget.getSubcommunities().get(0);
            assertEquals(subCommunityOfCloneTarget.getID().toString(), idRef.toString());
            assertEquals("My new Community", subCommunityOfCloneTarget.getName());
            assertNotEquals(parentCommunity.getID(), idRef.toString());
            List<Community> communities = subCommunityOfCloneTarget.getSubcommunities();
            List<Collection> collections = subCommunityOfCloneTarget.getCollections();

            assertEquals(2, communities.size());
            assertEquals(0, collections.size());
            Community firstChild = communities.get(0);
            Community secondChild = communities.get(1);
            boolean child1Found = StringUtils.equals(firstChild.getName(), child1.getName())
                                                     || StringUtils.equals(secondChild.getName(), child1.getName());
            boolean child2Found = StringUtils.equals(firstChild.getName(), child2.getName())
                                                     || StringUtils.equals(secondChild.getName(), child2.getName());
            assertTrue(child1Found);
            assertTrue(child2Found);
            assertNotEquals(firstChild.getID(), child1.getID());
            assertNotEquals(firstChild.getID(), child2.getID());
            assertEquals(1, firstChild.getCollections().size());
            assertEquals(0, secondChild.getCollections().size());
            Collection colProject = firstChild.getCollections().get(0);
            // check that the new cloned collelction has a new items project
            assertTrue(checkItems(itemService.findAllByCollection(context, colProject)));

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    @Test
    public void cloneCommunityUnauthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();

        context.restoreAuthSystemState();

        getClient().perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isUnauthorized());

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/communities"))
             .andExpect(status().isOk())
             .andExpect(content().contentType(contentType))
             .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                 CommunityMatcher.matchProperties(parentCommunity.getName(),
                                                  parentCommunity.getID(),
                                                  parentCommunity.getHandle())
             )))
             .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    @Ignore
    // as the cloning endpoint is protected by PreAuthorize("hasAuthority('AUTHENTICATED')")
    // this test for synsicrys does not work
    public void cloneCommunityisForbiddenTest() throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();

        context.restoreAuthSystemState();

        String tokenEperson = getAuthToken(eperson.getEmail(), password);
        getClient(tokenEperson).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isForbidden());

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/communities"))
             .andExpect(status().isOk())
             .andExpect(content().contentType(contentType))
             .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                 CommunityMatcher.matchProperties(parentCommunity.getName(),
                                                  parentCommunity.getID(),
                                                  parentCommunity.getHandle())
             )))
             .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    public void cloneCommunityWIthItemTest() throws Exception {
        context.turnOffAuthorisationSystem();
        Community cloneTarget = CommunityBuilder.createCommunity(context)
                                                .withName("Community to hold cloned communities").build();
        configurationService.setProperty("project.parent-community-id", cloneTarget.getID().toString());

        Community parentCommunity = CommunityBuilder.createCommunity(context)
                                                    .withName("Parent Community").build();

        Community child1 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 1").build();

        Community child2 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 2").build();

        Collection col = CollectionBuilder.createCollection(context, child1)
                                          .withName("Projects").build();

        Item publicItem1 = ItemBuilder.createItem(context, col)
                                      .withTitle("project_" + parentCommunity.getID().toString() + "_name")
                                      .build();

        communityService.replaceMetadata(context, parentCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, publicItem1.getName(), publicItem1.getID().toString(), Choices.CF_ACCEPTED, 0);

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .param("parent", cloneTarget.getID().toString())
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isCreated())
                     .andDo(result -> idRef
                             .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))))
                     .andExpect(jsonPath("$", Matchers.allOf(
                             hasJsonPath("$.name", is("My new Community")),
                             hasJsonPath("$.id", is(idRef.get().toString())),
                             hasJsonPath("$.id", not(parentCommunity.getID().toString()))
                             )));

            cloneTarget = context.reloadEntity(cloneTarget);
            Community subCommunityOfCloneTarget = cloneTarget.getSubcommunities().get(0);
            assertEquals(subCommunityOfCloneTarget.getID().toString(), idRef.toString());
            assertEquals("My new Community", subCommunityOfCloneTarget.getName());
            assertNotEquals(parentCommunity.getID(), idRef.toString());
            List<Community> communities = subCommunityOfCloneTarget.getSubcommunities();
            List<Collection> collections = subCommunityOfCloneTarget.getCollections();

            assertEquals(2, communities.size());
            assertEquals(0, collections.size());
            Community firstChild = communities.get(0);
            Community secondChild = communities.get(1);
            boolean child1Found = StringUtils.equals(firstChild.getName(), child1.getName())
                                                     || StringUtils.equals(secondChild.getName(), child1.getName());
            boolean child2Found = StringUtils.equals(firstChild.getName(), child2.getName())
                                                     || StringUtils.equals(secondChild.getName(), child2.getName());
            assertTrue(child1Found);
            assertTrue(child2Found);
            assertNotEquals(firstChild.getID(), child1.getID());
            assertNotEquals(firstChild.getID(), child2.getID());
            assertEquals(1, firstChild.getCollections().size());
            assertEquals(0, secondChild.getCollections().size());
            Collection colProject = firstChild.getCollections().get(0);
            // check that the new cloned collelction has a new item project
            Iterator<Item> items = itemService.findAllByCollection(context, colProject);
            assertTrue(items.hasNext());
            Item item = items.next();
            assertTrue(containMetadata(itemService, item, "dc", "title", null, "My new Community"));
            assertTrue(containMetadata(communityService, subCommunityOfCloneTarget,
                    MD_RELATION_ITEM_ENTITY.schema, MD_RELATION_ITEM_ENTITY.element,
                    MD_RELATION_ITEM_ENTITY.qualifier, "My new Community", item.getID().toString()));
            assertFalse(items.hasNext());

            // checking the original collection
            Iterator<Item> itemsOfOriginCollection = itemService.findAllByCollection(context, col);
            assertTrue(itemsOfOriginCollection.hasNext());
            Item itemOfOriginCollection = itemsOfOriginCollection.next();
            assertEquals(itemOfOriginCollection.getID(), publicItem1.getID());
            assertFalse(itemsOfOriginCollection.hasNext());
        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    @Test
    public void cloneCommunityWithGrantsValueUnprocessableEntityTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Group adminGroup = GroupBuilder.createGroup(context).build();
        Group writeGroup = GroupBuilder.createGroup(context).build();
        Group readGroup = GroupBuilder.createGroup(context).build();

        Group collectionGroupA = GroupBuilder.createGroup(context)
                .withName("Group A")
                .build();

        Group collectionGroupB = GroupBuilder.createGroup(context)
                .withName("Group B")
                .build();

        parentCommunity = createCommunity(context)
            .withName("Parent Community")
            .withAdminGroup(adminGroup)
            .build();

        groupService.setName(adminGroup, "project_" + parentCommunity.getID().toString() + "_admin_group");
        groupService.setName(writeGroup, "project_" + parentCommunity.getID().toString() + "_write_group");
        groupService.setName(readGroup, "project_" + parentCommunity.getID().toString() + "_read_group");

        List<String> groups = new ArrayList<String>();
        groups.add(adminGroup.getName());
        groups.add(writeGroup.getName());
        groups.add(readGroup.getName());

        configurationService.setProperty("project.template.groups-name", groups);

        Community child1 = createSubCommunity(context, parentCommunity)
            .withName("Sub Community 1")
            .build();

        Community child2 = createSubCommunity(context, parentCommunity)
            .withName("Sub Community 2")
            .build();

        createCollection(context, parentCommunity)
               .withName("Collection of parent Community")
               .withSubmitterGroup(readGroup)
               .withAdminGroup(adminGroup)
               .withWorkflowGroup(1, collectionGroupA)
               .withWorkflowGroup(2, collectionGroupB)
               .build();

        createCollection(context, child1)
               .withName("Child 1 Collection 1").build();

        createCollection(context, child2)
               .withName("Child 2 Collection 1")
               .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);

        getClient(tokenAdmin).perform(post("/api/core/communities")
                             .param("projection", "full")
                             .param("name", "My new Community")
                             .param("grants", "WrongValue")
                     .contentType(MediaType.parseMediaType(TEXT_URI_LIST_VALUE))
                     .content("https://localhost:8080/server//api/core/communities/" + parentCommunity.getID()))
                     .andExpect(status()
                     .isUnprocessableEntity());
    }


    @Test
    public void cloneCommunityWithGrantsValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Community cloneTarget = CommunityBuilder.createCommunity(context)
                                                .withName("Community to hold cloned communities")
                                                .build();
        configurationService.setProperty("project.parent-community-id", cloneTarget.getID().toString());

        Community parentCommunity = CommunityBuilder.createCommunity(context)
                                                    .withName("Parent Community")
                                                    .build();

        Community child1 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 1").build();

        Community child2 = createSubCommunity(context, parentCommunity)
                                    .withName("Sub Community 2").build();

        Collection col = CollectionBuilder.createCollection(context, child1)
                                          .withName("Projects").build();

        Item publicItem1 = ItemBuilder.createItem(context, col)
                                      .withTitle("project_" + parentCommunity.getID().toString() + "_name")
                                      .build();

        communityService.replaceMetadata(context, parentCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, publicItem1.getName(), publicItem1.getID().toString(), Choices.CF_ACCEPTED, 0);

        context.restoreAuthSystemState();

        AtomicReference<UUID> idRef = new AtomicReference<>();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                                 .param("projection", "full")
                                 .param("name", "My new Community")
                                 .param("grants", "project")
                                 .param("parent", cloneTarget.getID().toString())
                                 .contentType(MediaType.parseMediaType(
                                  TEXT_URI_LIST_VALUE))
                                 .content("https://localhost:8080/server//api/core/communities/"
                                          + parentCommunity.getID()))
                     .andExpect(status().isCreated()).andDo(result -> idRef
                                        .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))))
                    .andExpect(jsonPath("$",Matchers.allOf(hasJsonPath("$.name", is("My new Community")),
                            hasJsonPath("$.id", is(idRef.get().toString())),
                            hasJsonPath("$.id", not(parentCommunity.getID().toString())))));

            cloneTarget = context.reloadEntity(cloneTarget);
            Community subCommunityOfCloneTarget = cloneTarget.getSubcommunities().get(0);
            assertEquals(subCommunityOfCloneTarget.getID().toString(), idRef.toString());
            assertEquals("My new Community", subCommunityOfCloneTarget.getName());
            assertNotEquals(parentCommunity.getID(), idRef.toString());
            List<Community> communities = subCommunityOfCloneTarget.getSubcommunities();
            List<Collection> collections = subCommunityOfCloneTarget.getCollections();

            assertEquals(2, communities.size());
            assertEquals(0, collections.size());
            Community firstChild = communities.get(0);
            Community secondChild = communities.get(1);
            boolean child1Found = StringUtils.equals(firstChild.getName(), child1.getName())
                    || StringUtils.equals(secondChild.getName(), child1.getName());
            boolean child2Found = StringUtils.equals(firstChild.getName(), child2.getName())
                    || StringUtils.equals(secondChild.getName(), child2.getName());
            assertTrue(child1Found);
            assertTrue(child2Found);
            assertNotEquals(firstChild.getID(), child1.getID());
            assertNotEquals(firstChild.getID(), child2.getID());
            assertEquals(1, firstChild.getCollections().size());
            assertEquals(0, secondChild.getCollections().size());
            Collection colProject = firstChild.getCollections().get(0);
            // check that the new cloned collection has a new item project
            Iterator<Item> items = itemService.findAllByCollection(context, colProject);
            assertTrue(items.hasNext());
            Item item = items.next();
            assertTrue(containMetadata(itemService, item, "dc", "title", null, "My new Community"));
            assertTrue(containMetadata(itemService, item, "cris", "project", "shared", "project"));
            assertTrue(containMetadata(communityService, subCommunityOfCloneTarget,
                    MD_RELATION_ITEM_ENTITY.schema, MD_RELATION_ITEM_ENTITY.element,
                    MD_RELATION_ITEM_ENTITY.qualifier, "My new Community", item.getID().toString()));
            assertFalse(items.hasNext());

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    private <T extends DSpaceObject> boolean containMetadata(DSpaceObjectService<T> service, T target, String schema,
            String element, String qualifier, String valueToCheck) {
        String value = service.getMetadataFirstValue(target, schema, element, qualifier, null);
        if (StringUtils.equals(value, valueToCheck)) {
            return true;
        }
        return false;
    }

    private <T extends DSpaceObject> boolean containMetadata(DSpaceObjectService<T> service, T target, String schema,
            String element, String qualifier, String valueToCheck, String authorityToCheck) {
        List<MetadataValue> mdv = service.getMetadata(target, schema, element, qualifier, null);
        if (mdv.size() > 0 && StringUtils.equals(mdv.get(0).getValue(), valueToCheck) &&
                StringUtils.equals(mdv.get(0).getAuthority(), authorityToCheck)) {
            return true;
        }
        return false;
    }

    private boolean checkItems(Iterator<Item> iterator) {
        Item author1 = null;
        Item author2 = null;
        Item author3 = null;
        Item item1 = null;
        Item item2 = null;
        while (iterator.hasNext()) {
            Item item =  iterator.next();
            switch (item.getName()) {
            case "Michele, Boychuk": author1 = item;
            continue;
            case "Giorgio, Shultz" : author2 = item;
            continue;
            case "Tommaso, Gattari": author3 = item;
            continue;
            case "Title item 2" : item1 = item;
            continue;
            case "Title item 3" : item2 = item;
            continue;
            default:;
          }
        }
        assertNotNull(author1);
        assertNotNull(author2);
        assertNotNull(author3);
        assertNotNull(item1);
        assertNotNull(item2);
        List<MetadataValue> valuesItem1 = itemService.getMetadata(item1, "dc", "contributor", "author", Item.ANY);
        List<MetadataValue> valuesItem2 = itemService.getMetadata(item2, "dc", "contributor", "author", Item.ANY);
        assertEquals(2, valuesItem1.size());
        assertEquals(1, valuesItem2.size());
        boolean author1Found = StringUtils.equals(valuesItem1.get(0).getAuthority(), author1.getID().toString())
                            || StringUtils.equals(valuesItem1.get(0).getAuthority(), author2.getID().toString());
        boolean author2Found = StringUtils.equals(valuesItem1.get(1).getAuthority(), author1.getID().toString())
                            || StringUtils.equals(valuesItem1.get(1).getAuthority(), author2.getID().toString());
        boolean author3Found = StringUtils.equals(valuesItem2.get(0).getAuthority(), author3.getID().toString());
        return author1Found && author2Found && author3Found;
    }

}
