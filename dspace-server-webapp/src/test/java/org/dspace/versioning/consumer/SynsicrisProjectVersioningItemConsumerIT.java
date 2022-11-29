/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.versioning.consumer;

import static com.jayway.jsonpath.JsonPath.read;
import static org.dspace.project.util.ProjectConstants.FUNDER_PROJECT_MANAGERS_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_COORDINATOR_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_FUNDER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_LAST_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.MD_MEMBER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_READER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_READ_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.dspace.project.util.ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.SYSTEM_MEMBERS_GROUP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.RelationshipTypeBuilder;
import org.dspace.builder.VersionBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.eperson.Group;
import org.dspace.eperson.GroupConfiguration;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.versioning.ProjectVersionProvider;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.MediaType;

public class SynsicrisProjectVersioningItemConsumerIT extends AbstractControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SynsicrisProjectVersioningItemConsumerIT.class);

    private Community projectCommunity;
    private Community sharedCoummunity;
    private Collection projectCollection;
    private Collection researchProfileCollection;
    private Group funderGroup;
    private Group membersGroup;
    private Group readersGroup;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private GroupService groupService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        EntityType projectType = EntityTypeBuilder.createEntityTypeBuilder(context, PROJECT_ENTITY).build();
        RelationshipTypeBuilder.createRelationshipTypeBuilder(
            context, projectType, projectType, ProjectVersionProvider.VERSION_RELATIONSHIP,
            ProjectVersionProvider.VERSION_RELATIONSHIP, 0, 3, 0, 3
        );
        EntityType programmeType = EntityTypeBuilder.createEntityTypeBuilder(context, PROGRAMME).build();
        RelationshipTypeBuilder.createRelationshipTypeBuilder(
            context, programmeType, programmeType, ProjectVersionProvider.VERSION_RELATIONSHIP,
            ProjectVersionProvider.VERSION_RELATIONSHIP, 0, 3, 0, 3
        );

        GroupBuilder.createGroup(context)
            .withName(FUNDER_PROJECT_MANAGERS_GROUP)
            .build();

        GroupBuilder.createGroup(context)
            .withName(SYSTEM_MEMBERS_GROUP)
            .build();

        sharedCoummunity =
            CommunityBuilder.createCommunity(context)
                .withName("Shared")
                .build();

        researchProfileCollection =
            CollectionBuilder.createCollection(context, sharedCoummunity)
                .withName("Persons")
                .build();

        parentCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("community")
                .build();

        projectCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("project's community")
                .build();

        projectCollection =
            CollectionBuilder.createCollection(context, projectCommunity)
                .withName("project collection")
                .withEntityType(PROJECT_ENTITY)
                .build();

        GroupBuilder.createGroup(context)
            .withName(
                String.format(
                    PROJECT_COORDINATORS_GROUP_TEMPLATE,
                    projectCommunity.getID().toString()
                )
            )
            .addMember(admin)
            .build();

        funderGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        PROJECT_FUNDERS_GROUP_TEMPLATE,
                        projectCommunity.getID().toString()
                    )
                )
                .build();

        membersGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        PROJECT_MEMBERS_GROUP_TEMPLATE,
                        projectCommunity.getID().toString()
                    )
                )
                .build();

        readersGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        PROJECT_READERS_GROUP_TEMPLATE,
                        projectCommunity.getID().toString()
                    )
                )
                .build();

        Group g = collectionService.createAdministrators(context, researchProfileCollection);
        this.groupService.addMember(context, g, funderGroup);
        this.groupService.update(context, g);

        configurationService.setProperty(GroupConfiguration.SYSTEM_MEMBERS, membersGroup.getID());
        configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, funderGroup.getID());
        configurationService.setProperty("project.parent-community-id", projectCommunity.getID().toString());
        configurationService
            .setProperty("researcher-profile.collection.uuid", researchProfileCollection.getID().toString());

        context.restoreAuthSystemState();
    }

    @Test
    public void verifiesMetadasOnVersionedProjectWhenMadeVisible() throws Exception {
        context.turnOffAuthorisationSystem();

        Item item =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project test item")
                .withIssueDate("22022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();
        Item item1 =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project test item 1")
                .withIssueDate("2022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Collection programmeCollection =
            CollectionBuilder.createCollection(context, projectCommunity)
                .withName("project 2 collection")
                .withEntityType(PROGRAMME)
                .build();

        Item item2 =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme 2 test item 1")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Item item3 =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme 2 test item 2")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        context.commit();
        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef = new AtomicReference<Integer>();
        AtomicReference<String> itemUrl = new AtomicReference<String>();
        AtomicReference<UUID> itemId = new AtomicReference<UUID>();

        try {
            String adminToken = getAuthToken(admin.getEmail(), password);

            getClient(adminToken).perform(post("/api/versioning/versions")
                 .param("summary", "test summary!")
                 .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                 .content("/api/core/items/" + item.getID()))
                 .andExpect(status().isCreated())
                 .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));


            String[] split = itemUrl.get().split("/item");
            split = split[0].split("/api/versioning/versions/");

            String itemVersion = split[1];
            getClient(adminToken).perform(get("/api/versioning/versions/" + itemVersion + "/item"))
                .andExpect(status().isOk())
                .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            item = this.itemService.find(context, itemId.get());

            context.turnOffAuthorisationSystem();
            this.itemService.addMetadata(
                context, item, MD_VERSION_VISIBLE.schema, MD_VERSION_VISIBLE.element,
                MD_VERSION_VISIBLE.qualifier, null, "true"
            );
            this.itemService.update(context, item);
            this.context.commit();
            context.restoreAuthSystemState();

            item = this.context.reloadEntity(item);

            checkVisibleItem(item);

            Community comm = item.getCollections()
                .stream()
                .filter(col -> "project collection".equals(col.getName()))
                .map(col -> {
                    try {
                        return col.getCommunities().get(0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElse(null);

            assertNotNull(comm);
            comm.getCollections()
                .stream()
                .flatMap(col -> {
                    try {
                        return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                this.itemService.findAllByCollection(context, col),
                                Spliterator.ORDERED
                            ),
                            false
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(i -> StringUtils.endsWith(itemService.getMetadata(i, "synsicris.uniqueid"), "_" + itemVersion))
                .forEach(t -> {
                    try {
                        checkVisibleItem(context.reloadEntity(t));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

            context.commit();
        } finally {
            configurationService.setProperty(GroupConfiguration.SYSTEM_MEMBERS, null);
            configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, null);
            configurationService.setProperty("project.parent-community-id", null);
            configurationService.setProperty("researcher-profile.collection.uuid", null);
            context.turnOffAuthorisationSystem();
            this.authorizeService.removeGroupPolicies(context, funderGroup);
            VersionBuilder.delete(idRef.get());
            context.restoreAuthSystemState();
        }
    }

    @Test
    public void verifiesRestoredMetasdatasOnVersionedProjectWhenMadeInvisible() throws Exception {
        context.turnOffAuthorisationSystem();

        Item item =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project test item")
                .withIssueDate("22022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();
        Item item1 =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project test item 1")
                .withIssueDate("2022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Collection project2Collection =
            CollectionBuilder.createCollection(context, projectCommunity)
                .withName("project 2 collection")
                .withEntityType(PROGRAMME)
                .build();

        Item item2 =
            ItemBuilder.createItem(context, project2Collection)
                .withTitle("Programme 2 test item 1")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Item item3 =
            ItemBuilder.createItem(context, project2Collection)
                .withTitle("Programme 2 test item 2")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        context.commit();
        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef = new AtomicReference<Integer>();
        AtomicReference<String> itemUrl = new AtomicReference<String>();
        AtomicReference<UUID> itemId = new AtomicReference<UUID>();

        try {
            String adminToken = getAuthToken(admin.getEmail(), password);

            getClient(adminToken).perform(post("/api/versioning/versions")
                 .param("summary", "test summary!")
                 .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                 .content("/api/core/items/" + item.getID()))
                 .andExpect(status().isCreated())
                 .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));


            String[] split = itemUrl.get().split("/item");
            split = split[0].split("/api/versioning/versions/");

            String itemVersion = split[1];
            getClient(adminToken).perform(get("/api/versioning/versions/" + itemVersion + "/item"))
                .andExpect(status().isOk())
                .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            item = this.itemService.find(context, itemId.get());

            // make visible
            context.turnOffAuthorisationSystem();
            this.itemService.addMetadata(
                context, item, MD_VERSION_VISIBLE.schema, MD_VERSION_VISIBLE.element,
                MD_VERSION_VISIBLE.qualifier, null, "true"
            );
            this.itemService.update(context, item);
            this.context.commit();
            context.restoreAuthSystemState();

            item = this.context.reloadEntity(item);

            // hide project
            context.turnOffAuthorisationSystem();
            this.itemService.setMetadataSingleValue(context, item, MD_VERSION_VISIBLE, null, "false");
            this.itemService.update(context, item);
            this.context.commit();
            context.restoreAuthSystemState();

            item = this.context.reloadEntity(item);

            checkHidItem(item);

            Community comm = item.getCollections()
                .stream()
                .filter(col -> "project collection".equals(col.getName()))
                .map(col -> {
                    try {
                        return col.getCommunities().get(0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElse(null);

            assertNotNull(comm);
            comm.getCollections()
                .stream()
                .flatMap(col -> {
                    try {
                        return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                this.itemService.findAllByCollection(context, col),
                                Spliterator.ORDERED
                            ),
                            false
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(i -> StringUtils.endsWith(itemService.getMetadata(i, "synsicris.uniqueid"), "_" + itemVersion))
                .forEach(t -> {
                    try {
                        checkHidItem(context.reloadEntity(t));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            context.commit();
        } finally {
            configurationService.setProperty(GroupConfiguration.SYSTEM_MEMBERS, null);
            configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, null);
            configurationService.setProperty("project.parent-community-id", null);
            configurationService.setProperty("researcher-profile.collection.uuid", null);
            context.turnOffAuthorisationSystem();
            this.authorizeService.removeGroupPolicies(context, funderGroup);
            VersionBuilder.delete(idRef.get());
            context.restoreAuthSystemState();
        }
    }

    @Test
    public void verifiesMultipleVersioningWithLastVersionVisible() throws Exception {
        context.turnOffAuthorisationSystem();

        Community project1Community =
            CommunityBuilder.createSubCommunity(context, projectCommunity)
            .withName("project 1 community")
            .build();

        Collection project1Collection =
            CollectionBuilder.createCollection(context, project1Community)
            .withName("project 1 collection")
            .withEntityType(PROJECT_ENTITY)
            .build();

        GroupBuilder.createGroup(context)
            .withName(
                String.format(
                    ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE,
                    project1Community.getID().toString()
                )
            )
            .addMember(admin)
            .build();

        Item item =
            ItemBuilder.createItem(context, project1Collection)
                .withTitle("Project test item")
                .withIssueDate("22022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();
        Item item1 =
            ItemBuilder.createItem(context, project1Collection)
                .withTitle("Project test item 1")
                .withIssueDate("2022-10-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Community project2Community =
            CommunityBuilder.createSubCommunity(context, projectCommunity)
            .withName("project 2 community")
            .build();

        GroupBuilder.createGroup(context)
        .withName(
            String.format(
                ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE,
                project2Community.getID().toString()
            )
        )
        .addMember(admin)
        .build();

        Collection project2Collection =
            CollectionBuilder.createCollection(context, project2Community)
            .withName("project 2 collection")
            .withEntityType(PROJECT_ENTITY)
            .build();

        Item itemProject2 =
            ItemBuilder.createItem(context, project2Collection)
            .withTitle("Project 2")
            .withIssueDate("2022-08-20")
            .withAuthor(admin.getName())
            .withSubject("ExtraEntry")
            .build();

        Collection programmeCollection =
            CollectionBuilder.createCollection(context, project1Community)
                .withName("programme collection")
                .withEntityType(PROGRAMME)
                .build();

        Item item2 =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme 2 test item 1")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        Item item3 =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme 2 test item 2")
                .withIssueDate("2022-08-20")
                .withAuthor(admin.getName())
                .withSubject("ExtraEntry")
                .build();

        funderGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE,
                        project1Community.getID().toString()
                    )
                )
                .build();

        membersGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE,
                        project1Community.getID().toString()
                    )
                )
                .build();

        readersGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE,
                        project1Community.getID().toString()
                    )
                )
                .build();

        Group g = collectionService.createAdministrators(context, researchProfileCollection);
        this.groupService.addMember(context, g, funderGroup);
        this.groupService.update(context, g);

        configurationService.setProperty(GroupConfiguration.SYSTEM_MEMBERS, membersGroup.getID());
        configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, funderGroup.getID());
        configurationService.setProperty("project.parent-community-id", projectCommunity.getID().toString());
        configurationService
            .setProperty("researcher-profile.collection.uuid", researchProfileCollection.getID().toString());


        context.commit();
        context.restoreAuthSystemState();

        AtomicReference<Integer> idRef = new AtomicReference<Integer>();
        AtomicReference<Integer> idRef2 = new AtomicReference<Integer>();
        AtomicReference<Integer> idRef3 = new AtomicReference<Integer>();
        AtomicReference<Integer> idRef4 = new AtomicReference<Integer>();
        AtomicReference<String> itemUrl = new AtomicReference<String>();
        AtomicReference<UUID> itemId = new AtomicReference<UUID>();

        try {
            String adminToken = getAuthToken(admin.getEmail(), password);

            getClient(adminToken).perform(post("/api/versioning/versions")
                 .param("summary", "test summary!")
                 .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                 .content("/api/core/items/" + item.getID()))
                 .andExpect(status().isCreated())
                 .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));


            String[] split = itemUrl.get().split("/item");
            split = split[0].split("/api/versioning/versions/");

            String itemVersion = split[1];
            getClient(adminToken).perform(get("/api/versioning/versions/" + itemVersion + "/item"))
                .andExpect(status().isOk())
                .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            Item firstVersionItem = this.itemService.find(context, itemId.get());

            // make a second version
            getClient(adminToken).perform(post("/api/versioning/versions")
                .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                .content("/api/core/items/" + item.getID()))
                .andExpect(status().isCreated())
                .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

           getClient(adminToken).perform(get("/api/versioning/versions/" + idRef))
               .andExpect(status().isOk())
               .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));


           split = itemUrl.get().split("/item");
           split = split[0].split("/api/versioning/versions/");

           String itemSecondVersion = split[1];
           getClient(adminToken).perform(get("/api/versioning/versions/" + itemSecondVersion + "/item"))
               .andExpect(status().isOk())
               .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

           Item secondVersionItem = this.itemService.find(context, itemId.get());
           // checkHidItem(secondVersionItem);

           // make a third version
            getClient(adminToken).perform(
                post("/api/versioning/versions")
                    .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                    .content("/api/core/items/" + item.getID())
                )
                .andExpect(status().isCreated())
                .andDo(result -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef2))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));

            split = itemUrl.get().split("/item");
            split = split[0].split("/api/versioning/versions/");

            String itemThirdVersion = split[1];
            getClient(adminToken).perform(get("/api/versioning/versions/" + itemThirdVersion + "/item"))
                .andExpect(status().isOk())
                .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            Item thirdVersionItem = this.itemService.find(context, itemId.get());
            // checkHidItem(thirdVersionItem);

            getClient(adminToken).perform(
                post("/api/versioning/versions")
                    .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                    .content("/api/core/items/" + itemProject2.getID())
            )
                .andExpect(status().isCreated())
                .andDo(result -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef3))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));

            getClient(adminToken).perform(
                post("/api/versioning/versions")
                    .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                    .content("/api/core/items/" + itemProject2.getID())
            )
                .andExpect(status().isCreated())
                .andDo(result -> idRef4.set(read(result.getResponse().getContentAsString(), "$.id")));

            getClient(adminToken).perform(get("/api/versioning/versions/" + idRef4))
                .andExpect(status().isOk())
                .andDo(result -> itemUrl.set(read(result.getResponse().getContentAsString(), "$._links.item.href")));

            String[] split2 = itemUrl.get().split("/item");
            split2 = split2[0].split("/api/versioning/versions/");

            String item2Version = split2[1];
            getClient(adminToken).perform(get("/api/versioning/versions/" + item2Version + "/item"))
                .andExpect(status().isOk())
                .andDo(result -> itemId.set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            Item item2SecondVersion = this.itemService.find(context, itemId.get());

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            // make visible
            context.turnOffAuthorisationSystem();
            this.itemService.addMetadata(
                context, firstVersionItem, ProjectConstants.MD_VERSION_VISIBLE.schema,
                ProjectConstants.MD_VERSION_VISIBLE.element,
                ProjectConstants.MD_VERSION_VISIBLE.qualifier, null, "true"
            );
            this.itemService.update(context, firstVersionItem);
            this.context.commit();
            context.restoreAuthSystemState();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);

            checkVisibleItem(firstVersionItem);
            checkLastVersionVisible(firstVersionItem);

            secondVersionItem = this.context.reloadEntity(secondVersionItem);

           // make visible
           context.turnOffAuthorisationSystem();
           this.itemService.addMetadata(
                context, secondVersionItem, ProjectConstants.MD_VERSION_VISIBLE.schema,
                ProjectConstants.MD_VERSION_VISIBLE.element,
                ProjectConstants.MD_VERSION_VISIBLE.qualifier, null, "true"
           );
           this.itemService.update(context, secondVersionItem);
           this.context.commit();
           context.restoreAuthSystemState();

           firstVersionItem = this.context.reloadEntity(firstVersionItem);
           secondVersionItem = this.context.reloadEntity(secondVersionItem);
           thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

           checkVisibleItem(firstVersionItem);
           checkVisibleItem(secondVersionItem);
           checkLastVersionNotVisible(firstVersionItem);
           checkLastVersionVisible(secondVersionItem);

            // hide project
            context.turnOffAuthorisationSystem();
            this.itemService
                .setMetadataSingleValue(context, secondVersionItem, ProjectConstants.MD_VERSION_VISIBLE, null, "false");
            this.itemService.update(context, secondVersionItem);
            this.context.commit();
            context.restoreAuthSystemState();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            checkVisibleItem(firstVersionItem);
            checkHidItem(secondVersionItem);
            checkLastVersionNotVisible(thirdVersionItem);
            checkLastVersionNotVisible(secondVersionItem);
            // visible because latest version visible has been hidden!
            checkLastVersionVisible(firstVersionItem);

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            context.turnOffAuthorisationSystem();
            this.itemService.addMetadata(
                 context, thirdVersionItem, ProjectConstants.MD_VERSION_VISIBLE.schema,
                 ProjectConstants.MD_VERSION_VISIBLE.element,
                 ProjectConstants.MD_VERSION_VISIBLE.qualifier, null, "true"
            );
            this.itemService.update(context, thirdVersionItem);
            this.context.commit();
            context.restoreAuthSystemState();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            checkVisibleItem(firstVersionItem);
            checkHidItem(secondVersionItem);
            checkVisibleItem(thirdVersionItem);
            checkLastVersionNotVisible(secondVersionItem);
            checkLastVersionNotVisible(firstVersionItem);
            checkLastVersionVisible(thirdVersionItem);

            Community comm = item.getCollections()
                .stream()
                .filter(col -> "project 1 collection".equals(col.getName()))
                .map(col -> {
                    try {
                        return col.getCommunities().get(0);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElse(null);

            assertNotNull(comm);
            comm.getCollections()
                .stream()
                .flatMap(col -> {
                    try {
                        return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                this.itemService.findAllByCollection(context, col),
                                Spliterator.ORDERED
                            ),
                            false
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(i -> StringUtils.endsWith(itemService.getMetadata(i, "synsicris.uniqueid"), "_" + itemVersion)
                )
                .forEach(t -> {
                    try {
                        checkVisibleItem(context.reloadEntity(t));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            comm.getCollections()
                .stream()
                .flatMap(col -> {
                    try {
                        return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                this.itemService.findAllByCollection(context, col),
                                Spliterator.ORDERED
                            ),
                            false
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(
                    i -> StringUtils.endsWith(
                        itemService.getMetadata(i, "synsicris.uniqueid"), "_" + itemSecondVersion
                    )
                )
                .forEach(t -> {
                    try {
                        checkHidItem(context.reloadEntity(t));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            comm.getCollections()
                .stream()
                .flatMap(col -> {
                    try {
                        return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                this.itemService.findAllByCollection(context, col),
                                Spliterator.ORDERED
                            ),
                            false
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(
                    i -> StringUtils.endsWith(
                            itemService.getMetadata(i, "synsicris.uniqueid"), "_" + itemThirdVersion
                        )
                )
                .forEach(t -> {
                    try {
                        checkVisibleItem(context.reloadEntity(t));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            context.commit();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            this.context.turnOffAuthorisationSystem();
            this.itemService.delete(context, thirdVersionItem);
            this.context.restoreAuthSystemState();

            this.context.commit();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            assertNull(thirdVersionItem);
            checkHidItem(secondVersionItem);
            checkVisibleItem(firstVersionItem);
            checkLastVersionNotVisible(secondVersionItem);
            checkLastVersionVisible(firstVersionItem);

            this.context.turnOffAuthorisationSystem();
            this.itemService.delete(context, secondVersionItem);
            this.context.restoreAuthSystemState();

            this.context.commit();

            firstVersionItem = this.context.reloadEntity(firstVersionItem);
            secondVersionItem = this.context.reloadEntity(secondVersionItem);
            thirdVersionItem = this.context.reloadEntity(thirdVersionItem);

            assertNull(thirdVersionItem);
            assertNull(secondVersionItem);
            checkVisibleItem(firstVersionItem);
            checkLastVersionVisible(firstVersionItem);

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            configurationService.setProperty(GroupConfiguration.SYSTEM_MEMBERS, null);
            configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, null);
            configurationService.setProperty("project.parent-community-id", null);
            configurationService.setProperty("researcher-profile.collection.uuid", null);
            context.turnOffAuthorisationSystem();
            this.authorizeService.removeGroupPolicies(context, funderGroup);
            VersionBuilder.delete(idRef4.get());
            VersionBuilder.delete(idRef3.get());
            VersionBuilder.delete(idRef2.get());
            VersionBuilder.delete(idRef.get());
            context.restoreAuthSystemState();
        }
    }

    public void checkVisibleItem(Item item) throws SQLException {
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_FUNDER_POLICY_GROUP.toString()),
            hasSize(0)
        );
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_READER_POLICY_GROUP.toString()),
            hasSize(0)
        );
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_MEMBER_POLICY_GROUP.toString()),
            hasSize(0)
        );
        assertThat(
            this.itemService
                .getMetadataByMetadataString(item, MD_COORDINATOR_POLICY_GROUP.toString()),
            hasSize(0)
        );


        List<MetadataValue> policyGroups =
            this.itemService.getMetadataByMetadataString(item, MD_VERSION_READ_POLICY_GROUP.toString());
        assertNotNull(policyGroups);

        Matcher<Iterable<? super MetadataValue>> funderGroupItem = getGroupMatcher(funderGroup);
        Matcher<Iterable<? super MetadataValue>> readersGroupItem = getGroupMatcher(readersGroup);
        Matcher<Iterable<? super MetadataValue>> membersGroupItem = getGroupMatcher(membersGroup);
        assertThat(policyGroups, funderGroupItem);
        assertThat(policyGroups, readersGroupItem);
        assertThat(policyGroups, membersGroupItem);
        assertThat(
            policyGroups
                .stream()
                .map(MetadataValue::getValue)
                .filter(value ->
                    funderGroup.getName().equals(value) ||
                    readersGroup.getName().equals(value) ||
                    membersGroup.getName().equals(value)
                )
                .collect(Collectors.toList()),
            hasSize(3)
        );

        List<ResourcePolicy> policies = this.authorizeService.getPolicies(context, item);
        assertNotNull(policies);

        Matcher<Iterable<? super ResourcePolicy>> readPolicyMatcher =
            getGroupPolicyMatcher(Constants.READ, funderGroup);
        Matcher<Iterable<? super ResourcePolicy>> bitstreamReadPolicyMatcher =
            getGroupPolicyMatcher(Constants.DEFAULT_BITSTREAM_READ, funderGroup);

        assertThat(policies, readPolicyMatcher);
        assertThat(policies, bitstreamReadPolicyMatcher);
    }

    public void checkLastVersionVisible(Item item) {
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_LAST_VERSION_VISIBLE.toString()),
            hasItem(
                hasProperty("value", equalTo(Boolean.TRUE.toString()))
            )
        );
    }

    public void checkLastVersionNotVisible(Item item) {
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_LAST_VERSION_VISIBLE.toString()),
            not(
                hasTrueValue()
            )
        );
    }

    public Matcher<Iterable<? super MetadataValue>> hasTrueValue() {
        return hasItem(
            hasProperty("value", equalTo(Boolean.TRUE.toString()))
        );
    }

    public void checkHidItem(Item item) throws SQLException {
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_FUNDER_POLICY_GROUP.toString()),
            hasSize(1)
        );
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_READER_POLICY_GROUP.toString()),
            hasSize(1)
        );
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_MEMBER_POLICY_GROUP.toString()),
            hasSize(1)
        );
        assertThat(
            this.itemService.getMetadataByMetadataString(item, MD_COORDINATOR_POLICY_GROUP.toString()),
            hasSize(1)
        );

        List<MetadataValue> policyGroups =
            this.itemService.getMetadataByMetadataString(item, MD_VERSION_READ_POLICY_GROUP.toString());
        assertNotNull(policyGroups);

        Matcher<Iterable<? super MetadataValue>> funderGroupItem = getGroupMatcher(funderGroup);
        Matcher<Iterable<? super MetadataValue>> readersGroupItem = getGroupMatcher(readersGroup);
        Matcher<Iterable<? super MetadataValue>> membersGroupItem = getGroupMatcher(membersGroup);
        assertThat(policyGroups, not(funderGroupItem));
        assertThat(policyGroups, not(readersGroupItem));
        assertThat(policyGroups, not(membersGroupItem));

        List<ResourcePolicy> policies = this.authorizeService.getPolicies(context, item);
        assertNotNull(policies);

        Matcher<Iterable<? super ResourcePolicy>> readPolicyMatcher =
            getGroupPolicyMatcher(Constants.READ, funderGroup);
        Matcher<Iterable<? super ResourcePolicy>> bitstreamReadPolicyMatcher =
            getGroupPolicyMatcher(Constants.DEFAULT_BITSTREAM_READ, funderGroup);

        assertThat(policies, not(readPolicyMatcher));
        assertThat(policies, not(bitstreamReadPolicyMatcher));
    }

    public Matcher<Iterable<? super ResourcePolicy>> getGroupPolicyMatcher(
        int action, Group group
    ) {
        return hasItem(
            allOf(
                hasProperty("action", equalTo(action)),
                hasProperty(
                    "group",
                    allOf(
                        hasProperty("ID", equalTo(group.getID())),
                        hasProperty("name", equalTo(group.getName()))
                    )
                )
            )
        );
    }

    public Matcher<Iterable<? super MetadataValue>> getGroupMatcher(Group group) {
        return hasItem(
            groupMatcher(group)
        );
    }

    public Matcher<MetadataValue> groupMatcher(Group group) {
        return allOf(
            hasProperty("value", equalTo(group.getName())),
            hasProperty("authority", equalTo(group.getID().toString()))
        );
    }

}
