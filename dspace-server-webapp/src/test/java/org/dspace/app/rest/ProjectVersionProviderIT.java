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
import static org.dspace.app.matcher.MetadataValueMatcher.with;
import static org.dspace.app.matcher.ResourcePolicyMatcher.matches;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.RelationshipTypeBuilder;
import org.dspace.builder.ResourcePolicyBuilder;
import org.dspace.builder.VersionBuilder;
import org.dspace.builder.WorkspaceItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.Relationship;
import org.dspace.content.RelationshipType;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.RelationshipService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.event.factory.EventServiceFactory;
import org.dspace.event.service.EventService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.versioning.Version;
import org.dspace.versioning.service.VersioningService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.MediaType;

public class ProjectVersionProviderIT extends AbstractControllerIntegrationTest {

    public static final String CRIS_CONSUMER = "crisconsumer";

    private static String[] consumers;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private VersioningService versioningService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private ItemService itemService;

    private Community joinProjects;

    private Community shared;

    private Collection sharedPersons;

    private Collection publications;

    private Collection persons;

    private Collection subPublications;

    private Item parentProject;

    private RelationshipType publicationIsVersionOf;

    private RelationshipType personIsVersionOf;

    private RelationshipType parentProjectIsVersionOf;

    @BeforeClass
    public static void initCrisConsumer() {
        ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
        consumers = configService.getArrayProperty("event.dispatcher.default.consumers");
        String newConsumers = consumers.length > 0 ? join(",", consumers) + "," + CRIS_CONSUMER : CRIS_CONSUMER;
        configService.setProperty("event.dispatcher.default.consumers", newConsumers);
        EventService eventService = EventServiceFactory.getInstance().getEventService();
        eventService.reloadConfiguration();
    }

    @AfterClass
    public static void resetDefaultConsumers() {
        ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
        configService.setProperty("event.dispatcher.default.consumers", consumers);
        EventService eventService = EventServiceFactory.getInstance().getEventService();
        eventService.reloadConfiguration();
    }

    @Before
    public void setup() {

        context.turnOffAuthorisationSystem();

        shared = createCommunity("Shared");
        sharedPersons = createCollection("Shared Persons", "Person", shared);

        Community joinProjects = createCommunity("Joint projects");

        Community testProject = createSubCommunity("Test Project", joinProjects);
        publications = createCollection("Publications", "Publication", testProject);
        persons = createCollection("Persons", "Person", testProject);

        GroupBuilder.createGroup(context)
            .withName("project_" + testProject.getID() + "_coordinators_group")
            .addMember(eperson)
            .build();

        Community testSubProjects = createSubCommunity("Sub projects", testProject);
        Community subProject = createSubCommunity("sub_001", testSubProjects);
        subPublications = createCollection("Sub Publications", "Publication", subProject);

        Collection joinProject = createCollection("Joint projects", PROJECT_ENTITY, testProject);
        parentProject = ItemBuilder.createItem(context, joinProject)
            .withTitle("Test project")
            .build();

        publicationIsVersionOf = createIsVersionRelationshipType("Publication");
        personIsVersionOf = createIsVersionRelationshipType("Person");
        parentProjectIsVersionOf = createIsVersionRelationshipType(PROJECT_ENTITY);

        context.restoreAuthSystemState();

    }

