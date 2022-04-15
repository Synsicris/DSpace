/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.dspace.app.rest.matcher.EntityTypeMatcher;
import org.dspace.app.rest.matcher.RelationshipTypeMatcher;
import org.dspace.app.rest.test.AbstractEntityIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.RelationshipType;
import org.dspace.content.service.EntityTypeService;
import org.dspace.core.Constants;
import org.dspace.external.provider.AbstractExternalDataProvider;
import org.dspace.external.service.ExternalDataService;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test class for the entity type endpoint
 *
 * @author Mykhaylo Boychuk - 4Science
 */
public class EntityTypeRestRepositoryIT extends AbstractEntityIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ExternalDataService externalDataService;
    @Autowired
    private EntityTypeService entityTypeService;
    private EntityType publicationType;
    private EntityType journalType;
    private EntityType journalIssueType;
    private EntityType orgUnitType;
    private EntityType dataPackageType;
    private EntityType personType;
    private EntityType journalVolumeType;
    private EntityType projectType;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        context.turnOffAuthorisationSystem();

        publicationType = entityTypeService.findByEntityType(context, "Publication");
        if (publicationType == null) {
            publicationType = EntityTypeBuilder.createEntityTypeBuilder(context, "Publication").build();
        }
        journalType = entityTypeService.findByEntityType(context, "Journal");
        if (journalType == null) {
            journalType = EntityTypeBuilder.createEntityTypeBuilder(context, "Journal").build();
        }
        personType = entityTypeService.findByEntityType(context, "Person");
        if (personType == null) {
            personType = EntityTypeBuilder.createEntityTypeBuilder(context, "Person").build();
        }
        projectType = entityTypeService.findByEntityType(context, "Project");
        if (projectType == null) {
            projectType = EntityTypeBuilder.createEntityTypeBuilder(context, "Project").build();
        }
        journalVolumeType = entityTypeService.findByEntityType(context, "JournalVolume");
        if (journalVolumeType == null) {
            journalVolumeType = EntityTypeBuilder.createEntityTypeBuilder(context, "JournalVolume").build();
        }
        journalIssueType = entityTypeService.findByEntityType(context, "JournalIssue");
        if (journalIssueType == null) {
            journalIssueType = EntityTypeBuilder.createEntityTypeBuilder(context, "JournalIssue").build();
        }
        orgUnitType = entityTypeService.findByEntityType(context, "OrgUnit");
        if (orgUnitType == null) {
            orgUnitType = EntityTypeBuilder.createEntityTypeBuilder(context, "OrgUnit").build();
        }

        context.restoreAuthSystemState();
    }

    @Test
    public void getAllEntityTypeEndpoint() throws Exception {
        //When we call this facets endpoint
        getClient().perform(get("/api/core/entitytypes"))

                   //We expect a 200 OK status
                   .andExpect(status().isOk())
                   //The type has to be 'discover'
                   .andExpect(jsonPath("$.page.totalElements", is(8)))
                   //There needs to be a self link to this endpoint
                   .andExpect(jsonPath("$._links.self.href", containsString("api/core/entitytypes")))
                   //We have 4 facets in the default configuration, they need to all be present in the embedded section
                   .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context,
                                                              Constants.ENTITY_TYPE_NONE)),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Person")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "OrgUnit")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal")),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalVolume")),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue"))
                   )));
    }

    @Test
    public void getAllEntityTypeEndpointWithPaging() throws Exception {
        getClient().perform(get("/api/core/entitytypes").param("size", "5"))

                   //We expect a 200 OK status
                   .andExpect(status().isOk())
                   //The type has to be 'discover'
                   .andExpect(jsonPath("$.page.size", is(5)))
                   .andExpect(jsonPath("$.page.totalElements", is(8)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   //There needs to be a self link to this endpoint
                   .andExpect(jsonPath("$._links.self.href", containsString("api/core/entitytypes")))
                   //We have 4 facets in the default configuration, they need to all be present in the embedded section
                   .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context,
                             Constants.ENTITY_TYPE_NONE)),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Person")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "OrgUnit"))
                   )));

        getClient().perform(get("/api/core/entitytypes").param("size", "5").param("page", "1"))

                   //We expect a 200 OK status
                   .andExpect(status().isOk())
                   //The type has to be 'discover'
                   .andExpect(jsonPath("$.page.size", is(5)))
                   .andExpect(jsonPath("$.page.totalElements", is(8)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   .andExpect(jsonPath("$.page.number", is(1)))
                   //There needs to be a self link to this endpoint
                   .andExpect(jsonPath("$._links.self.href", containsString("api/core/entitytypes")))
                   //We have 4 facets in the default configuration, they need to all be present in the embedded section
                   .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                       EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal")),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalVolume")),
                       EntityTypeMatcher
                           .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue"))
                   )));
    }

    @Test
    public void retrieveOneEntityType() throws Exception {
        EntityType entityType = entityTypeService.findByEntityType(context, "Publication");
        getClient().perform(get("/api/core/entitytypes/" + entityType.getID()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", EntityTypeMatcher.matchEntityTypeEntry(entityType)));
    }

    @Test
    public void retrieveOneEntityTypeThatDoesNotExist() throws Exception {
        getClient().perform(get("/api/core/entitytypes/" + 5555))
                   .andExpect(status().isNotFound());
    }

    @Test
    public void findAllByAuthorizedCollection() throws Exception {
        try {
            context.turnOffAuthorisationSystem();

            //** GIVEN **
            //1. A community-collection structure with one parent community with sub-community and one collection.
            parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .build();
            Collection col1 =
                CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("JournalIssue")
                        .withSubmitterGroup(eperson)
                        .withName("Collection 1")
                        .build();
            Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Publication")
                    .withSubmitterGroup(eperson)
                     .withName("Collection 2")
                    .build();
            Collection col3 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Project")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 3")
                    .build();
            Collection col4 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Journal")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 4")
                    .build();

            context.restoreAuthSystemState();


            context.setCurrentUser(eperson);
            String token = getAuthToken(eperson.getEmail(), password);
            getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                    EntityTypeMatcher
                        .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal"))
                )));
        } finally {
            CommunityBuilder.deleteCommunity(parentCommunity.getID());
        }
    }

    @Test
    public void findAllByAuthorizedCollectionWithScope() throws Exception {
        Community parentCommunity2 = null;
        try {
            context.turnOffAuthorisationSystem();

            //** GIVEN **
            //1. A community-collection structure with one parent community with sub-community and one collection.
            parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .build();

            parentCommunity2 = CommunityBuilder.createCommunity(context)
                    .withName("Parent Community2")
                    .build();
            Collection col1 =
                CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("JournalIssue")
                        .withSubmitterGroup(eperson)
                        .withName("Collection 1")
                        .build();
            Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Publication")
                    .withSubmitterGroup(eperson)
                     .withName("Collection 2")
                    .build();
            Collection col3 = CollectionBuilder.createCollection(context, parentCommunity2)
                    .withEntityType("Project")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 3")
                    .build();
            Collection col4 = CollectionBuilder.createCollection(context, parentCommunity2)
                    .withEntityType("Journal")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 4")
                    .build();

            context.restoreAuthSystemState();


            context.setCurrentUser(eperson);
            String token = getAuthToken(eperson.getEmail(), password);
            getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection")
                    .param("scope", parentCommunity2.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal"))
                )))
                .andExpect(jsonPath("$._embedded.entitytypes", Matchers.not(containsInAnyOrder(
                    EntityTypeMatcher
                        .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication"))
                ))));
        } finally {
            CommunityBuilder.deleteCommunity(parentCommunity.getID());
            if (parentCommunity2 != null) {
                CommunityBuilder.deleteCommunity(parentCommunity2.getID());
            }
        }
    }

    @Test
    public void findAllByAuthorizedCollectionWithExcludedList() throws Exception {
        try {
            context.turnOffAuthorisationSystem();
            configurationService.setProperty("project.entity-name.to-skip", "JournalIssue, Journal");

            //** GIVEN **
            //1. A community-collection structure with one parent community with sub-community and one collection.
            parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .build();
            Collection col1 =
                CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("JournalIssue")
                        .withSubmitterGroup(eperson)
                        .withName("Collection 1")
                        .build();
            Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Publication")
                    .withSubmitterGroup(eperson)
                     .withName("Collection 2")
                    .build();
            Collection col3 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Project")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 3")
                    .build();
            Collection col4 = CollectionBuilder.createCollection(context, parentCommunity)
                    .withEntityType("Journal")
                    .withSubmitterGroup(eperson)
                    .withName("Collection 4")
                    .build();

            context.restoreAuthSystemState();


            context.setCurrentUser(eperson);
            String token = getAuthToken(eperson.getEmail(), password);
            getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                        EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                        EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication"))
                    )))
                .andExpect(jsonPath("$._embedded.entitytypes", Matchers.not(containsInAnyOrder(
                    EntityTypeMatcher
                        .matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal"))
                ))));
        } finally {
            CommunityBuilder.deleteCommunity(parentCommunity.getID());
        }
    }

    @Test
    public void findAllPaginationTest() throws Exception {
        getClient().perform(get("/api/core/entitytypes")
                   .param("page", "0")
                   .param("size", "3"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Person")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context,
                    Constants.ENTITY_TYPE_NONE))
                    )))
                   .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.next.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=2"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$.page.size", is(3)))
                   .andExpect(jsonPath("$.page.totalElements", is(8)))
                   .andExpect(jsonPath("$.page.totalPages", is(3)))
                   .andExpect(jsonPath("$.page.number", is(0)));

        getClient().perform(get("/api/core/entitytypes")
                   .param("page", "1")
                   .param("size", "3"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "OrgUnit")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal")),
                    EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project"))
                    )))
                   .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.prev.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.next.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=2"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                           Matchers.containsString("/api/core/entitytypes?"),
                           Matchers.containsString("page=2"), Matchers.containsString("size=3"))))
                   .andExpect(jsonPath("$.page.size", is(3)))
                   .andExpect(jsonPath("$.page.totalElements", is(8)))
                   .andExpect(jsonPath("$.page.totalPages", is(3)))
                   .andExpect(jsonPath("$.page.number", is(1)));
    }

    @Test
    public void findEntityTypeWithEmbedRelatioshipTypeTest() throws Exception {

        EntityType person = entityTypeService.findByEntityType(context, "Person");
        EntityType orgunit = entityTypeService.findByEntityType(context, "OrgUnit");
        EntityType project = entityTypeService.findByEntityType(context, "Project");
        EntityType publication = entityTypeService.findByEntityType(context, "Publication");
        EntityType journalIssue = entityTypeService.findByEntityType(context, "journalIssue");

        RelationshipType relationshipType1 = relationshipTypeService.findbyTypesAndTypeName(context,
                             publication, person, "isAuthorOfPublication", "isPublicationOfAuthor");
        RelationshipType relationshipType2 = relationshipTypeService.findbyTypesAndTypeName(context,
                          publication, project, "isProjectOfPublication", "isPublicationOfProject");
        RelationshipType relationshipType3 = relationshipTypeService.findbyTypesAndTypeName(context,
                          publication, orgunit, "isOrgUnitOfPublication", "isPublicationOfOrgUnit");
        RelationshipType relationshipType4 = relationshipTypeService.findbyTypesAndTypeName(context,
           journalIssue, publication, "isPublicationOfJournalIssue", "isJournalIssueOfPublication");
        RelationshipType relationshipType5 = relationshipTypeService.findbyTypesAndTypeName(context,
                             publication, orgunit, "isAuthorOfPublication","isPublicationOfAuthor");

        getClient().perform(get("/api/core/entitytypes/" + publication.getID())
                   .param("embed", "relationshiptypes"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", EntityTypeMatcher.matchEntityTypeEntry(publication)))
                   .andExpect(jsonPath("$._embedded.relationshiptypes._embedded.relationshiptypes", containsInAnyOrder(
                           RelationshipTypeMatcher.matchRelationshipTypeEntry(relationshipType1),
                           RelationshipTypeMatcher.matchRelationshipTypeEntry(relationshipType2),
                           RelationshipTypeMatcher.matchRelationshipTypeEntry(relationshipType3),
                           RelationshipTypeMatcher.matchRelationshipTypeEntry(relationshipType4),
                           RelationshipTypeMatcher.matchRelationshipTypeEntry(relationshipType5)
                           )));
    }

    @Test
    public void findAllByAuthorizedCollectionTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

       CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("JournalIssue")
                        .withSubmitterGroup(eperson)
                        .withName("Collection 1")
                        .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Publication")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 2")
                         .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Project")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 3")
                         .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Journal")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 4")
                         .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);
        getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue")),
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project")),
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal"))
                   )));
    }

    @Test
    public void findAllByAuthorizedCollectionPaginationTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("JournalIssue")
                        .withSubmitterGroup(eperson)
                        .withName("Collection 1")
                        .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Publication")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 2")
                         .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Project")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 3")
                         .build();

        CollectionBuilder.createCollection(context, parentCommunity)
                         .withEntityType("Journal")
                         .withSubmitterGroup(eperson)
                         .withName("Collection 4")
                         .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);
        getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection")
                        .param("size", "2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "JournalIssue")),
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Journal"))
                         )))
                        .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                                  Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                  Matchers.containsString("page=0"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                                  Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                  Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.next.href", Matchers.allOf(
                                  Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                  Matchers.containsString("page=1"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                                  Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                  Matchers.containsString("page=1"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$.page.size", is(2)))
                        .andExpect(jsonPath("$.page.totalElements", is(4)))
                        .andExpect(jsonPath("$.page.totalPages", is(2)))
                        .andExpect(jsonPath("$.page.number", is(0)));

        getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedCollection")
                        .param("page", "1")
                        .param("size", "2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Publication")),
                   EntityTypeMatcher.matchEntityTypeEntry(entityTypeService.findByEntityType(context, "Project"))
                         )))
                        .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                                Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                Matchers.containsString("page=0"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                                Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                Matchers.containsString("page=1"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.prev.href", Matchers.allOf(
                                Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                Matchers.containsString("page=0"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                                Matchers.containsString("api/core/entitytypes/search/findAllByAuthorizedCollection?"),
                                Matchers.containsString("page=1"), Matchers.containsString("size=2"))))
                        .andExpect(jsonPath("$.page.size", is(2)))
                        .andExpect(jsonPath("$.page.totalElements", is(4)))
                        .andExpect(jsonPath("$.page.totalPages", is(2)))
                        .andExpect(jsonPath("$.page.number", is(1)));
    }

    @Test
    public void findAllByAuthorizedExternalSource() throws Exception {
        context.turnOffAuthorisationSystem();

        EntityType publication = entityTypeService.findByEntityType(context, "Publication");
        EntityType orgUnit = entityTypeService.findByEntityType(context, "OrgUnit");
        EntityType project = entityTypeService.findByEntityType(context, "Project");
        EntityType funding = EntityTypeBuilder.createEntityTypeBuilder(context, "Funding").build();

        Community rootCommunity = CommunityBuilder.createCommunity(context)
                                                  .withName("Parent Community")
                                                  .build();

        CollectionBuilder.createCollection(context, rootCommunity)
                                           .withEntityType(orgUnit.getLabel())
                                           .withName("Collection 1")
                                           .build();

        CollectionBuilder.createCollection(context, rootCommunity)
                                           .withEntityType(publication.getLabel())
                                           .withSubmitterGroup(eperson)
                                           .withName("Collection 2")
                                           .build();

        CollectionBuilder.createCollection(context, rootCommunity)
                                           .withEntityType(project.getLabel())
                                           .withSubmitterGroup(eperson)
                                           .withName("Collection 3")
                                           .build();

        CollectionBuilder.createCollection(context, rootCommunity)
                                           .withEntityType(funding.getLabel())
                                           .withSubmitterGroup(eperson)
                                           .withName("Collection 4")
                                           .build();

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);
        getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedExternalSource"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                                   EntityTypeMatcher.matchEntityTypeEntry(publication),
                                   EntityTypeMatcher.matchEntityTypeEntry(funding),
                                   EntityTypeMatcher.matchEntityTypeEntry(project))))
                        .andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));

        String adminToken = getAuthToken(admin.getEmail(), password);
        getClient(adminToken).perform(get("/api/core/entitytypes/search/findAllByAuthorizedExternalSource"))
                             .andExpect(status().isOk())
                             .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                                        EntityTypeMatcher.matchEntityTypeEntry(orgUnit),
                                        EntityTypeMatcher.matchEntityTypeEntry(funding),
                                        EntityTypeMatcher.matchEntityTypeEntry(project),
                                        EntityTypeMatcher.matchEntityTypeEntry(publication))))
                             .andExpect(jsonPath("$.page.totalElements", Matchers.is(4)));

        try {
            ((AbstractExternalDataProvider) externalDataService.getExternalDataProvider("mock"))
                    .setSupportedEntityTypes(Arrays.asList("Publication"));
            ((AbstractExternalDataProvider) externalDataService.getExternalDataProvider("pubmed"))
                    .setSupportedEntityTypes(Arrays.asList("Publication"));

            // these are similar to the previous checks but now we have restricted the mock and pubmed providers
            // to support only publication, this mean that there are no providers suitable for funding
            getClient(token).perform(get("/api/core/entitytypes/search/findAllByAuthorizedExternalSource"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                                       EntityTypeMatcher.matchEntityTypeEntry(publication),
                                       EntityTypeMatcher.matchEntityTypeEntry(funding),
                                       EntityTypeMatcher.matchEntityTypeEntry(project))))
                            .andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));

            getClient(adminToken).perform(get("/api/core/entitytypes/search/findAllByAuthorizedExternalSource"))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$._embedded.entitytypes", containsInAnyOrder(
                                            EntityTypeMatcher.matchEntityTypeEntry(project),
                                            EntityTypeMatcher.matchEntityTypeEntry(orgUnit),
                                            EntityTypeMatcher.matchEntityTypeEntry(funding),
                                            EntityTypeMatcher.matchEntityTypeEntry(publication))))
                                 .andExpect(jsonPath("$.page.totalElements", Matchers.is(4)));

        } finally {
            ((AbstractExternalDataProvider) externalDataService.getExternalDataProvider("mock"))
                    .setSupportedEntityTypes(null);
            ((AbstractExternalDataProvider) externalDataService.getExternalDataProvider("pubmed"))
                    .setSupportedEntityTypes(null);
        }

    }

}
