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
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadataNotEmpty;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadataStringEndsWith;
import static org.dspace.builder.CollectionBuilder.createCollection;
import static org.dspace.builder.CommunityBuilder.createCommunity;
import static org.dspace.builder.CommunityBuilder.createSubCommunity;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.converter.CommunityConverter;
import org.dspace.app.rest.matcher.CollectionMatcher;
import org.dspace.app.rest.matcher.CommunityMatcher;
import org.dspace.app.rest.matcher.HalMatcher;
import org.dspace.app.rest.matcher.MetadataMatcher;
import org.dspace.app.rest.matcher.PageMatcher;
import org.dspace.app.rest.model.CommunityRest;
import org.dspace.app.rest.model.MetadataRest;
import org.dspace.app.rest.model.MetadataValueRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.test.MetadataPatchSuite;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.ResourcePolicyBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration Tests against the /api/core/communities endpoint (including any subpaths)
 */
public class CommunityRestRepositoryIT extends AbstractControllerIntegrationTest {

    @Autowired
    private CommunityConverter communityConverter;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private ResourcePolicyService resoucePolicyService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ItemService itemService;

    private Community topLevelCommunityA;
    private Community subCommunityA;
    private Community communityB;
    private Community communityC;
    private Collection collectionA;

    private EPerson topLevelCommunityAAdmin;
    private EPerson subCommunityAAdmin;
    private EPerson collectionAdmin;
    private EPerson submitter;

    @Test
    public void createTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CommunityRest comm = new CommunityRest();
        CommunityRest commNoembeds = new CommunityRest();
        // We send a name but the created community should set this to the title
        comm.setName("Test Top-Level Community");
        commNoembeds.setName("Test Top-Level Community Full");

        MetadataRest metadataRest = new MetadataRest();

        MetadataValueRest description = new MetadataValueRest();
        description.setValue("<p>Some cool HTML code here</p>");
        metadataRest.put("dc.description", description);

        MetadataValueRest abs = new MetadataValueRest();
        abs.setValue("Sample top-level community created via the REST API");
        metadataRest.put("dc.description.abstract", abs);

        MetadataValueRest contents = new MetadataValueRest();
        contents.setValue("<p>HTML News</p>");
        metadataRest.put("dc.description.tableofcontents", contents);

        MetadataValueRest copyright = new MetadataValueRest();
        copyright.setValue("Custom Copyright Text");
        metadataRest.put("dc.rights", copyright);

        MetadataValueRest title = new MetadataValueRest();
        title.setValue("Title Text");
        metadataRest.put("dc.title", title);

        comm.setMetadata(metadataRest);
        commNoembeds.setMetadata(metadataRest);


        String authToken = getAuthToken(admin.getEmail(), password);

        // Capture the UUID of the created Community (see andDo() below)
        AtomicReference<UUID> idRef = new AtomicReference<>();
        AtomicReference<UUID> idRefNoEmbeds = new AtomicReference<>();
        AtomicReference<String> handle = new AtomicReference<>();

        try {
            getClient(authToken).perform(post("/api/core/communities")
                                        .content(mapper.writeValueAsBytes(comm))
                                        .contentType(contentType)
                                   .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(contentType))
                                .andExpect(jsonPath("$", CommunityMatcher.matchNonAdminEmbeds()))
                                .andExpect(jsonPath("$", Matchers.allOf(
                                    hasJsonPath("$.id", not(empty())),
                                    hasJsonPath("$.uuid", not(empty())),
                                    hasJsonPath("$.name", is("Title Text")),
                                    hasJsonPath("$.handle", not(empty())),
                                    hasJsonPath("$.type", is("community")),
                                    hasJsonPath("$._links.collections.href", not(empty())),
                                    hasJsonPath("$._links.logo.href", not(empty())),
                                    hasJsonPath("$._links.subcommunities.href", not(empty())),
                                    hasJsonPath("$._links.self.href", not(empty())),
                                    hasJsonPath("$.metadata", Matchers.allOf(
                                            matchMetadata("dc.description", "<p>Some cool HTML code here</p>"),
                                            matchMetadata("dc.description.abstract",
                                                   "Sample top-level community created via the REST API"),
                                            matchMetadata("dc.description.tableofcontents", "<p>HTML News</p>"),
                                            matchMetadata("dc.rights", "Custom Copyright Text"),
                                            matchMetadata("dc.title", "Title Text")
                                            )
                                        )
                                )))

                                // capture "handle" returned in JSON response and check against the metadata
                                .andDo(result -> handle.set(
                                        read(result.getResponse().getContentAsString(), "$.handle")))
                                .andExpect(jsonPath("$",
                                    hasJsonPath("$.metadata", Matchers.allOf(
                                        matchMetadataNotEmpty("dc.identifier.uri"),
                                        matchMetadataStringEndsWith("dc.identifier.uri", handle.get())
                                        )
                                    )))

                                // capture "id" returned in JSON response
                                .andDo(result -> idRef
                                    .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));