    @Test
    public void testProjectVersioning() throws Exception {

        context.turnOffAuthorisationSystem();

        Community otherTestProject = createSubCommunity("Other Test Project", joinProjects);
        Collection otherPublications = createCollection("Publications", "Publication", otherTestProject);

        Item otherPublication = ItemBuilder.createItem(context, otherPublications)
            .withTitle("Other Publication")
            .build();

        Collection otherJoinProject = createCollection("Joint projects", PROJECT_ENTITY, otherTestProject);
        Item otherParentProject = ItemBuilder.createItem(context, otherJoinProject)
            .withTitle("Test project")
            .build();

        Item sharedPerson1 = ItemBuilder.createItem(context, sharedPersons)
            .withTitle("First Shared Person")
            .build();

        Item sharedPerson2 = ItemBuilder.createItem(context, sharedPersons)
            .withTitle("Second Shared Person")
            .build();

        Item firstPerson = ItemBuilder.createItem(context, persons)
            .withTitle("First Person")
            .build();

        Item secondPerson = ItemBuilder.createItem(context, persons)
            .withTitle("Second Person")
            .build();

        Item publication = ItemBuilder.createItem(context, publications)
            .withTitle("Publication")
            .withAuthor("First Shared Person", sharedPerson1.getID().toString())
            .withAuthor("First Person", firstPerson.getID().toString())
            .build();

        Item subPublication = ItemBuilder.createItem(context, subPublications)
            .withTitle("Sub publication")
            .withAuthor("First Person", firstPerson.getID().toString())
            .withAuthor("Second Person", secondPerson.getID().toString())
            .withAuthor("Second Shared Person", sharedPerson2.getID().toString())
            .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);
        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated())
            .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));


        assertThat(findOneByRelationship(sharedPerson1, personIsVersionOf), nullValue());
        assertThat(findOneByRelationship(sharedPerson2, personIsVersionOf), nullValue());
        assertThat(findOneByRelationship(otherPublication, publicationIsVersionOf), nullValue());
        assertThat(findOneByRelationship(otherParentProject, parentProjectIsVersionOf), nullValue());

        Item firstPersonV2 = findOneByRelationship(firstPerson, personIsVersionOf);
        assertThat(firstPersonV2.isArchived(), is(true));
        assertThat(firstPersonV2.getOwningCollection(), is(persons));
        assertThat(firstPersonV2, notNullValue());
        assertThat(firstPersonV2.getMetadata(), hasItem(with("dc.title", "First Person")));
        assertThat(firstPersonV2.getMetadata(), not(hasItem(with("synsicris.isLastVersion", "true"))));
        assertThat(firstPersonV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", firstPerson.getID().toString() + "_2")));

        Item secondPersonV2 = findOneByRelationship(secondPerson, personIsVersionOf);
        assertThat(secondPersonV2.isArchived(), is(true));
        assertThat(secondPersonV2.getOwningCollection(), is(persons));
        assertThat(secondPersonV2, notNullValue());
        assertThat(secondPersonV2.getMetadata(), hasItem(with("dc.title", "Second Person")));
        assertThat(secondPersonV2.getMetadata(), not(hasItem(with("synsicris.isLastVersion", "true"))));
        assertThat(secondPersonV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", secondPerson.getID().toString() + "_2")));

        Item publicationV2 = findOneByRelationship(publication, publicationIsVersionOf);
        assertThat(publicationV2.isArchived(), is(true));
        assertThat(publicationV2.getOwningCollection(), is(publications));
        assertThat(publicationV2, notNullValue());
        assertThat(publicationV2.getMetadata(), hasItem(with("dc.title", "Publication")));
        assertThat(publicationV2.getMetadata(), not(hasItem(with("synsicris.isLastVersion", "true"))));
        assertThat(publicationV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", publication.getID().toString() + "_2")));
        assertThat(publicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "First Shared Person", sharedPerson1.getID().toString(), 0, 600)));
        assertThat(publicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "First Person", firstPersonV2.getID().toString(), 1, 600)));

        Item subPublicationV2 = findOneByRelationship(subPublication, publicationIsVersionOf);
        assertThat(subPublicationV2.isArchived(), is(true));
        assertThat(subPublicationV2.getOwningCollection(), is(subPublications));
        assertThat(subPublicationV2, notNullValue());
        assertThat(subPublicationV2.getMetadata(), hasItem(with("dc.title", "Sub publication")));
        assertThat(subPublicationV2.getMetadata(), not(hasItem(with("synsicris.isLastVersion", "true"))));
        assertThat(subPublicationV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", subPublication.getID().toString() + "_2")));
        assertThat(subPublicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "First Person", firstPersonV2.getID().toString(), 0, 600)));
        assertThat(subPublicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "Second Person", secondPersonV2.getID().toString(), 1, 600)));
        assertThat(subPublicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "Second Shared Person", sharedPerson2.getID().toString(), 2, 600)));

        Item parentProjectV2 = findOneByRelationship(parentProject, parentProjectIsVersionOf);
        assertThat(parentProjectV2.isArchived(), is(true));
        assertThat(parentProjectV2.getOwningCollection(), is(parentProject.getOwningCollection()));
        assertThat(parentProjectV2.getMetadata(), hasItem(with("dc.title", "Test project")));
        assertThat(parentProjectV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", parentProject.getID().toString() + "_2")));
        assertThat(parentProjectV2.getMetadata(), hasItem(with("synsicris.isLastVersion", "true")));

        assertThat(getVersionNumber(firstPersonV2), is(2));
        assertThat(getVersionNumber(secondPersonV2), is(2));
        assertThat(getVersionNumber(publicationV2), is(2));
        assertThat(getVersionNumber(subPublicationV2), is(2));
        assertThat(getVersionNumber(parentProjectV2), is(2));

        context.commit();
        VersionBuilder.delete(idRef.get());
    }

    @Test
    public void testProjectManyVersioning() throws Exception {
        context.turnOffAuthorisationSystem();

        Item person = ItemBuilder.createItem(context, persons)
            .withTitle("Person")
            .build();

        Item publication = ItemBuilder.createItem(context, publications)
            .withTitle("Publication")
            .withAuthor("Person", person.getID().toString())
            .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);
        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated())
            .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

        Item personV2 = findOneByRelationship(person, personIsVersionOf);
        assertThat(personV2.isArchived(), is(true));
        assertThat(personV2.getOwningCollection(), is(persons));
        assertThat(personV2, notNullValue());
        assertThat(personV2.getMetadata(), hasItem(with("dc.title", "Person")));
        assertThat(personV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", person.getID().toString() + "_2")));

        Item publicationV2 = findOneByRelationship(publication, publicationIsVersionOf);
        assertThat(publicationV2.isArchived(), is(true));
        assertThat(publicationV2.getOwningCollection(), is(publications));
        assertThat(publicationV2, notNullValue());
        assertThat(publicationV2.getMetadata(), hasItem(with("dc.title", "Publication")));
        assertThat(publicationV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", publication.getID().toString() + "_2")));
        assertThat(publicationV2.getMetadata(),
            hasItem(with("dc.contributor.author", "Person", personV2.getID().toString(), 600)));

        Item parentProjectV2 = findOneByRelationship(parentProject, parentProjectIsVersionOf);
        assertThat(parentProjectV2.isArchived(), is(true));
        assertThat(parentProjectV2.getOwningCollection(), is(parentProject.getOwningCollection()));
        assertThat(parentProjectV2.getMetadata(), hasItem(with("dc.title", "Test project")));
        assertThat(parentProjectV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", parentProject.getID().toString() + "_2")));
        assertThat(parentProjectV2.getMetadata(), hasItem(with("synsicris.isLastVersion", "true")));

        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated());

        assertThat(findOneByRelationship(personV2, personIsVersionOf), nullValue());
        assertThat(findOneByRelationship(publicationV2, publicationIsVersionOf), nullValue());

        List<Item> personVersions = findByRelationship(person, personIsVersionOf);
        assertThat(personVersions, hasSize(2));

        Item personV3 = personVersions.get(0).equals(personV2) ? personVersions.get(1) : personVersions.get(0);
        assertThat(personV3.isArchived(), is(true));
        assertThat(personV3.getOwningCollection(), is(persons));
        assertThat(personV3, notNullValue());
        assertThat(personV3.getMetadata(), hasItem(with("dc.title", "Person")));
        assertThat(personV3.getMetadata(),
            hasItem(with("synsicris.uniqueid", person.getID().toString() + "_3")));

        List<Item> pubVersions = findByRelationship(publication, publicationIsVersionOf);
        assertThat(pubVersions, hasSize(2));

        Item publicationV3 = pubVersions.get(0).equals(publicationV2) ? pubVersions.get(1) : pubVersions.get(0);
        assertThat(publicationV3.isArchived(), is(true));
        assertThat(publicationV3.getOwningCollection(), is(publications));
        assertThat(publicationV3, notNullValue());
        assertThat(publicationV3.getMetadata(), hasItem(with("dc.title", "Publication")));
        assertThat(publicationV3.getMetadata(),
            hasItem(with("synsicris.uniqueid", publication.getID().toString() + "_3")));
        assertThat(publicationV3.getMetadata(),
            hasItem(with("dc.contributor.author", "Person", personV3.getID().toString(), 600)));

        List<Item> prjVersions = findByRelationship(parentProject, parentProjectIsVersionOf);
        assertThat(prjVersions, hasSize(2));

        Item parentProjectV3 = prjVersions.get(0).equals(parentProjectV2) ? prjVersions.get(1) : prjVersions.get(0);
        assertThat(parentProjectV3.isArchived(), is(true));
        assertThat(parentProjectV3.getOwningCollection(), is(parentProject.getOwningCollection()));
        assertThat(parentProjectV3.getMetadata(), hasItem(with("dc.title", "Test project")));
        assertThat(parentProjectV3.getMetadata(),
            hasItem(with("synsicris.uniqueid", parentProject.getID().toString() + "_3")));
        assertThat(parentProjectV3.getMetadata(), hasItem(with("synsicris.isLastVersion", "true")));

        parentProjectV2 = context.reloadEntity(parentProjectV2);
        assertThat(parentProjectV2.getMetadata(), not(hasItem(with("synsicris.isLastVersion", "true"))));

        assertThat(getVersionNumber(personV2), is(2));
        assertThat(getVersionNumber(personV3), is(3));
        assertThat(getVersionNumber(publicationV2), is(2));
        assertThat(getVersionNumber(publicationV3), is(3));
        assertThat(getVersionNumber(parentProjectV2), is(2));
        assertThat(getVersionNumber(parentProjectV3), is(3));

        context.commit();
        VersionBuilder.delete(idRef.get());
    }

    @Test
    public void testProjectVersioningWithWorkspaceItem() throws Exception {
        context.turnOffAuthorisationSystem();

        Item firstPerson = ItemBuilder.createItem(context, persons)
            .withTitle("First Person")
            .build();

        WorkspaceItem secondPerson = WorkspaceItemBuilder.createWorkspaceItem(context, persons)
            .withTitle("Second Person")
            .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated())
            .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

        Item firstPersonV2 = findOneByRelationship(firstPerson, personIsVersionOf);
        assertThat(firstPersonV2.isArchived(), is(true));
        assertThat(firstPersonV2.getOwningCollection(), is(persons));
        assertThat(firstPersonV2, notNullValue());
        assertThat(firstPersonV2.getMetadata(), hasItem(with("dc.title", "First Person")));
        assertThat(firstPersonV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", firstPerson.getID().toString() + "_2")));

        assertThat(findOneByRelationship(secondPerson.getItem(), personIsVersionOf), nullValue());

        context.commit();
        VersionBuilder.delete(idRef.get());
    }

    @Test
    public void testProjectVersioningWithResourcePoliciesCopy() throws Exception {

        context.turnOffAuthorisationSystem();

        Item person = ItemBuilder.createItem(context, persons)
            .withTitle("Person")
            .build();

        ResourcePolicyBuilder.createResourcePolicy(context)
            .withUser(eperson)
            .withPolicyType(ResourcePolicy.TYPE_INHERITED)
            .withDspaceObject(person)
            .withName("Test resource policy 1")
            .withAction(Constants.READ)
            .build();

        ResourcePolicyBuilder.createResourcePolicy(context)
            .withUser(admin)
            .withPolicyType(ResourcePolicy.TYPE_CUSTOM)
            .withDspaceObject(person)
            .withName("Test resource policy 2")
            .withAction(Constants.READ)
            .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated())
            .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

        Item personV2 = findOneByRelationship(person, personIsVersionOf);
        assertThat(personV2.isArchived(), is(true));
        assertThat(personV2.getOwningCollection(), is(persons));
        assertThat(personV2, notNullValue());
        assertThat(personV2.getMetadata(), hasItem(with("dc.title", "Person")));
        assertThat(personV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", person.getID().toString() + "_2")));

        List<ResourcePolicy> resourcePolicies = personV2.getResourcePolicies();
        assertThat(resourcePolicies, hasSize(2));
        assertThat(resourcePolicies, hasItem(matches(Constants.READ, admin, ResourcePolicy.TYPE_CUSTOM)));
        assertThat(resourcePolicies, hasItem(matches(Constants.READ, eperson, ResourcePolicy.TYPE_INHERITED)));

        context.commit();
        VersionBuilder.delete(idRef.get());
    }

    @Test
    public void testProjectVersioningWithFunderGroupReadPolicy() throws Exception {

        context.turnOffAuthorisationSystem();

        Group funderGroup = GroupBuilder.createGroup(context)
            .withName("Funder group")
            .build();

        configurationService.setProperty("project.funder_programme.group", funderGroup.getID());

        Item person = ItemBuilder.createItem(context, persons)
            .withTitle("Person")
            .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
            .content("/api/core/items/" + parentProject.getID()))
            .andExpect(status().isCreated())
            .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));

        Item personV2 = findOneByRelationship(person, personIsVersionOf);
        assertThat(personV2.isArchived(), is(true));
        assertThat(personV2.getOwningCollection(), is(persons));
        assertThat(personV2, notNullValue());
        assertThat(personV2.getMetadata(), hasItem(with("dc.title", "Person")));
        assertThat(personV2.getMetadata(),
            hasItem(with("synsicris.uniqueid", person.getID().toString() + "_2")));

        assertThat(personV2.getResourcePolicies(), hasItem(matches(Constants.READ, funderGroup, null)));

        context.commit();
        VersionBuilder.delete(idRef.get());
    }

    @Test
    public void testProjectDeleteManyVersioning() throws Exception {
        context.commit();
        context.turnOffAuthorisationSystem();

        Item person = ItemBuilder.createItem(context, persons)
                                 .withTitle("Person")
                                 .build();

        Item publication = ItemBuilder.createItem(context, publications)
                                      .withTitle("Publication")
                                      .withAuthor("Person", person.getID().toString())
                                      .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        AtomicReference<Integer> idRef = new AtomicReference<>();
        getClient(token).perform(post("/api/versioning/versions")
                            .contentType(MediaType.parseMediaType(RestMediaTypes.TEXT_URI_LIST_VALUE))
                            .content("/api/core/items/" + parentProject.getID()))
                        .andExpect(status().isCreated())
                        .andDo(result -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
        context.commit();

        Version version = versioningService.getVersion(context, idRef.get());
        Item versionedItem = null;
        try {
            assertNotNull(version);
            assertNotNull(version.getItem());

            versionedItem = this.itemService.find(context, version.getItem().getID());
            getClient().perform(get("/api/core/items/" + versionedItem.getID()))
                .andExpect(status().isOk());
//            assertEquals(2, versionedItems.size());

            List<Item> versionedItems =
                findVersionedItems(context, context.reloadEntity(versionedItem), idRef.get().toString());

            token = getAuthToken(admin.getEmail(), password);

            // when delete versionedItem
            getClient(token).perform(delete("/api/core/items/" + versionedItem.getID()))
                            .andExpect(status().isNoContent());

            // check that versionedItem is deleted.
            getClient().perform(get("/api/core/items/" + versionedItem.getID()))
                            .andExpect(status().isNotFound());

            parentProject = context.reloadEntity(parentProject);
            person = context.reloadEntity(person);
            publication = context.reloadEntity(publication);

            // all original items are existed.
            assertNotNull(parentProject);
            assertNotNull(person);
            assertNotNull(publication);

            version = context.reloadEntity(version);
            assertNotNull(version);

            // version not deleted, but doesn't contain item.
            assertNull(version.getItem());

            // check that all versioned Items related to parentProject will be deleted
            for (Item item : versionedItems) {
                assertNull(context.reloadEntity(item));
            }
        } finally {
            context.turnOffAuthorisationSystem();
            VersionBuilder.delete(idRef.get());
            context.restoreAuthSystemState();
        }
    }

    private Community createCommunity(String name) {
        return CommunityBuilder.createCommunity(context)
            .withName(name)
            .build();
    }

    private Community createSubCommunity(String name, Community parent) {
        return CommunityBuilder.createSubCommunity(context, parent)
            .withName(name)
            .build();
    }

    private Collection createCollection(String name, String entityType, Community parent) {
        return CollectionBuilder.createCollection(context, parent)
            .withName(name)
            .withEntityType(entityType)
            .build();
    }

    private RelationshipType createIsVersionRelationshipType(String entityType) {

        EntityType type = EntityTypeBuilder.createEntityTypeBuilder(context, entityType).build();

        return RelationshipTypeBuilder
            .createRelationshipTypeBuilder(context, type, type, "isVersionOf", "hasVersion", 0, 1, 0, null)
            .build();
    }

    private int getVersionNumber(Item item) throws SQLException {
        Version version = versioningService.getVersion(context, item);
        assertThat(version, notNullValue());
        return version.getVersionNumber();
    }

    private Item findOneByRelationship(Item item, RelationshipType type) throws SQLException {

        List<Relationship> relationships = relationshipService
            .findByItemAndRelationshipType(context, item, type, false);

        if (relationships.isEmpty()) {
            return null;
        }

        assertThat(relationships, hasSize(1));
        return relationships.get(0).getLeftItem();
    }

    private List<Item> findByRelationship(Item item, RelationshipType type) throws SQLException {
        return relationshipService.findByItemAndRelationshipType(context, item, type, false).stream()
            .map(Relationship::getLeftItem)
            .collect(Collectors.toList());
    }

    private List<Item> findVersionedItems(Context c, Item projectItem, String versionNumber) throws SQLException {
        List<Item> versionedItems = new ArrayList<>();
        Community community = this.projectConsumerService.getProjectCommunity(c, projectItem);
        this.projectConsumerService
            .findVersionedItemsRelatedToProject(c, community, projectItem, versionNumber)
            .forEachRemaining(versionedItems::add);
        return versionedItems;
    }

}