            getClient(authToken).perform(post("/api/core/communities")
                    .content(mapper.writeValueAsBytes(commNoembeds))
                    .contentType(contentType))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$", HalMatcher.matchNoEmbeds()))
                    .andDo(result -> idRefNoEmbeds
                            .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));
        } finally {
            // Delete the created community (cleanup after ourselves!)
            CommunityBuilder.deleteCommunity(idRef.get());
            CommunityBuilder.deleteCommunity(idRefNoEmbeds.get());
        }
    }

    @Test
    public void createSubCommunityUnAuthorizedTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        // Create a parent community to POST a new sub-community to
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

        context.restoreAuthSystemState();

        ObjectMapper mapper = new ObjectMapper();
        CommunityRest comm = new CommunityRest();
        // We send a name but the created community should set this to the title
        comm.setName("Test Sub-Level Community");

        // Anonymous user tries to create a community.
        // Should fail because user is not authenticated. Error 401.
        getClient().perform(post("/api/core/communities")
            .content(mapper.writeValueAsBytes(comm))
            .param("parent", parentCommunity.getID().toString())
            .contentType(contentType))
                   .andExpect(status().isUnauthorized());

        // Non-admin Eperson tries to create a community.
        // Should fail because user doesn't have permissions. Error 403.
        String authToken = getAuthToken(eperson.getEmail(), password);
        getClient(authToken).perform(post("/api/core/communities")
            .content(mapper.writeValueAsBytes(comm))
            .param("parent", parentCommunity.getID().toString())
            .contentType(contentType))
                            .andExpect(status().isForbidden());
    }

    @Test
    public void createSubCommunityAuthorizedTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        // Create a parent community to POST a new sub-community to
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

        // ADD authorization on parent community
        context.setCurrentUser(eperson);
        authorizeService.addPolicy(context, parentCommunity, Constants.ADD, eperson);
        context.restoreAuthSystemState();

        String authToken = getAuthToken(eperson.getEmail(), password);

        ObjectMapper mapper = new ObjectMapper();
        CommunityRest comm = new CommunityRest();
        // We send a name but the created community should set this to the title
        comm.setName("Test Sub-Level Community");

        comm.setMetadata(new MetadataRest()
            .put("dc.description",
                new MetadataValueRest("<p>Some cool HTML code here</p>"))
            .put("dc.description.abstract",
                new MetadataValueRest("Sample top-level community created via the REST API"))
            .put("dc.description.tableofcontents",
                new MetadataValueRest("<p>HTML News</p>"))
            .put("dc.rights",
                new MetadataValueRest("Custom Copyright Text"))
            .put("dc.title",
                new MetadataValueRest("Title Text")));

        // Capture the UUID and Handle of the created Community (see andDo() below)
        AtomicReference<UUID> idRef = new AtomicReference<>();
        AtomicReference<String> handle = new AtomicReference<>();
        try {
            getClient(authToken).perform(post("/api/core/communities")
                .content(mapper.writeValueAsBytes(comm))
                .param("parent", parentCommunity.getID().toString())
                .contentType(contentType))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(contentType))
                                .andExpect(jsonPath("$", Matchers.allOf(
                                    hasJsonPath("$.id", not(empty())),
                                    hasJsonPath("$.uuid", not(empty())),
                                    hasJsonPath("$.name", is("Title Text")),
                                    hasJsonPath("$.handle", not(empty())),
                                    hasJsonPath("$.type", is("community")),
                                    hasJsonPath("$._links.collections.href", not(empty())),
                                    hasJsonPath("$._links.logo.href", not(empty())),
                                    hasJsonPath("$._links.subcommunities.href", not(empty())),
                                    hasJsonPath("$._links.self.href", not(empty())),
                                    hasJsonPath("$.metadata", Matchers.allOf(
                                            MetadataMatcher.matchMetadata("dc.description",
                                                "<p>Some cool HTML code here</p>"),
                                            MetadataMatcher.matchMetadata("dc.description.abstract",
                                                "Sample top-level community created via the REST API"),
                                            MetadataMatcher.matchMetadata("dc.description.tableofcontents",
                                                "<p>HTML News</p>"),
                                            MetadataMatcher.matchMetadata("dc.rights",
                                                "Custom Copyright Text"),
                                            MetadataMatcher.matchMetadata("dc.title",
                                                "Title Text")
                                            )
                                        )
                                )))
                                // capture "handle" returned in JSON response and check against the metadata
                                .andDo(result -> handle.set(
                                        read(result.getResponse().getContentAsString(), "$.handle")))
                                .andExpect(jsonPath("$",
                                    hasJsonPath("$.metadata", Matchers.allOf(
                                        matchMetadataNotEmpty("dc.identifier.uri"),
                                        matchMetadataStringEndsWith("dc.identifier.uri", handle.get())
                                    )
                                )))
                                // capture "id" returned in JSON response
                                .andDo(result -> idRef
                                    .set(UUID.fromString(read(result.getResponse().getContentAsString(), "$.id"))));
        } finally {
            // Delete the created community (cleanup after ourselves!)
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    @Test
    public void createUnauthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        ObjectMapper mapper = new ObjectMapper();
        CommunityRest comm = new CommunityRest();
        comm.setName("Test Top-Level Community");

        MetadataRest metadataRest = new MetadataRest();

        MetadataValueRest title = new MetadataValueRest();
        title.setValue("Title Text");
        metadataRest.put("dc.title", title);

        comm.setMetadata(metadataRest);

        context.restoreAuthSystemState();

        // Anonymous user tries to create a community.
        // Should fail because user is not authenticated. Error 401.
        getClient().perform(post("/api/core/communities")
                                        .content(mapper.writeValueAsBytes(comm))
                                        .contentType(contentType))
                   .andExpect(status().isUnauthorized());

        // Non-admin Eperson tries to create a community.
        // Should fail because user doesn't have permissions. Error 403.
        String authToken = getAuthToken(eperson.getEmail(), password);
        getClient(authToken).perform(post("/api/core/communities")
                                        .content(mapper.writeValueAsBytes(comm))
                                        .contentType(contentType))
                   .andExpect(status().isForbidden());
    }

    @Test
    public void findAllTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle()),
                       CommunityMatcher
                           .matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(), child1.getHandle())
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
                   .andExpect(jsonPath("$.page.size", is(20)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)))
        ;
    }

    @Test
    public void findOneTestWithEmbedsNoPageSize() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with 10 sub-communities
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child0 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 0")
                                           .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 1")
                                           .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 2")
                                           .build();
        Community child3 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 3")
                                           .build();
        Community child4 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 4")
                                           .build();
        Community child5 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 5")
                                           .build();
        Community child6 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 6")
                                           .build();
        Community child7 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 7")
                                           .build();
        Community child8 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 8")
                                           .build();
        Community child9 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 9")
                                           .build();


        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID())
                                    .param("embed", "subcommunities"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", CommunityMatcher.matchCommunity(parentCommunity)))
                   .andExpect(
                           jsonPath("$._embedded.subcommunities._embedded.subcommunities", Matchers.containsInAnyOrder(
                                   CommunityMatcher.matchCommunity(child0),
                                   CommunityMatcher.matchCommunity(child1),
                                   CommunityMatcher.matchCommunity(child2),
                                   CommunityMatcher.matchCommunity(child3),
                                   CommunityMatcher.matchCommunity(child4),
                                   CommunityMatcher.matchCommunity(child5),
                                   CommunityMatcher.matchCommunity(child6),
                                   CommunityMatcher.matchCommunity(child7),
                                   CommunityMatcher.matchCommunity(child8),
                                   CommunityMatcher.matchCommunity(child9)
                           )))
                   .andExpect(jsonPath("$._links.self.href",
                                       Matchers.containsString("/api/core/communities/" + parentCommunity.getID())))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.size", is(20)))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.totalElements", is(10)));
    }

    @Test
    public void findOneTestWithEmbedsWithPageSize() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with 10 sub-communities
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child0 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 0")
                                           .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 1")
                                           .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 2")
                                           .build();
        Community child3 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 3")
                                           .build();
        Community child4 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 4")
                                           .build();
        Community child5 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 5")
                                           .build();
        Community child6 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 6")
                                           .build();
        Community child7 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 7")
                                           .build();
        Community child8 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 8")
                                           .build();
        Community child9 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 9")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID())
                                    .param("embed", "subcommunities")
                                    .param("embed.size", "subcommunities=5"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", CommunityMatcher.matchCommunity(parentCommunity)))
                   .andExpect(
                           jsonPath("$._embedded.subcommunities._embedded.subcommunities", Matchers.containsInAnyOrder(
                                   CommunityMatcher.matchCommunity(child0),
                                   CommunityMatcher.matchCommunity(child1),
                                   CommunityMatcher.matchCommunity(child2),
                                   CommunityMatcher.matchCommunity(child3),
                                   CommunityMatcher.matchCommunity(child4)
                           )))
                   .andExpect(jsonPath("$._links.self.href",
                                       Matchers.containsString("/api/core/communities/" + parentCommunity.getID())))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.size", is(5)))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.totalElements", is(10)));
    }

    @Test
    public void findOneTestWithEmbedsWithInvalidPageSize() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with 10 sub-communities
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child0 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 0")
                                           .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 1")
                                           .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 2")
                                           .build();
        Community child3 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 3")
                                           .build();
        Community child4 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 4")
                                           .build();
        Community child5 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 5")
                                           .build();
        Community child6 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 6")
                                           .build();
        Community child7 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 7")
                                           .build();
        Community child8 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 8")
                                           .build();
        Community child9 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 9")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID())
                                    .param("embed", "subcommunities")
                                    .param("embed.size", "subcommunities=invalidPage"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$", CommunityMatcher.matchCommunity(parentCommunity)))
                   .andExpect(
                           jsonPath("$._embedded.subcommunities._embedded.subcommunities", Matchers.containsInAnyOrder(
                                   CommunityMatcher.matchCommunity(child0),
                                   CommunityMatcher.matchCommunity(child1),
                                   CommunityMatcher.matchCommunity(child2),
                                   CommunityMatcher.matchCommunity(child3),
                                   CommunityMatcher.matchCommunity(child4),
                                   CommunityMatcher.matchCommunity(child5),
                                   CommunityMatcher.matchCommunity(child6),
                                   CommunityMatcher.matchCommunity(child7),
                                   CommunityMatcher.matchCommunity(child8),
                                   CommunityMatcher.matchCommunity(child9)
                           )))
                   .andExpect(jsonPath("$._links.self.href",
                                       Matchers.containsString("/api/core/communities/" + parentCommunity.getID())))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.size", is(20)))
                   .andExpect(jsonPath("$._embedded.subcommunities.page.totalElements", is(10)));
    }

    @Test
    public void findAllNoDuplicatesOnMultipleCommunityTitlesTest() throws Exception {
        context.turnOffAuthorisationSystem();
        List<String> titles = Arrays.asList("First title", "Second title", "Third title", "Fourth title");
        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName(titles.get(0))
                .withTitle(titles.get(1))
                .withTitle(titles.get(2))
                .withTitle(titles.get(3))
                .build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities").param("size", "2")
                                                        .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                        CommunityMatcher.matchCommunityEntryMultipleTitles(titles, parentCommunity.getID(),
                                parentCommunity.getHandle()),
                        CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                           child1.getHandle())
                )))
                .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.page.totalPages", is(1)));
    }

    @Test
    public void findAllNoDuplicatesOnMultipleCommunityTitlesPaginationTest() throws Exception {
        context.turnOffAuthorisationSystem();
        List<String> titles = Arrays.asList("First title", "Second title", "Third title", "Fourth title");
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName(titles.get(0))
                                          .withTitle(titles.get(1))
                                          .withTitle(titles.get(2))
                                          .withTitle(titles.get(3))
                                          .build();
        Community childCommunity = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("test")
                                                   .build();
        Community secondParentCommunity = CommunityBuilder.createCommunity(context).withName("testing").build();
        Community thirdParentCommunity = CommunityBuilder.createCommunity(context).withName("testingTitleTwo").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities").param("size", "2")
                                                        .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                       CommunityMatcher.matchCommunityEntryMultipleTitles(titles, parentCommunity.getID(),
                                                                          parentCommunity.getHandle()),
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(childCommunity.getName(),
                                                                          childCommunity.getID(),
                                                                          childCommunity.getHandle())
                       )))
                   .andExpect(jsonPath("$._links.self.href",
                                       Matchers.containsString("/api/core/communities")))
                   .andExpect(jsonPath("$.page", PageMatcher.pageEntryWithTotalPagesAndElements(0, 2,
                                                                                                2, 4)));

        getClient().perform(get("/api/core/communities").param("size", "2").param("page", "1")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(secondParentCommunity.getName(),
                                                                          secondParentCommunity.getID(),
                                                                          secondParentCommunity.getHandle()),
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(thirdParentCommunity.getName(),
                                                                          thirdParentCommunity.getID(),
                                                                          thirdParentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
                   .andExpect(jsonPath("$.page", PageMatcher.pageEntryWithTotalPagesAndElements(1, 2,
                                                                                                2, 4)));
    }


    @Test
    public void findAllNoNameCommunityIsReturned() throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context).withName("test").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                        CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                           parentCommunity.getID(),
                                                                           parentCommunity.getHandle())
                )))
                .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
                .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    public void findAllCommunitiesAreReturnedInCorrectOrder() throws Exception {
        // The hibernate query for finding all communities is "SELECT ... ORDER BY STR(dc_title.value)"
        // So the communities should be returned in alphabetical order

        context.turnOffAuthorisationSystem();

        List<String> orderedTitles = Arrays.asList("Abc", "Bcd", "Cde");

        Community community1 = CommunityBuilder.createCommunity(context)
            .withName(orderedTitles.get(0))
            .build();

        Community community2 = CommunityBuilder.createCommunity(context)
            .withName(orderedTitles.get(1))
            .build();

        Community community3 = CommunityBuilder.createCommunity(context)
            .withName(orderedTitles.get(2))
            .build();

        context.restoreAuthSystemState();

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = getClient().perform(get("/api/core/communities")).andReturn();
        String response = result.getResponse().getContentAsString();
        JSONArray communities = new JSONObject(response).getJSONObject("_embedded").getJSONArray("communities");
        List<String> responseTitles = StreamSupport.stream(communities.spliterator(), false)
                                        .map(JSONObject.class::cast)
                                        .map(x -> x.getString("name"))
                                        .collect(Collectors.toList());

        assertEquals(orderedTitles, responseTitles);
    }

    @Test
    public void findAllPaginationTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities")
                                .param("size", "1")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.not(
                       Matchers.contains(
                           CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                              child1.getHandle())
                       )
                   )))
                   .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.next.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$.page.size", is(1)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   .andExpect(jsonPath("$.page.number", is(0)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)))
        ;

        getClient().perform(get("/api/core/communities")
                                .param("size", "1")
                                .param("page", "1")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                          child1.getHandle())
                   )))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.not(
                       Matchers.contains(
                           CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                              parentCommunity.getID(),
                                                                              parentCommunity.getHandle())
                       )
                   )))
                   .andExpect(jsonPath("$._links.first.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.prev.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=0"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.self.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$._links.last.href", Matchers.allOf(
                           Matchers.containsString("/api/core/communities?"),
                           Matchers.containsString("page=1"), Matchers.containsString("size=1"))))
                   .andExpect(jsonPath("$.page.number", is(1)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   .andExpect(jsonPath("$.page.size", is(1)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)))
        ;
    }

    @Test
    public void findAllUnAuthenticatedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community 2")
                .build();

        resoucePolicyService.removePolicies(context, parentCommunity, Constants.READ);
        resoucePolicyService.removePolicies(context, child1, Constants.READ);
        context.restoreAuthSystemState();

        // anonymous can see only public communities
        getClient().perform(get("/api/core/communities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                            CommunityMatcher.matchCommunity(child2))))
                   .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    public void findAllForbiddenTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                   .withName("Parent Community")
                   .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                   .withName("Sub Community")
                   .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                   .withName("Sub Community 2")
                   .build();

        resoucePolicyService.removePolicies(context, parentCommunity, Constants.READ);
        resoucePolicyService.removePolicies(context, child1, Constants.READ);
        context.restoreAuthSystemState();

        String tokenEperson = getAuthToken(eperson.getEmail(), password);
        getClient(tokenEperson).perform(get("/api/core/communities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                            CommunityMatcher.matchCommunity(child2))))
                   .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    public void findAllGrantAccessAdminsTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson parentAdmin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson1@mail.com")
                .withPassword("qwerty01")
                .build();
        parentCommunity = CommunityBuilder.createCommunity(context)
                   .withName("Parent Community")
                   .withAdminGroup(parentAdmin)
                   .build();

        EPerson child1Admin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson2@mail.com")
                .withPassword("qwerty02")
                .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                   .withName("Sub Community 1")
                   .withAdminGroup(child1Admin)
                   .build();

        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community 2")
                .build();

        resoucePolicyService.removePolicies(context, parentCommunity, Constants.READ);
        resoucePolicyService.removePolicies(context, child1, Constants.READ);
        context.restoreAuthSystemState();

        String tokenParentAdmin = getAuthToken(parentAdmin.getEmail(), "qwerty01");
        getClient(tokenParentAdmin).perform(get("/api/core/communities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                           CommunityMatcher.matchCommunity(parentCommunity),
                           CommunityMatcher.matchCommunity(child1),
                           CommunityMatcher.matchCommunity(child2))))
                   .andExpect(jsonPath("$.page.totalElements", is(3)));

        String tokenChild1Admin = getAuthToken(child1Admin.getEmail(), "qwerty02");
        getClient(tokenChild1Admin).perform(get("/api/core/communities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                           CommunityMatcher.matchCommunity(child1),
                           CommunityMatcher.matchCommunity(child2))))
                   .andExpect(jsonPath("$.page.totalElements", is(2)));
    }

    @Test
    public void findOneTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        // When full projection is requested, response should include expected properties, links, and embeds.
        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", CommunityMatcher.matchNonAdminEmbeds()))
                .andExpect(jsonPath("$", CommunityMatcher.matchCommunityEntry(
                        parentCommunity.getName(), parentCommunity.getID(), parentCommunity.getHandle())));

        // When no projection is requested, response should include expected properties, links, and no embeds.
        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", HalMatcher.matchNoEmbeds()))
                .andExpect(jsonPath("$", CommunityMatcher.matchLinks(parentCommunity.getID())))
                .andExpect(jsonPath("$", CommunityMatcher.matchProperties(
                        parentCommunity.getName(), parentCommunity.getID(), parentCommunity.getHandle())));
    }

    @Test
    public void findOneFullProjectionTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        String adminToken = getAuthToken(admin.getEmail(), password);
        getClient(adminToken).perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                                .param("projection", "full"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", CommunityMatcher.matchCommunityEntryFullProjection(
                       parentCommunity.getName(), parentCommunity.getID(), parentCommunity.getHandle())));

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                                .param("projection", "full"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.not(CommunityMatcher.matchCommunityEntryFullProjection(
                       parentCommunity.getName(), parentCommunity.getID(), parentCommunity.getHandle()))));
    }

    @Test
    public void findOneUnAuthenticatedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Community privateCommunity = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Private Community")
                .build();

        resoucePolicyService.removePolicies(context, privateCommunity, Constants.READ);

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + privateCommunity.getID().toString()))
                   .andExpect(status().isUnauthorized());
    }

    @Test
    public void findOneForbiddenTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Community privateCommunity = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Private Community")
                .build();

        resoucePolicyService.removePolicies(context, privateCommunity, Constants.READ);

        context.restoreAuthSystemState();

        String tokenEperson = getAuthToken(eperson.getEmail(), password);
        getClient(tokenEperson).perform(get("/api/core/communities/" + privateCommunity.getID().toString()))
                   .andExpect(status().isForbidden());
    }

    @Test
    public void findOneGrantAccessAdminsTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .withAdminGroup(eperson)
                .build();

        EPerson privateCommunityAdmin = EPersonBuilder.createEPerson(context)
                .withEmail("comunityAdmin@mail.com")
                .withPassword("qwerty01")
                .build();
        Community privateCommunity = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .withAdminGroup(privateCommunityAdmin)
                .build();

        EPerson privateCommunityAdmin2 = EPersonBuilder.createEPerson(context)
                .withEmail("comunityAdmin2@mail.com")
                .withPassword("qwerty02")
                .build();
        Community privateCommunity2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community 2")
                .withAdminGroup(privateCommunityAdmin2)
                .build();

        resoucePolicyService.removePolicies(context, privateCommunity, Constants.READ);

        context.restoreAuthSystemState();

        String tokenParentComunityAdmin = getAuthToken(eperson.getEmail(), password);
        getClient(tokenParentComunityAdmin).perform(get("/api/core/communities/" + privateCommunity.getID().toString()))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$", Matchers.is(CommunityMatcher.matchCommunity(privateCommunity))));

        String tokenCommunityAdmin = getAuthToken(privateCommunityAdmin.getEmail(), "qwerty01");
        getClient(tokenCommunityAdmin).perform(get("/api/core/communities/" + privateCommunity.getID().toString()))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$", Matchers.is(CommunityMatcher.matchCommunity(privateCommunity))));

        String tokenComunityAdmin2 = getAuthToken(privateCommunityAdmin2.getEmail(), "qwerty02");
        getClient(tokenComunityAdmin2).perform(get("/api/core/communities/"
                                                   + privateCommunity.getID().toString()))
                 .andExpect(status().isForbidden());
    }

    @Test
    public void findOneRelsTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .withLogo("ThisIsSomeDummyText")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.is(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$", Matchers.not(
                       Matchers.is(
                           CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                              child1.getHandle())
                       )
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers
                       .containsString("/api/core/communities/" + parentCommunity.getID().toString())))
                   .andExpect(jsonPath("$._links.logo.href", Matchers
                       .containsString("/api/core/communities/" + parentCommunity.getID().toString() + "/logo")))
                   .andExpect(jsonPath("$._links.collections.href", Matchers
                       .containsString("/api/core/communities/" + parentCommunity.getID().toString() + "/collections")))
                   .andExpect(jsonPath("$._links.subcommunities.href", Matchers
                        .containsString("/api/core/communities/" + parentCommunity.getID().toString() +
                                "/subcommunities")))
        ;

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString() + "/logo"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType));

        getClient().perform(get("/api/core/communities/" + child1.getID().toString() + "/logo"))
                   .andExpect(status().isNoContent());

        //Main community has no collections
        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString() + "/collections"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.page.totalElements", is(0)));

        getClient().perform(get("/api/core/communities/" + child1.getID().toString() + "/collections"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType));

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString() + "/subcommunities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType));

        //child1 subcommunity has no subcommunities, therefore contentType is not set
        getClient().perform(get("/api/core/communities/" + child1.getID().toString() + "/subcommunities"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.page.totalElements", is(0)));
    }


    @Test
    public void findAllSearchTop() throws Exception {

        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .withLogo("ThisIsSomeDummyText")
                                          .build();

        Community parentCommunity2 = CommunityBuilder.createCommunity(context)
                                                     .withName("Parent Community 2")
                                                     .withLogo("SomeTest")
                                                     .build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();

        Community child12 = CommunityBuilder.createSubCommunity(context, child1)
                                            .withName("Sub Sub Community")
                                            .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/search/top")
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle()),
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity2.getName(),
                                                                          parentCommunity2.getID(),
                                                                          parentCommunity2.getHandle())
                   )))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.not(Matchers.containsInAnyOrder(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                          child1.getHandle()),
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child12.getName(), child12.getID(),
                                                                          child12.getHandle())
                   ))))
                   .andExpect(
                       jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities/search/top")))
                   .andExpect(jsonPath("$.page.size", is(20)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)))
        ;
    }

    @Test
    public void findAllSubCommunities() throws Exception {

        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                          .withName("Parent Community")
                          .withLogo("ThisIsSomeDummyText")
                          .build();

        Community parentCommunity2 = CommunityBuilder.createCommunity(context)
                                     .withName("Parent Community 2")
                                     .withLogo("SomeTest")
                                     .build();

        Community parentCommunityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                          .withName("Sub Community")
                                          .build();

        Community parentCommunityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                          .withName("Sub Community2")
                                          .build();

        Community parentCommunityChild2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunityChild2)
                                                .withName("Sub Sub Community")
                                                .build();

        Community parentCommunity2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunity2)
                                           .withName("Sub2 Community")
                                           .build();

        Collection col1 = CollectionBuilder.createCollection(context, parentCommunityChild1)
                                           .withName("Collection 1")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString() + "/subcommunities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   //Checking that these communities are present
                   .andExpect(jsonPath("$._embedded.subcommunities", Matchers.containsInAnyOrder(
                        CommunityMatcher.matchCommunity(parentCommunityChild1),
                        CommunityMatcher.matchCommunity(parentCommunityChild2)
                   )))
                   //Checking that these communities are not present
                   .andExpect(jsonPath("$._embedded.subcommunities", Matchers.not(Matchers.anyOf(
                        CommunityMatcher.matchCommunity(parentCommunity),
                        CommunityMatcher.matchCommunity(parentCommunity2),
                        CommunityMatcher.matchCommunity(parentCommunity2Child1),
                        CommunityMatcher.matchCommunity(parentCommunityChild2Child1)
                   ))))
                   .andExpect(jsonPath("$._links.self.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID().toString())))
                   .andExpect(jsonPath("$.page.size", is(20)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)));

       getClient().perform(get("/api/core/communities/" + parentCommunityChild2.getID().toString() + "/subcommunities"))
                  .andExpect(status().isOk())
                  .andExpect(content().contentType(contentType))
                  //Checking that these communities are present
                  .andExpect(jsonPath("$._embedded.subcommunities", Matchers.contains(
                            CommunityMatcher.matchCommunity(parentCommunityChild2Child1)
                  )))
                  //Checking that these communities are not present
                  .andExpect(jsonPath("$._embedded.subcommunities", Matchers.not(Matchers.anyOf(
                            CommunityMatcher.matchCommunity(parentCommunity),
                            CommunityMatcher.matchCommunity(parentCommunity2),
                            CommunityMatcher.matchCommunity(parentCommunity2Child1),
                            CommunityMatcher.matchCommunity(parentCommunityChild2Child1),
                            CommunityMatcher.matchCommunity(parentCommunityChild1)
                  ))))
                  .andExpect(jsonPath("$._links.self.href",
                          Matchers.containsString("/api/core/communities/" + parentCommunityChild2.getID().toString())))
                  .andExpect(jsonPath("$.page.size", is(20)))
                  .andExpect(jsonPath("$.page.totalElements", is(1)));

        getClient().perform(get("/api/core/communities/"
                                + parentCommunityChild2Child1.getID().toString() + "/subcommunities"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._links.self.href",
                    Matchers.containsString("/api/core/communities/" + parentCommunityChild2Child1.getID().toString())))
                   .andExpect(jsonPath("$.page.size", is(20)))
                   .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    @Test
    public void findAllSubCommunitiesUnAuthenticatedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                          .withName("Parent Community")
                          .withLogo("ThisIsSomeDummyText")
                          .build();

        Community communityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .build();

        Community communityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community2")
                .build();

        Community communityChild2Child1 = CommunityBuilder.createSubCommunity(context, communityChild2)
                .withName("Sub Community2")
                .build();

        resoucePolicyService.removePolicies(context, communityChild2, Constants.READ);
        context.restoreAuthSystemState();

        // anonymous can NOT see the private communities
        getClient().perform(get("/api/core/communities/" + communityChild2.getID().toString() + "/subcommunities"))
                .andExpect(status().isUnauthorized());

        // anonymous can see only public communities
        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString() + "/subcommunities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.subcommunities", Matchers.contains(
                        CommunityMatcher.matchCommunity(communityChild1))))
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        // admin can see all communities
        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/communities/"
                + parentCommunity.getID().toString() + "/subcommunities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.subcommunities", Matchers.containsInAnyOrder(
                        CommunityMatcher.matchCommunity(communityChild1),
                        CommunityMatcher.matchCommunity(communityChild2))))
                .andExpect(jsonPath("$.page.totalElements", is(2)));
    }

    @Test
    public void findAllSubCommunitiesForbiddenTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                          .withName("Parent Community")
                          .withLogo("ThisIsSomeDummyText")
                          .build();

        Community communityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .build();

        Community communityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community2")
                .build();

        Community communityChild2Child1 = CommunityBuilder.createSubCommunity(context, communityChild2)
                .withName("Sub Community2")
                .build();

        resoucePolicyService.removePolicies(context, communityChild2, Constants.READ);
        context.restoreAuthSystemState();

        String tokenEperson = getAuthToken(eperson.getEmail(), password);
        getClient(tokenEperson).perform(get("/api/core/communities/"
                + parentCommunity.getID().toString() + "/subcommunities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.subcommunities", Matchers.contains(
                        CommunityMatcher.matchCommunity(communityChild1))))
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        getClient(tokenEperson).perform(get("/api/core/communities/"
                + communityChild2.getID().toString() + "/subcommunities"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllSubCommunitiesGrantAccessAdminsTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson parentComAdmin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson1@mail.com")
                .withPassword("qwerty01")
                .build();
        parentCommunity = CommunityBuilder.createCommunity(context)
                          .withName("Parent Community")
                          .withLogo("ThisIsSomeDummyText")
                          .withAdminGroup(parentComAdmin)
                          .build();

        EPerson child1Admin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson2@mail.com")
                .withPassword("qwerty02")
                .build();
        Community communityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .withAdminGroup(child1Admin)
                .build();

        EPerson child2Admin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson3@mail.com")
                .withPassword("qwerty03")
                .build();
        Community communityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community2")
                .withAdminGroup(child2Admin)
                .build();

        Community ommunityChild1Child1 = CommunityBuilder.createSubCommunity(context, communityChild1)
                .withName("Sub1 Community 1")
                .build();

        Community сommunityChild2Child1 = CommunityBuilder.createSubCommunity(context, communityChild2)
                .withName("Sub2 Community 1")
                .build();

        resoucePolicyService.removePolicies(context, parentCommunity, Constants.READ);
        resoucePolicyService.removePolicies(context, communityChild1, Constants.READ);
        resoucePolicyService.removePolicies(context, communityChild2, Constants.READ);

        context.restoreAuthSystemState();

        String tokenParentAdmin = getAuthToken(parentComAdmin.getEmail(), "qwerty01");
        getClient(tokenParentAdmin).perform(get("/api/core/communities/"
                + parentCommunity.getID().toString() + "/subcommunities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.subcommunities", Matchers.containsInAnyOrder(
                        CommunityMatcher.matchCommunity(communityChild1),
                        CommunityMatcher.matchCommunity(communityChild2))))
                .andExpect(jsonPath("$.page.totalElements", is(2)));

        String tokenChild1Admin = getAuthToken(child1Admin.getEmail(), "qwerty02");
        getClient(tokenChild1Admin).perform(get("/api/core/communities/"
                + parentCommunity.getID().toString() + "/subcommunities"))
                .andExpect(status().isForbidden());

        String tokenChild2Admin = getAuthToken(child2Admin.getEmail(), "qwerty03");
        getClient(tokenChild2Admin).perform(get("/api/core/communities/"
                + communityChild1.getID().toString() + "/subcommunities"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllCollectionsUnAuthenticatedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .withLogo("ThisIsSomeDummyText")
                .build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .build();

        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .build();

        Collection child1Col1 = CollectionBuilder.createCollection(context, child1)
                .withName("Collection 1 child 1")
                .build();
        Collection child1Col2 = CollectionBuilder.createCollection(context, child1)
                .withName("Collection 2 child 1")
                .build();

        Collection child2Col1 = CollectionBuilder.createCollection(context, child2)
                .withName("Collection 1 child 2")
                .build();

        resoucePolicyService.removePolicies(context, child1Col2, Constants.READ);
        resoucePolicyService.removePolicies(context, child2, Constants.READ);
        context.restoreAuthSystemState();

        // anonymous can see only public communities
        getClient().perform(get("/api/core/communities/" + child1.getID().toString() + "/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.collections", Matchers.contains(CollectionMatcher
                     .matchCollection(child1Col1))))
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        // anonymous can NOT see the private communities
        getClient().perform(get("/api/core/communities/" + child2.getID().toString() + "/collections"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllCollectionsForbiddenTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .withLogo("ThisIsSomeDummyText")
                .build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community 1")
                .build();


        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community 2")
                .build();

        Collection child1Col1 = CollectionBuilder.createCollection(context, child1)
                .withName("Collection 1 child 1")
                .build();
        Collection child1Col2 = CollectionBuilder.createCollection(context, child1)
                .withName("Collection 2 child 1")
                .build();

        Collection child2Col1 = CollectionBuilder.createCollection(context, child2)
                .withName("Collection 1 child 2")
                .build();

        resoucePolicyService.removePolicies(context, child1Col2, Constants.READ);
        resoucePolicyService.removePolicies(context, child2, Constants.READ);
        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/communities/" + child1.getID().toString() + "/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.collections", Matchers.containsInAnyOrder(
                        CollectionMatcher.matchCollection(child1Col1),
                        CollectionMatcher.matchCollection(child1Col2))))
                .andExpect(jsonPath("$.page.totalElements", is(2)));

        getClient(tokenAdmin).perform(get("/api/core/communities/" + child2.getID().toString() + "/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.collections", Matchers.contains(
                CollectionMatcher.matchCollection(child2Col1))))
        .andExpect(jsonPath("$.page.totalElements", is(1)));

        String tokenEperson = getAuthToken(eperson.getEmail(), password);
        getClient(tokenEperson).perform(get("/api/core/communities/" + child2.getID().toString() + "/collections"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllCollectionsGrantAccessAdminsTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson parentAdmin = EPersonBuilder.createEPerson(context)
                .withEmail("eperson1@mail.com")
                .withPassword("qwerty01")
                .build();
        parentCommunity = CommunityBuilder.createCommunity(context)
                .withName("Parent Community")
                .withLogo("ThisIsSomeDummyText")
                .withAdminGroup(parentAdmin)
                .build();

        EPerson child1Admin = EPersonBuilder.createEPerson(context)
                .withEmail("child1admin@mail.com")
                .withPassword("qwerty02")
                .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .withAdminGroup(child1Admin)
                .build();

        EPerson child2Admin = EPersonBuilder.createEPerson(context)
                .withEmail("child2admin@mail.com")
                .withPassword("qwerty03")
                .build();
        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                .withName("Sub Community")
                .withAdminGroup(child2Admin)
                .build();

        Collection child1Col1 = CollectionBuilder.createCollection(context, child1)
                .withName("Child 1 Collection 1")
                .build();
        Collection child1Col2 = CollectionBuilder.createCollection(context, child1)
                .withName("Child 1 Collection 2")
                .build();

        Collection child2Col1 = CollectionBuilder.createCollection(context, child2)
                .withName("Child 2 Collection 1")
                .build();

        resoucePolicyService.removePolicies(context, child1Col2, Constants.READ);
        resoucePolicyService.removePolicies(context, child2, Constants.READ);
        context.restoreAuthSystemState();

        String tokenParentAdmin = getAuthToken(parentAdmin.getEmail(), "qwerty01");
        getClient(tokenParentAdmin).perform(get("/api/core/communities/" + child1.getID().toString() + "/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.collections", Matchers.containsInAnyOrder(
                        CollectionMatcher.matchCollection(child1Col1),
                        CollectionMatcher.matchCollection(child1Col2))))
                .andExpect(jsonPath("$.page.totalElements", is(2)));


        getClient(tokenParentAdmin).perform(get("/api/core/communities/" + child2.getID().toString() + "/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.collections", Matchers.contains(
                        CollectionMatcher.matchCollection(child2Col1))))
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        String tokenChild2Admin = getAuthToken(child2Admin.getEmail(), "qwerty03");
        getClient(tokenChild2Admin).perform(get("/api/core/communities/" + child1.getID().toString() + "/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.collections", Matchers.contains(
                        CollectionMatcher.matchCollection(child1Col1))))
                .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    @Test
    public void findAllSubCommunitiesWithUnexistentUUID() throws Exception {
        getClient().perform(get("/api/core/communities/" + UUID.randomUUID().toString() + "/subcommunities"))
                   .andExpect(status().isNotFound());
    }

    @Test
    public void findOneTestWrongUUID() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    public void updateTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();
        Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.is(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$", Matchers.not(
                       Matchers.is(
                           CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                              child1.getHandle())
                       )
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
        ;

        String token = getAuthToken(admin.getEmail(), password);

        context.turnOffAuthorisationSystem();

        ObjectMapper mapper = new ObjectMapper();

        CommunityRest communityRest = communityConverter.convert(parentCommunity, Projection.DEFAULT);

        communityRest.setMetadata(new MetadataRest()
                .put("dc.title", new MetadataValueRest("Electronic theses and dissertations")));

        context.restoreAuthSystemState();

        getClient(token).perform(put("/api/core/communities/" + parentCommunity.getID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsBytes(communityRest)))
                   .andExpect(status().isOk())
        ;

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.is(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds("Electronic theses and dissertations",
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
        ;
    }

    @Test
    public void deleteTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .withLogo("ThisIsSomeDummyText")
                                          .build();

        Community parentCommunity2 = CommunityBuilder.createCommunity(context)
                                                     .withName("Parent Community 2")
                                                     .withLogo("SomeTest")
                                                     .build();

        Community parentCommunityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                                          .withName("Sub Community")
                                                          .build();

        Community parentCommunityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                                          .withName("Sub Community2")
                                                          .build();

        Community parentCommunityChild2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunityChild2)
                                                                .withName("Sub Sub Community")
                                                                .build();


        Community parentCommunity2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunity2)
                                                           .withName("Sub2 Community")
                                                           .build();

        Collection col1 = CollectionBuilder.createCollection(context, parentCommunityChild1)
                                           .withName("Collection 1")
                                           .build();

        String token = getAuthToken(admin.getEmail(), password);

        context.restoreAuthSystemState();

        getClient(token).perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                           .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(contentType))
                        .andExpect(jsonPath("$", Matchers.is(
                            CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                               parentCommunity.getID(),
                                                                               parentCommunity.getHandle())
                        )))
                        .andExpect(jsonPath("$._links.self.href",
                                            Matchers.containsString("/api/core/communities")));
        getClient(token).perform(delete("/api/core/communities/" + parentCommunity.getID().toString()))
                        .andExpect(status().isNoContent())
        ;
        getClient(token).perform(get("/api/core/communities/" + parentCommunity.getID().toString()))
                        .andExpect(status().isNotFound())
        ;

        getClient(token).perform(get("/api/core/communities/" + parentCommunityChild1.getID().toString()))
                        .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void deleteTestUnAuthorized() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .withLogo("ThisIsSomeDummyText")
                                          .build();

        Community parentCommunity2 = CommunityBuilder.createCommunity(context)
                                                     .withName("Parent Community 2")
                                                     .withLogo("SomeTest")
                                                     .build();

        Community parentCommunityChild1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                                          .withName("Sub Community")
                                                          .build();

        Community parentCommunityChild2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                                          .withName("Sub Community2")
                                                          .build();

        Community parentCommunityChild2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunityChild2)
                                                                .withName("Sub Sub Community")
                                                                .build();


        Community parentCommunity2Child1 = CommunityBuilder.createSubCommunity(context, parentCommunity2)
                                                           .withName("Sub2 Community")
                                                           .build();

        Collection col1 = CollectionBuilder.createCollection(context, parentCommunityChild1)
                                           .withName("Collection 1")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                           .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(contentType))
                        .andExpect(jsonPath("$", Matchers.is(
                            CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                               parentCommunity.getID(),
                                                                               parentCommunity.getHandle())
                        )))
                        .andExpect(jsonPath("$._links.self.href",
                                            Matchers.containsString("/api/core/communities")));
        getClient().perform(delete("/api/core/communities/" + parentCommunity.getID().toString()))
                        .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    public void deleteCommunityEpersonWithDeleteRightsTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

        context.setCurrentUser(eperson);
        authorizeService.addPolicy(context, parentCommunity, Constants.DELETE, eperson);

        String token = getAuthToken(eperson.getEmail(), password);

        context.restoreAuthSystemState();

        getClient(token).perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                           .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(contentType))
                        .andExpect(jsonPath("$", Matchers.is(
                            CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                               parentCommunity.getID(),
                                                                               parentCommunity.getHandle())
                        )))
                        .andExpect(jsonPath("$._links.self.href",
                                            Matchers.containsString("/api/core/communities")));
        getClient(token).perform(delete("/api/core/communities/" + parentCommunity.getID().toString()))
                        .andExpect(status().isNoContent())
        ;
        getClient(token).perform(get("/api/core/communities/" + parentCommunity.getID().toString()))
                        .andExpect(status().isNotFound())
        ;

        authorizeService.removePoliciesActionFilter(context, eperson, Constants.DELETE);
    }

    @Test
    public void updateCommunityEpersonWithWriteRightsTest() throws Exception {
        //We turn off the authorization system in order to create the structure as defined below
        context.turnOffAuthorisationSystem();

        //** GIVEN **
        //1. A community-collection structure with one parent community with sub-community and one collection.
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();
        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community")
                                           .build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.is(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds(parentCommunity.getName(),
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$", Matchers.not(
                       Matchers.is(
                           CommunityMatcher.matchCommunityEntryNonAdminEmbeds(child1.getName(), child1.getID(),
                                                                              child1.getHandle())
                       )
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
        ;

        context.turnOffAuthorisationSystem();

        ObjectMapper mapper = new ObjectMapper();

        CommunityRest communityRest = communityConverter.convert(parentCommunity, Projection.DEFAULT);

        communityRest.setMetadata(new MetadataRest()
                .put("dc.title", new MetadataValueRest("Electronic theses and dissertations")));

        context.setCurrentUser(eperson);
        authorizeService.addPolicy(context, parentCommunity, Constants.WRITE, eperson);

        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        getClient(token).perform(put("/api/core/communities/" + parentCommunity.getID().toString())
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(mapper.writeValueAsBytes(communityRest)))
                        .andExpect(status().isOk())
        ;

        getClient().perform(get("/api/core/communities/" + parentCommunity.getID().toString())
                      .param("embed", CommunityMatcher.getNonAdminEmbeds()))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$", Matchers.is(
                       CommunityMatcher.matchCommunityEntryNonAdminEmbeds("Electronic theses and dissertations",
                                                                          parentCommunity.getID(),
                                                                          parentCommunity.getHandle())
                   )))
                   .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/core/communities")))
        ;

        authorizeService.removePoliciesActionFilter(context, eperson, Constants.DELETE);

    }

    @Test
    public void patchCommunityMetadataAuthorized() throws Exception {
        runPatchMetadataTests(admin, 200);
    }

    @Test
    public void patchCommunityMetadataUnauthorized() throws Exception {
        runPatchMetadataTests(eperson, 403);
    }

    private void runPatchMetadataTests(EPerson asUser, int expectedStatus) throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context).withName("Community").build();
        context.restoreAuthSystemState();
        String token = getAuthToken(asUser.getEmail(), password);

        new MetadataPatchSuite().runWith(getClient(token), "/api/core/communities/"
                + parentCommunity.getID(), expectedStatus);
    }

    @Test
    public void createTestInvalidParentCommunityBadRequest() throws Exception {
        context.turnOffAuthorisationSystem();

        ObjectMapper mapper = new ObjectMapper();
        CommunityRest comm = new CommunityRest();
        // We send a name but the created community should set this to the title
        comm.setName("Test Top-Level Community");

        MetadataRest metadataRest = new MetadataRest();

        MetadataValueRest description = new MetadataValueRest();
        description.setValue("<p>Some cool HTML code here</p>");
        metadataRest.put("dc.description", description);

        MetadataValueRest abs = new MetadataValueRest();
        abs.setValue("Sample top-level community created via the REST API");
        metadataRest.put("dc.description.abstract", abs);

        MetadataValueRest contents = new MetadataValueRest();
        contents.setValue("<p>HTML News</p>");
        metadataRest.put("dc.description.tableofcontents", contents);

        MetadataValueRest copyright = new MetadataValueRest();
        copyright.setValue("Custom Copyright Text");
        metadataRest.put("dc.rights", copyright);

        MetadataValueRest title = new MetadataValueRest();
        title.setValue("Title Text");
        metadataRest.put("dc.title", title);

        comm.setMetadata(metadataRest);

        context.restoreAuthSystemState();

        String authToken = getAuthToken(admin.getEmail(), password);
        getClient(authToken).perform(post("/api/core/communities")
                                         .param("parent", "123")
                                         .content(mapper.writeValueAsBytes(comm))
                                         .contentType(contentType))
                            .andExpect(status().isBadRequest());
    }

    public void setUpAuthorizedSearch() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        topLevelCommunityAAdmin = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Jhon", "Brown")
            .withEmail("topLevelCommunityAAdmin@my.edu")
            .withPassword(password)
            .build();
        topLevelCommunityA = CommunityBuilder.createCommunity(context)
            .withName("The name of this community is topLevelCommunityA")
            .withAdminGroup(topLevelCommunityAAdmin)
            .build();

        subCommunityAAdmin = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Jhon", "Brown")
            .withEmail("subCommunityAAdmin@my.edu")
            .withPassword(password)
            .build();
        subCommunityA = CommunityBuilder.createCommunity(context)
            .withName("The name of this sub-community is subCommunityA")
            .withAdminGroup(subCommunityAAdmin)
            .addParentCommunity(context, topLevelCommunityA)
            .build();

        submitter = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Jhon", "Brown")
            .withEmail("submitter@my.edu")
            .withPassword(password)
            .build();
        collectionAdmin = EPersonBuilder.createEPerson(context)
            .withNameInMetadata("Jhon", "Brown")
            .withEmail("collectionAdmin@my.edu")
            .withPassword(password)
            .build();
        collectionA = CollectionBuilder.createCollection(context, subCommunityA)
            .withName("The name of this collection is collectionA")
            .withAdminGroup(collectionAdmin)
            .withSubmitterGroup(submitter)
            .build();

        context.restoreAuthSystemState();

        configurationService.setProperty(
            "org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", "true");
    }

    @Test
    public void testAdminAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .withAdminGroup(admin)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(admin.getEmail(), password);

        // Verify the site admin gets all communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(topLevelCommunityA.getName(), topLevelCommunityA.getID(),
                    topLevelCommunityA.getHandle()),
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle()),
                CommunityMatcher.matchProperties(communityC.getName(), communityC.getID(), communityC.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));
    }

    @Test
    public void testCommunityAdminAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .withAdminGroup(topLevelCommunityAAdmin)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is named topLevelCommunityC")
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(topLevelCommunityAAdmin.getEmail(), password);

        // Verify the community admin gets all the communities he's admin for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(topLevelCommunityA.getName(), topLevelCommunityA.getID(),
                    topLevelCommunityA.getHandle()),
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubCommunityAdminAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        /**
         * The Community/Collection structure for this test:
         *
         * topLevelCommunityA
         * ├── subCommunityA
         * |   └── collectionA
         * ├── communityB
         * └── communityC
         */
        context.turnOffAuthorisationSystem();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .withAdminGroup(subCommunityAAdmin)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(subCommunityAAdmin.getEmail(), password);

        // Verify the community admin gets all the communities he's admin for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testCollectionAdminAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(collectionAdmin.getEmail(), password);

        // Verify the collection admin doesn't have any matches for communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubmitterAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(submitter.getEmail(), password);

        // Verify the submitter doesn't have any matches for communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubGroupOfAdminGroupAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        GroupBuilder.createGroup(context)
            .withName("adminSubGroup")
            .withParent(groupService.findByName(context, Group.ADMIN))
            .addMember(eperson)
            .build();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        // Verify the site admins' subgroups members get all communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(topLevelCommunityA.getName(), topLevelCommunityA.getID(),
                    topLevelCommunityA.getHandle()),
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle()),
                CommunityMatcher.matchProperties(communityC.getName(), communityC.getID(), communityC.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));
    }

    @Test
    public void testSubGroupOfCommunityAdminGroupAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        GroupBuilder.createGroup(context)
            .withName("communityAdminSubGroup")
            .withParent(groupService.findByName(context, "COMMUNITY_" + topLevelCommunityA.getID() + "_ADMIN"))
            .addMember(eperson)
            .build();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .build();
        ResourcePolicyBuilder.createResourcePolicy(context)
            .withDspaceObject(communityB)
            .withAction(Constants.ADMIN)
            .withGroup(groupService.findByName(context, "COMMUNITY_" + topLevelCommunityA.getID() + "_ADMIN"))
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        // Verify the community admins' subgroup users get all the communities he's admin for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(topLevelCommunityA.getName(), topLevelCommunityA.getID(),
                    topLevelCommunityA.getHandle()),
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubGroupOfSubCommunityAdminGroupAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        GroupBuilder.createGroup(context)
            .withName("communityAdminSubGroup")
            .withParent(groupService.findByName(context, "COMMUNITY_" + subCommunityA.getID() + "_ADMIN"))
            .addMember(eperson)
            .build();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        ResourcePolicyBuilder.createResourcePolicy(context)
            .withDspaceObject(communityB)
            .withAction(Constants.ADMIN)
            .withGroup(groupService.findByName(context, "COMMUNITY_" + subCommunityA.getID() + "_ADMIN"))
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        // Verify the sub-community admins' subgroup users get all the communities he's admin for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(subCommunityA.getName(), subCommunityA.getID(),
                    subCommunityA.getHandle()),
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities", Matchers.containsInAnyOrder(
                CommunityMatcher.matchProperties(communityB.getName(), communityB.getID(), communityB.getHandle())
            )));

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubGroupOfCollectionAdminGroupAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        GroupBuilder.createGroup(context)
            .withName("collectionAdminSubGroup")
            .withParent(groupService.findByName(context, "COLLECTION_" + collectionA.getID() + "_ADMIN"))
            .addMember(eperson)
            .build();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        Collection collectionB = CollectionBuilder.createCollection(context, communityB)
            .withName("collectionB")
            .build();
        ResourcePolicyBuilder.createResourcePolicy(context)
            .withDspaceObject(collectionB)
            .withAction(Constants.ADMIN)
            .withGroup(groupService.findByName(context, "COLLECTION_" + collectionA.getID() + "_ADMIN"))
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        // Verify the collection admins' subgroup members don't have any matches for communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testSubGroupOfSubmitterGroupAuthorizedSearch() throws Exception {
        setUpAuthorizedSearch();

        context.turnOffAuthorisationSystem();
        GroupBuilder.createGroup(context)
            .withName("collectionAdminSubGroup")
            .withParent(groupService.findByName(context, "COLLECTION_" + collectionA.getID() + "_SUBMIT"))
            .addMember(eperson)
            .build();
        communityB = CommunityBuilder.createCommunity(context)
            .withName("topLevelCommunityB is a very original name")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        Collection collectionB = CollectionBuilder.createCollection(context, communityB)
            .withName("collectionB")
            .build();
        ResourcePolicyBuilder.createResourcePolicy(context)
            .withDspaceObject(collectionB)
            .withAction(Constants.ADD)
            .withGroup(groupService.findByName(context, "COLLECTION_" + collectionA.getID() + "_SUBMIT"))
            .build();
        communityC = CommunityBuilder.createCommunity(context)
            .withName("the last community is topLevelCommunityC")
            .addParentCommunity(context, topLevelCommunityA)
            .build();
        context.restoreAuthSystemState();

        String token = getAuthToken(eperson.getEmail(), password);

        // Verify an ePerson in a subgroup of submitter group doesn't have any matches for communities
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify the search only shows dso's which according to the query
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityB.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());

        // Verify that a query doesn't show dso's which the user doesn't have rights for
        getClient(token).perform(get("/api/core/communities/search/findAdminAuthorized")
            .param("query", communityC.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.communities").doesNotExist());
    }

    @Test
    public void testAdminAuthorizedSearchUnauthenticated() throws Exception {
        // Verify a non-authenticated user can't use this function
        getClient().perform(get("/api/core/communities/search/findAdminAuthorized"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findAllSearchTopEmbeddedPaginationTest() throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .withLogo("ThisIsSomeDummyText")
                                          .build();

        Collection col = CollectionBuilder.createCollection(context, parentCommunity)
                                          .withName("Collection 1").build();

        Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Collection 2").build();

        CommunityBuilder.createCommunity(context)
                        .withName("Parent Community 2")
                        .withLogo("SomeTest").build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community").build();

        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity)
                                           .withName("Sub Community 2").build();

        context.restoreAuthSystemState();

        getClient().perform(get("/api/core/communities/search/top")
                   .param("size", "1")
                   .param("embed", "subcommunities")
                   .param("embed", "collections")
                   .param("embed.size", "subcommunities=1")
                   .param("embed.size", "collections=1"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                                       CommunityMatcher.matchCommunity(parentCommunity))))
                    // Verify subcommunities
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._embedded.subcommunities",
                              Matchers.contains(CommunityMatcher.matchCommunity(child1))))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._links.self.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/subcommunities?size=1")))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._links.next.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/subcommunities?page=1&size=1")))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._links.last.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/subcommunities?page=1&size=1")))
                    // Verify collections
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._embedded.collections",
                              Matchers.contains(CollectionMatcher.matchCollection(col))))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._links.self.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/collections?size=1")))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._links.next.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/collections?page=1&size=1")))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._links.last.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/collections?page=1&size=1")))

                   .andExpect(jsonPath("$._links.self.href",
                              Matchers.containsString("/api/core/communities/search/top?size=1")))
                   .andExpect(jsonPath("$._links.first.href",
                              Matchers.containsString("/api/core/communities/search/top?page=0&size=1")))
                   .andExpect(jsonPath("$._links.next.href",
                              Matchers.containsString("/api/core/communities/search/top?page=1&size=1")))
                   .andExpect(jsonPath("$._links.last.href",
                              Matchers.containsString("/api/core/communities/search/top?page=1&size=1")))
                   .andExpect(jsonPath("$.page.size", is(1)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)));

        getClient().perform(get("/api/core/communities/search/top")
                   .param("size", "1")
                   .param("embed", "subcommunities")
                   .param("embed", "collections")
                   .param("embed.size", "subcommunities=2")
                   .param("embed.size", "collections=2"))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(contentType))
                   .andExpect(jsonPath("$._embedded.communities", Matchers.contains(
                                       CommunityMatcher.matchCommunity(parentCommunity))))
                    // Verify subcommunities
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._embedded.subcommunities",
                              Matchers.containsInAnyOrder(CommunityMatcher.matchCommunity(child1),
                                                          CommunityMatcher.matchCommunity(child2)
                                                          )))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.subcommunities._links.self.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/subcommunities?size=2")))
                    // Verify collections
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._embedded.collections",
                              Matchers.containsInAnyOrder(CollectionMatcher.matchCollection(col),
                                                          CollectionMatcher.matchCollection(col2)
                                                          )))
                   .andExpect(jsonPath("$._embedded.communities[0]._embedded.collections._links.self.href",
                              Matchers.containsString("/api/core/communities/" + parentCommunity.getID()
                                                    + "/collections?size=2")))

                   .andExpect(jsonPath("$._links.self.href",
                              Matchers.containsString("/api/core/communities/search/top?size=1")))
                   .andExpect(jsonPath("$._links.first.href",
                              Matchers.containsString("/api/core/communities/search/top?page=0&size=1")))
                   .andExpect(jsonPath("$._links.next.href",
                              Matchers.containsString("/api/core/communities/search/top?page=1&size=1")))
                   .andExpect(jsonPath("$._links.last.href",
                              Matchers.containsString("/api/core/communities/search/top?page=1&size=1")))
                   .andExpect(jsonPath("$.page.size", is(1)))
                   .andExpect(jsonPath("$.page.totalPages", is(2)))
                   .andExpect(jsonPath("$.page.totalElements", is(2)));
    }


    @Test
    public void cloneCommunityTest() throws Exception {
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

        Collection col = createCollection(context, parentCommunity)
            .withName("Collection of parent Community")
            .withSubmitterGroup(readGroup)
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
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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

            Community newCommunity = communityService.find(context, idRef.get());
            assertEquals("My new Community", newCommunity.getName());
            assertNotEquals(parentCommunity.getID(), newCommunity.getID());

            String admin_Group = "project_" + newCommunity.getID().toString() + "_admin_group";
            String write_Group = "project_" + newCommunity.getID().toString() + "_write_group";
            String read_Group = "project_" + newCommunity.getID().toString() + "_read_group";

            Group groupAdmin = groupService.findByName(context, admin_Group);
            Group groupWrite = groupService.findByName(context, write_Group);
            Group groupRead = groupService.findByName(context, read_Group);

            assertEquals(groupAdmin.getName(), admin_Group);
            assertEquals(groupWrite.getName(), write_Group);
            assertEquals(groupRead.getName(), read_Group);

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
    public void cloneCommunityWithParentTest() throws Exception {
        context.turnOffAuthorisationSystem();
        Community cloneTarget = CommunityBuilder.createCommunity(context)
                                                .withName("Community to hold cloned communities")
                                                .build();
        Community parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();

        Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community 1")
                                           .build();

        Community child2 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community 2")
                                           .build();

        Collection col = CollectionBuilder.createCollection(context, parentCommunity)
                                                       .withName("Collection of parent Community")
                                                       .build();
        Collection child1Col1 = CollectionBuilder.createCollection(context, child1)
                                                 .withName("Child 1 Collection 1")
                                                 .build();
        Collection child2Col1 = CollectionBuilder.createCollection(context, child2)
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
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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
    public void cloneCommunityWrongUUIDTest() throws Exception {

        String tokenAdmin = getAuthToken(admin.getEmail(), password);

        getClient(tokenAdmin).perform(post("/api/core/communities").param("projection", "full")
                             .contentType(MediaType.parseMediaType(
                                org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE))
                             .content("https://localhost:8080/server//api/core/communities/" + UUID.randomUUID()))
                             .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test99() throws Exception {
        context.turnOffAuthorisationSystem();
        Community cloneTarget = CommunityBuilder.createCommunity(context)
                                                .withName("Community to hold cloned communities").build();

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

        communityService.addMetadata(context, parentCommunity, ProjectConstants.MD_PROJECT_ENTITY.SCHEMA,
                ProjectConstants.MD_PROJECT_ENTITY.ELEMENT, ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER,
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
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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
        return (author1Found && author2Found && author3Found);
    }

    @Test
    public void cloneCommunityUnauthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();

        context.restoreAuthSystemState();

        getClient().perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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

        communityService.addMetadata(context, parentCommunity, ProjectConstants.MD_PROJECT_ENTITY.SCHEMA,
                ProjectConstants.MD_PROJECT_ENTITY.ELEMENT, ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER,
                                     null, placeholder.toString());

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        AtomicReference<UUID> idRef = new AtomicReference<>();

        try {
            getClient(tokenAdmin).perform(post("/api/core/communities")
                     .param("projection", "full")
                     .param("name", "My new Community")
                     .param("parent", cloneTarget.getID().toString())
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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
            assertTrue(containeMetadata(itemService, item, "dc", "title", null, "My new Community"));
            assertTrue(containeMetadata(communityService, subCommunityOfCloneTarget,
                    ProjectConstants.MD_PROJECT_ENTITY.SCHEMA, ProjectConstants.MD_PROJECT_ENTITY.ELEMENT,
                    ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, "project_" + item.getID().toString() + "_item"));
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
                     .contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes
                     .TEXT_URI_LIST_VALUE))
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

        StringBuilder placeholder = new StringBuilder();
        placeholder.append("project_").append(publicItem1.getID().toString()).append("_item");

        communityService.addMetadata(context, parentCommunity, ProjectConstants.MD_PROJECT_ENTITY.SCHEMA,
                ProjectConstants.MD_PROJECT_ENTITY.ELEMENT, ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, null,
                                     placeholder.toString());

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
                                  org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE))
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
            // check that the new cloned collelction has a new item project
            Iterator<Item> items = itemService.findAllByCollection(context, colProject);
            assertTrue(items.hasNext());
            Item item = items.next();
            assertTrue(containeMetadata(itemService, item, "dc", "title", null, "My new Community"));
            assertTrue(containeMetadata(itemService, item, "cris", "project", "shared", "project"));
            assertTrue(containeMetadata(communityService, subCommunityOfCloneTarget,
                    ProjectConstants.MD_PROJECT_ENTITY.SCHEMA, ProjectConstants.MD_PROJECT_ENTITY.ELEMENT,
                    ProjectConstants.MD_PROJECT_ENTITY.QUALIFIER, "project_" + item.getID().toString() + "_item"));
            assertFalse(items.hasNext());

        } finally {
            CommunityBuilder.deleteCommunity(idRef.get());
        }
    }

    private <T extends DSpaceObject> boolean containeMetadata(DSpaceObjectService<T> service, T target, String schema,
            String element, String qualifier, String valueToCheck) {
        String value = service.getMetadataFirstValue(target, schema, element, qualifier, null);
        if (StringUtils.equals(value, valueToCheck)) {
            return true;
        }
        return false;
    }
}
