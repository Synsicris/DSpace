/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.dspace.app.rest.utils.UsageReportUtils.TOP_CITIES_REPORT_ID;
import static org.dspace.app.rest.utils.UsageReportUtils.TOP_CITIES_REPORT_ID_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOP_COUNTRIES_REPORT_ID;
import static org.dspace.app.rest.utils.UsageReportUtils.TOP_COUNTRIES_REPORT_ID_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOP_ITEMS_REPORT_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_DOWNLOADS_REPORT_ID;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_DOWNLOADS_REPORT_ID_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_PER_MONTH_REPORT_ID;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_PER_MONTH_REPORT_ID_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_REPORT_ID;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_REPORT_ID_RELATION;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_TOTAL_DOWNLOADS;
import static org.dspace.app.rest.utils.UsageReportUtils.TOTAL_VISITS_TOTAL_DOWNLOADS_RELATION;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dspace.app.rest.matcher.PageMatcher;
import org.dspace.app.rest.matcher.UsageReportMatcher;
import org.dspace.app.rest.model.UsageReportPointCityRest;
import org.dspace.app.rest.model.UsageReportPointCountryRest;
import org.dspace.app.rest.model.UsageReportPointDateRest;
import org.dspace.app.rest.model.UsageReportPointDsoTotalVisitsRest;
import org.dspace.app.rest.model.UsageReportPointRest;
import org.dspace.app.rest.model.ViewEventRest;
import org.dspace.app.rest.repository.StatisticsRestRepository;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.UsageReportUtils;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.builder.BitstreamBuilder;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.ResourcePolicyBuilder;
import org.dspace.builder.SiteBuilder;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.Site;
import org.dspace.core.Constants;
import org.dspace.eperson.EPerson;
import org.dspace.services.ConfigurationService;
import org.dspace.statistics.factory.StatisticsServiceFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Integration test to test the /api/statistics/usagereports/ endpoints, see {@link UsageReportUtils} and
 * {@link StatisticsRestRepository}
 *
 * @author Maria Verdonck (Atmire) on 10/06/2020
 */
public class StatisticsRestRepositoryIT extends AbstractControllerIntegrationTest {

    @Autowired
    ConfigurationService configurationService;
    @Autowired
    protected AuthorizeService authorizeService;

    private Community communityNotVisited;
    private Community communityVisited;
    private Collection collectionNotVisited;
    private Collection collectionVisited;
    private Item itemNotVisitedWithBitstreams;
    private Item itemVisited;
    private Bitstream bitstreamNotVisited;
    private Bitstream bitstreamVisited;
    private Item person;
    private Item orgUnit;
    private Item publicationVisited1;
    private Item publicationVisited2;
    private Bitstream bitstreampublication_first;
    private Bitstream bitstreampublication_second;
    private String loggedInToken;
    private String adminToken;

    @BeforeClass
    public static void clearStatistics() throws Exception {
        // To ensure these tests start "fresh", clear out any existing statistics data.
        // NOTE: this is committed immediately in removeIndex()
        StatisticsServiceFactory.getInstance().getSolrLoggerService().removeIndex("*:*");
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Explicitly use solr commit in SolrLoggerServiceImpl#postView
        configurationService.setProperty("solr-statistics.autoCommit", false);

        context.turnOffAuthorisationSystem();

        Community community = CommunityBuilder.createCommunity(context).build();
        communityNotVisited = CommunityBuilder.createSubCommunity(context, community).build();
        communityVisited = CommunityBuilder.createSubCommunity(context, community).build();
        collectionNotVisited = CollectionBuilder.createCollection(context, community).build();
        collectionVisited = CollectionBuilder.createCollection(context, community).build();
        itemVisited = ItemBuilder.createItem(context, collectionNotVisited).build();
        itemNotVisitedWithBitstreams = ItemBuilder.createItem(context, collectionNotVisited).build();
        bitstreamNotVisited = BitstreamBuilder.createBitstream(context,
                itemNotVisitedWithBitstreams, toInputStream("test", UTF_8)).withName("BitstreamNotVisitedName").build();
        bitstreamVisited = BitstreamBuilder
                .createBitstream(context, itemNotVisitedWithBitstreams, toInputStream("test", UTF_8))
                .withName("BitstreamVisitedName").build();

        loggedInToken = getAuthToken(eperson.getEmail(), password);
        adminToken = getAuthToken(admin.getEmail(), password);
        EntityTypeBuilder.createEntityTypeBuilder(context, "OrgUnit").build();
        EntityTypeBuilder.createEntityTypeBuilder(context, "Person").build();
        EntityTypeBuilder.createEntityTypeBuilder(context, "Publication").build();
        //orgUnit
        orgUnit = ItemBuilder.createItem(context, collectionVisited)
                             .withEntityType("OrgUnit").withFullName("4Science")
                             .withTitle("4Science").build();
        //person item for relation inverse
        //it has as affiliation 4Science
        person = ItemBuilder.createItem(context, collectionVisited)
                            .withEntityType("Person").withFullName("testPerson")
                            .withTitle("testPerson")
                            .withAffiliation(orgUnit.getName(), orgUnit.getID().toString()).build();
        //first publication for person item
        publicationVisited1 = ItemBuilder.createItem(context, collectionVisited)
                                         .withEntityType("Publication")
                                         .withAuthor(person.getName(), person.getID().toString())
                                         .build();
        //second publication for person item
        publicationVisited2 = ItemBuilder.createItem(context, collectionVisited)
                                         .withEntityType("Publication")
                                         .withAuthor(person.getName(), person.getID().toString())
                                         .build();
        //bitstream for first publication of person
        bitstreampublication_first = BitstreamBuilder
                                         .createBitstream(context, publicationVisited1,
                                                          toInputStream("test", UTF_8))
                                         .withName("bitstream1")
                .build();
        //bitstream for second publication of person
        bitstreampublication_second = BitstreamBuilder
                                          .createBitstream(context, publicationVisited2,
                                                           toInputStream("test", UTF_8))
                                          .withName("bitstream2")
                .build();
        context.restoreAuthSystemState();
    }

    @Test
    public void usagereports_withoutId_NotImplementedException() throws Exception {
        getClient().perform(get("/api/statistics/usagereports"))
                .andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()));
    }

    @Test
    public void usagereports_notProperUUIDAndReportId_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/notProperUUIDAndReportId"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void usagereports_nonValidUUIDpart_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/notAnUUID" + "_" + TOTAL_VISITS_REPORT_ID))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void usagereports_nonValidReportIDpart_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() +
                "_NotValidReport"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void usagereports_NonExistentUUID_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/" + UUID.randomUUID() + "_" + TOTAL_VISITS_REPORT_ID))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void usagereport_onlyAdminReadRights() throws Exception {
        // ** WHEN **
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        // We request a dso's TotalVisits usage stat report as anon but dso has no read policy for anon
        getClient().perform(
                get(
                    "/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() +
                        "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isUnauthorized());
        // We request a dso's TotalVisits usage stat report as admin
        getClient(adminToken).perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk());
    }

    @Test
    public void usagereport_onlyAdminReadRights_unvalidToken() throws Exception {
        // ** WHEN **
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        // We request a dso's TotalVisits usage stat report with unvalid token
        getClient("unvalidToken").perform(
            get("/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                                 // ** THEN **
                                 .andExpect(status().isUnauthorized());
    }

    @Test
    public void usagereport_loggedInUserReadRights() throws Exception {
        // ** WHEN **
        context.turnOffAuthorisationSystem();
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        ResourcePolicyBuilder.createResourcePolicy(context)
                .withDspaceObject(itemNotVisitedWithBitstreams)
                .withAction(Constants.READ)
                .withUser(eperson).build();

        EPerson eperson1 = EPersonBuilder.createEPerson(context)
                .withEmail("eperson1@mail.com")
                .withPassword(password)
                .build();
        context.restoreAuthSystemState();
        String anotherLoggedInUserToken = getAuthToken(eperson1.getEmail(), password);
        // We request a dso's TotalVisits usage stat report as anon but dso has no read policy for anon
        getClient().perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isUnauthorized());
        // We request a dso's TotalVisits usage stat report as logged in eperson and has read policy for this user
        getClient(loggedInToken).perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk());
        // We request a dso's TotalVisits usage stat report as another logged in eperson and has no read policy for
        // this user
        getClient(anotherLoggedInUserToken).perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isForbidden());
    }

    @Test
    public void totalVisitsReport_Community_Visited() throws Exception {
        // ** WHEN **
        // We visit the community
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("community");
        viewEventRest.setTargetId(communityVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setType("community");
        expectedPoint.setId(communityVisited.getID().toString());

        // And request that community's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + communityVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(communityVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Community_NotVisited() throws Exception {
        // ** WHEN **
        // Community is never visited
        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 0);
        expectedPoint.setType("community");
        expectedPoint.setId(communityNotVisited.getID().toString());

        // And request that community's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + communityNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(communityNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Collection_Visited() throws Exception {
        // ** WHEN **
        // We visit the collection twice
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("collection");
        viewEventRest.setTargetId(collectionVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 2);
        expectedPoint.setType("collection");
        expectedPoint.setId(collectionVisited.getID().toString());

        // And request that collection's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + collectionVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(collectionVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Collection_NotVisited() throws Exception {
        // ** WHEN **
        // Collection is never visited
        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 0);
        expectedPoint.setType("collection");
        expectedPoint.setId(collectionNotVisited.getID().toString());

        // And request that collection's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + collectionNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(collectionNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Item_Visited() throws Exception {
        // ** WHEN **
        // We visit an Item
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setType("item");
        expectedPoint.setId(itemVisited.getID().toString());

        // And request that collection's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Item_NotVisited() throws Exception {
        // ** WHEN **
        //Item is never visited
        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 0);
        expectedPoint.setType("item");
        expectedPoint.setId(itemNotVisitedWithBitstreams.getID().toString());

        // And request that item's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Bitstream_Visited() throws Exception {
        // ** WHEN **
        // We visit a Bitstream
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("bitstream");
        viewEventRest.setTargetId(bitstreamVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setType("bitstream");
        expectedPoint.setId(bitstreamVisited.getID().toString());

        // And request that bitstream's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + bitstreamVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(bitstreamVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsReport_Bitstream_NotVisited() throws Exception {
        // ** WHEN **
        // Bitstream is never visited
        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 0);
        expectedPoint.setType("bitstream");
        expectedPoint.setId(bitstreamNotVisited.getID().toString());

        // And request that bitstream's TotalVisits stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + bitstreamNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(bitstreamNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID, Arrays.asList(expectedPoint))))
                );
    }

    @Test
    public void totalVisitsPerMonthReport_Item_Visited() throws Exception {
        // ** WHEN **
        // We visit an Item
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<UsageReportPointRest> expectedPoints = this.getListOfVisitsPerMonthsPoints(1);

        // And request that item's TotalVisitsPerMonth stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemVisited.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemVisited.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                        TOTAL_VISITS_PER_MONTH_REPORT_ID, expectedPoints))));
    }

    @Test
    public void totalVisitsPerMonthReport_Item_NotVisited() throws Exception {
        // ** WHEN **
        // Item is not visited
        List<UsageReportPointRest> expectedPoints = this.getListOfVisitsPerMonthsPoints(0);

        // And request that item's TotalVisitsPerMonth stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() + "_" +
                        TOTAL_VISITS_PER_MONTH_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(
                                        itemNotVisitedWithBitstreams.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                        TOTAL_VISITS_PER_MONTH_REPORT_ID, expectedPoints))));
    }

    @Test
    public void totalVisitsPerMonthReport_Collection_Visited() throws Exception {
        // ** WHEN **
        // We visit a Collection twice
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("collection");
        viewEventRest.setTargetId(collectionVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<UsageReportPointRest> expectedPoints = this.getListOfVisitsPerMonthsPoints(2);

        // And request that collection's TotalVisitsPerMonth stat report
        getClient().perform(
                get("/api/statistics/usagereports/" +
                        collectionVisited.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(collectionVisited.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                        TOTAL_VISITS_PER_MONTH_REPORT_ID, expectedPoints))));
    }

    @Test
    public void TotalDownloadsReport_Bitstream() throws Exception {
        // ** WHEN **
        // We visit a Bitstream
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("bitstream");
        viewEventRest.setTargetId(bitstreamVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setType("bitstream");
        expectedPoint.setId(bitstreamVisited.getID().toString());

        // And request that bitstreams's TotalDownloads stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + bitstreamVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(bitstreamVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                        TOTAL_DOWNLOADS_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    @Test
    public void TotalDownloadsReport_Item() throws Exception {
        // ** WHEN **
        // We visit an Item's bitstream
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("bitstream");
        viewEventRest.setTargetId(bitstreamVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPoint = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setId("BitstreamVisitedName");
        expectedPoint.setType("bitstream");

        // And request that item's TotalDownloads stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() + "_" +
                        TOTAL_DOWNLOADS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemNotVisitedWithBitstreams.getID() +
                                                      "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                        TOTAL_DOWNLOADS_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    @Test
    public void TotalDownloadsReport_Item_NotVisited() throws Exception {
        // ** WHEN **
        // You don't visit an item's bitstreams
        // And request that item's TotalDownloads stat report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemNotVisitedWithBitstreams.getID() + "_" +
                        TOTAL_DOWNLOADS_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemNotVisitedWithBitstreams.getID() +
                                                      "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                        TOTAL_DOWNLOADS_REPORT_ID, new ArrayList<>()))));
    }

    @Test
    public void TotalDownloadsReport_NotSupportedDSO_Collection() throws Exception {
        getClient()
                .perform(get("/api/statistics/usagereports/" +
                                 collectionVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID))
                .andExpect(status().isNotFound());
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCountriesReport_Collection_Visited() throws Exception {
        // ** WHEN **
        // We visit a Collection
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("collection");
        viewEventRest.setTargetId(collectionVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointCountryRest expectedPoint = new UsageReportPointCountryRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setId("US");
        expectedPoint.setLabel("United States");

        // And request that collection's TopCountries report
        getClient().perform(
                get("/api/statistics/usagereports/" + collectionVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(collectionVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                        TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCountriesReport_Community_Visited() throws Exception {
        // ** WHEN **
        // We visit a Community twice
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("community");
        viewEventRest.setTargetId(communityVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointCountryRest expectedPoint = new UsageReportPointCountryRest();
        expectedPoint.addValue("views", 2);
        expectedPoint.setId("US");
        expectedPoint.setLabel("United States");

        // And request that collection's TopCountries report
        getClient().perform(
                get("/api/statistics/usagereports/" + communityVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(communityVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                        TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCountriesReport_Item_NotVisited() throws Exception {
        // ** WHEN **
        // Item is not visited
        // And request that item's TopCountries report
        getClient().perform(
                get("/api/statistics/usagereports/" +
                        itemNotVisitedWithBitstreams.getID() + "_" + TOP_COUNTRIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemNotVisitedWithBitstreams.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                        TOP_COUNTRIES_REPORT_ID, new ArrayList<>()))));
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCitiesReport_Item_Visited() throws Exception {
        // ** WHEN **
        // We visit an Item
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointCityRest expectedPoint = new UsageReportPointCityRest();
        expectedPoint.addValue("views", 1);
        expectedPoint.setId("New York");

        // And request that item's TopCities report
        getClient().perform(
                get("/api/statistics/usagereports/" + itemVisited.getID() + "_" + TOP_CITIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(itemVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                        TOP_CITIES_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCitiesReport_Community_Visited() throws Exception {
        // ** WHEN **
        // We visit a Community thrice
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("community");
        viewEventRest.setTargetId(communityVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient(loggedInToken).perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointCityRest expectedPoint = new UsageReportPointCityRest();
        expectedPoint.addValue("views", 3);
        expectedPoint.setId("New York");

        // And request that community's TopCities report
        getClient().perform(
                get("/api/statistics/usagereports/" + communityVisited.getID() + "_" + TOP_CITIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(communityVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                        TOP_CITIES_REPORT_ID, Arrays.asList(expectedPoint)))));
    }

    /**
     * Note: Geolite response mocked in {@link org.dspace.statistics.MockSolrLoggerServiceImpl}
     */
    @Test
    public void topCitiesReport_Collection_NotVisited() throws Exception {
        // ** WHEN **
        // Collection is not visited
        // And request that collection's TopCountries report
        getClient().perform(
                get("/api/statistics/usagereports/" + collectionNotVisited.getID() + "_" + TOP_CITIES_REPORT_ID))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.is(
                        UsageReportMatcher
                                .matchUsageReport(collectionNotVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                        TOP_CITIES_REPORT_ID, new ArrayList<>()))));
    }

    @Test
    public void usagereportsSearch_notProperURI_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/search/object?uri=BadUri"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void usagereportsSearch_noURI_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/search/object"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void usagereportsSearch_NonExistentUUID_Exception() throws Exception {
        getClient().perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                "/items/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page",
                        PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 0, 0)));
    }

    @Test
    public void usagereportSearch_onlyAdminReadRights() throws Exception {
        // ** WHEN **
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        // We request a dso's TotalVisits usage stat report as anon but dso has no read policy for anon
        getClient().perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                "/items/" + itemNotVisitedWithBitstreams.getID()))
                // ** THEN **
                .andExpect(status().isUnauthorized());
        // We request a dso's TotalVisits usage stat report as admin
        getClient(adminToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api" +
                        "/core/items/" + itemNotVisitedWithBitstreams.getID()))
                // ** THEN **
                .andExpect(status().isOk());
    }

    @Test
    public void usagereportSearch_onlyAdminReadRights_unvalidToken() throws Exception {
        // ** WHEN **
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        // We request a dso's TotalVisits usage stat report with unvalid token
        getClient("unvalidToken")
            .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                         "/items/" + itemNotVisitedWithBitstreams.getID()))
            // ** THEN **
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void usagereportSearch_loggedInUserReadRights() throws Exception {
        // ** WHEN **
        context.turnOffAuthorisationSystem();
        authorizeService.removeAllPolicies(context, itemNotVisitedWithBitstreams);
        ResourcePolicyBuilder.createResourcePolicy(context)
                .withDspaceObject(itemNotVisitedWithBitstreams)
                .withAction(Constants.READ)
                .withUser(eperson).build();

        EPerson eperson1 = EPersonBuilder.createEPerson(context)
                .withEmail("eperson1@mail.com")
                .withPassword(password)
                .build();
        context.restoreAuthSystemState();
        String anotherLoggedInUserToken = getAuthToken(eperson1.getEmail(), password);
        // We request a dso's TotalVisits usage stat report as anon but dso has no read policy for anon
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + itemNotVisitedWithBitstreams.getID()))
                // ** THEN **
                .andExpect(status().isUnauthorized());
        // We request a dso's TotalVisits usage stat report as logged in eperson and has read policy for this user
        getClient(loggedInToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + itemNotVisitedWithBitstreams.getID()))
                // ** THEN **
                .andExpect(status().isOk());
        // We request a dso's TotalVisits usage stat report as another logged in eperson and has no read policy for
        // this user
        getClient(anotherLoggedInUserToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + itemNotVisitedWithBitstreams.getID()))
                // ** THEN **
                .andExpect(status().isForbidden());
    }

    @Test
    public void usageReportsSearch_Site() throws Exception {
        context.turnOffAuthorisationSystem();
        Site site = SiteBuilder.createSite(context).build();
        Item itemVisited2 = ItemBuilder.createItem(context, collectionNotVisited).build();
        context.restoreAuthSystemState();

        // ** WHEN **
        // We visit an item and another twice
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        ViewEventRest viewEventRest2 = new ViewEventRest();
        viewEventRest2.setTargetType("item");
        viewEventRest2.setTargetId(itemVisited2.getID());

        ObjectMapper mapper2 = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper2.writeValueAsBytes(viewEventRest2))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper2.writeValueAsBytes(viewEventRest2))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<UsageReportPointRest> points = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPoint1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint1.addValue("views", 1);
        expectedPoint1.setType("item");
        expectedPoint1.setId(itemVisited.getID().toString());
        UsageReportPointDsoTotalVisitsRest expectedPoint2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint2.addValue("views", 2);
        expectedPoint2.setType("item");
        expectedPoint2.setId(itemVisited2.getID().toString());
        points.add(expectedPoint1);
        points.add(expectedPoint2);

        // And request the sites global usage report (show top most popular items)
        getClient(adminToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/sites/" + site.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(site.getID() + "_" +
                                                      TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID, points))));
    }

    @Test
    public void usageReportsSearch_Community_Visited() throws Exception {
        // ** WHEN **
        // We visit a community
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("community");
        viewEventRest.setTargetId(communityVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("community");
        expectedPointTotalVisits.setId(communityVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");

        // And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/communities/" + communityVisited.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(communityVisited.getID() +
                                                      "_" + TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() +
                                                                "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                TOP_CITIES_REPORT_ID, Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPointCountry))
                )));
    }

    @Test
    public void usageReportsSearch_Collection_NotVisited() throws Exception {
        // ** WHEN **
        // Collection is not visited

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 0);
        expectedPointTotalVisits.setType("collection");
        expectedPointTotalVisits.setId(collectionNotVisited.getID().toString());
        // And request the collection's usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/collections/" + collectionNotVisited.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(collectionNotVisited.getID() + "_" + TOTAL_VISITS_REPORT_ID,
                                        TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher
                                .matchUsageReport(collectionNotVisited.getID() + "_" + TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                        TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                        this.getListOfVisitsPerMonthsPoints(0)),
                        UsageReportMatcher.matchUsageReport(collectionNotVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                TOP_CITIES_REPORT_ID, new ArrayList<>()),
                        UsageReportMatcher.matchUsageReport(collectionNotVisited.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID,
                                TOP_COUNTRIES_REPORT_ID, new ArrayList<>()))));
    }

    @Test
    public void usageReportsSearch_Item_Visited_FileNotVisited() throws Exception {
        // ** WHEN **
        // We visit an item
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("item");
        expectedPointTotalVisits.setId(itemVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");

        //views and downloads
        List<UsageReportPointRest> totalDownloadsPoints = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit1.addValue("views", 1);
        expectedPointTotalVisitsBit1.setType("item");
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit2.addValue("views", 0);
        expectedPointTotalVisitsBit2.setType("bitstream");
        totalDownloadsPoints.add(expectedPointTotalVisitsBit1);
        totalDownloadsPoints.add(expectedPointTotalVisitsBit2);


        // And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + itemVisited.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(itemVisited.getID() +
                                                      "_" + TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                TOP_CITIES_REPORT_ID, Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPointCountry)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                TOTAL_DOWNLOADS_REPORT_ID, new ArrayList<>()),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOTAL_VISITS_TOTAL_DOWNLOADS,
                                TOTAL_VISITS_TOTAL_DOWNLOADS, totalDownloadsPoints)

                )));
    }

    @Test
    public void usageReportsSearch_ItemVisited_FilesVisited() throws Exception {
        context.turnOffAuthorisationSystem();
        Bitstream bitstream1 =
                BitstreamBuilder.createBitstream(context, itemVisited,
                                                 toInputStream("test", UTF_8))
                                .withName("bitstream1")
                                .build();
        Bitstream bitstream2 =
                BitstreamBuilder.createBitstream(context, itemVisited,
                                                 toInputStream("test", UTF_8))
                                .withName("bitstream2")
                                .build();
        context.restoreAuthSystemState();

        // ** WHEN **
        // We visit an item
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        // And its two files, second one twice
        ViewEventRest viewEventRestBit1 = new ViewEventRest();
        viewEventRestBit1.setTargetType("bitstream");
        viewEventRestBit1.setTargetId(bitstream1.getID());
        ViewEventRest viewEventRestBit2 = new ViewEventRest();
        viewEventRestBit2.setTargetType("bitstream");
        viewEventRestBit2.setTargetId(bitstream2.getID());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestBit1))
                .contentType(contentType))
                .andExpect(status().isCreated());
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestBit2))
                .contentType(contentType))
                .andExpect(status().isCreated());
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestBit2))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("item");
        expectedPointTotalVisits.setId(itemVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");

        List<UsageReportPointRest> totalDownloadsPoints = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit1.addValue("views", 1);
        expectedPointTotalVisitsBit1.setId("bitstream1");
        expectedPointTotalVisitsBit1.setType("bitstream");
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit2.addValue("views", 2);
        expectedPointTotalVisitsBit2.setId("bitstream2");
        expectedPointTotalVisitsBit2.setType("bitstream");
        totalDownloadsPoints.add(expectedPointTotalVisitsBit1);
        totalDownloadsPoints.add(expectedPointTotalVisitsBit2);


        // first point for views
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsItem = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsItem.addValue("views", 1);
        expectedPointTotalVisitsItem.setType("item");

        //second point for total downlods
        UsageReportPointDsoTotalVisitsRest expectedPointTotalDownloads = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalDownloads.addValue("views", 3);
        expectedPointTotalDownloads.setType("bitstream");

        List<UsageReportPointRest> usageReportPointRestsVisitsAndDownloads = new ArrayList<>();
        usageReportPointRestsVisitsAndDownloads.add(expectedPointTotalVisitsItem);
        usageReportPointRestsVisitsAndDownloads.add(expectedPointTotalDownloads);


        // And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + itemVisited.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(itemVisited.getID() + "_" +
                                                      TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                TOP_CITIES_REPORT_ID, Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPointCountry)),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                TOTAL_DOWNLOADS_REPORT_ID, totalDownloadsPoints),
                        UsageReportMatcher.matchUsageReport(itemVisited.getID() + "_" + TOTAL_VISITS_TOTAL_DOWNLOADS,
                                TOTAL_VISITS_TOTAL_DOWNLOADS, usageReportPointRestsVisitsAndDownloads)
                        )

                        )

                );
    }

    @Test
    public void usageReportsSearch_Bitstream_Visited() throws Exception {
        // ** WHEN **
        // We visit a bitstream
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("bitstream");
        viewEventRest.setTargetId(bitstreamVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("bitstream");
        expectedPointTotalVisits.setLabel("BitstreamVisitedName");
        expectedPointTotalVisits.setId(bitstreamVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");

        // And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + bitstreamVisited.getID()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(bitstreamVisited.getID() + "_" +
                                                      TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" + TOP_CITIES_REPORT_ID,
                                TOP_CITIES_REPORT_ID, Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" + TOP_COUNTRIES_REPORT_ID,
                                TOP_COUNTRIES_REPORT_ID, Arrays.asList(expectedPointCountry)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" + TOTAL_DOWNLOADS_REPORT_ID,
                                TOTAL_DOWNLOADS_REPORT_ID, Arrays.asList(expectedPointTotalVisits)))));
    }

    // Create expected points from -6 months to now, with given number of views in current month
    private List<UsageReportPointRest> getListOfVisitsPerMonthsPoints(int viewsLastMonth) {
        List<UsageReportPointRest> expectedPoints = new ArrayList<>();
        int nrOfMonthsBack = 6;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i <= nrOfMonthsBack; i++) {
            UsageReportPointDateRest expectedPoint = new UsageReportPointDateRest();
            if (i > 0) {
                expectedPoint.addValue("views", 0);
            } else {
                expectedPoint.addValue("views", viewsLastMonth);
            }
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            expectedPoint.setId(month + " " + cal.get(Calendar.YEAR));

            expectedPoints.add(expectedPoint);
            cal.add(Calendar.MONTH, -1);
        }
        return expectedPoints;
    }

    // This test search for statistics before the moment in which item is visited
    @Test
    public void usageReportsSearch_ItemNotVisited_AtTime() throws Exception {
        context.turnOffAuthorisationSystem();
        Site site = SiteBuilder.createSite(context).build();
        //create new item using ItemBuilder
        context.restoreAuthSystemState();
        //visit first item now
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("item");
        viewEventRest.setTargetId(itemVisited.getID());
        ObjectMapper mapper = new ObjectMapper();
        //add visit for first item
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());
        //create expected raport points
        List<UsageReportPointRest> points = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPoint1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint1.addValue("views", 0);
        expectedPoint1.setType("item");
        points.add(expectedPoint1);
        // And request the sites global usage report (show top most popular items) for a specific date range
        // we expect no points becase we are searching in a moment before the view of item happened
        getClient(adminToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/sites/" + site.getID() + "&startDate=2019-06-01&endDate=2019-06-02"))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(site.getID() + "_" +
                                                      TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID, points))));
    }

    // This test search for statistics one day after the moment in which community is visited
    @Test
    public void usageReportsSearch_Community_VisitedAtTime() throws Exception {
        // ** WHEN **
        // We visit a community
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("community");
        viewEventRest.setTargetId(communityVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("community");
        expectedPointTotalVisits.setId(communityVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");
        //add one day to the moment when we visit the community
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        String endDate = dateFormat.format(cal.getTime());
        // And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/communities/" + communityVisited.getID() + "&startDate=2019-06-01&endDate=" + endDate))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher
                                .matchUsageReport(communityVisited.getID() + "_" +
                                                      TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                        Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() + "_" +
                                                                TOP_CITIES_REPORT_ID, TOP_CITIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(communityVisited.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID,
                                                            TOP_COUNTRIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCountry))
                )));
    }

    // filter bitstream only with  start date
    @Test
    public void usageReportsSearch_Bitstream_VisitedFromTime() throws Exception {
        // ** WHEN **
        // We visit a bitstream
        ViewEventRest viewEventRest = new ViewEventRest();
        viewEventRest.setTargetType("bitstream");
        viewEventRest.setTargetId(bitstreamVisited.getID());

        ObjectMapper mapper = new ObjectMapper();

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRest))
                .contentType(contentType))
                .andExpect(status().isCreated());

        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisits = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisits.addValue("views", 1);
        expectedPointTotalVisits.setType("bitstream");
        expectedPointTotalVisits.setLabel("BitstreamVisitedName");
        expectedPointTotalVisits.setId(bitstreamVisited.getID().toString());

        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");

        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");

        //downloads and views expected points
        List<UsageReportPointRest> totalDownloadsPoints = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit1.addValue("views", 1);
        expectedPointTotalVisitsBit1.setType("bitstream");
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit2.addValue("views", 0);
        expectedPointTotalVisitsBit2.setType("bitstream");
        totalDownloadsPoints.add(expectedPointTotalVisitsBit1);
        totalDownloadsPoints.add(expectedPointTotalVisitsBit2);
        //  And request the community usage reports
        getClient()
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + bitstreamVisited.getID() + "&startDate=2019-05-01"))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOTAL_VISITS_REPORT_ID, TOTAL_VISITS_REPORT_ID,
                                                            Arrays.asList(expectedPointTotalVisits)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOP_CITIES_REPORT_ID, TOP_CITIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID,
                                                            TOP_COUNTRIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCountry)),
                        UsageReportMatcher.matchUsageReport(bitstreamVisited.getID() + "_" +
                                                                TOTAL_DOWNLOADS_REPORT_ID,
                                                            TOTAL_DOWNLOADS_REPORT_ID,
                                                            Arrays.asList(expectedPointTotalVisits))
                )));
    }
    //test for inverse relation between person and publication
    @Test
    public void usageReportsSearch_PersonWithPublicationVisited() throws Exception {
        //visit the person
        ViewEventRest viewEventRestItem = new ViewEventRest();
        viewEventRestItem.setTargetType("item");
        viewEventRestItem.setTargetId(person.getID());

        //visit the first publication
        ViewEventRest viewEventRestFirstPublicationOfPerson = new ViewEventRest();
        viewEventRestFirstPublicationOfPerson.setTargetType("item");
        viewEventRestFirstPublicationOfPerson.setTargetId(publicationVisited1.getID());

        //visit the second publication
        ViewEventRest viewEventRestSecondPublicationOfPerson = new ViewEventRest();
        viewEventRestSecondPublicationOfPerson.setTargetType("item");
        viewEventRestSecondPublicationOfPerson.setTargetId(publicationVisited2.getID());

        //first bitstream visit
        ViewEventRest viewEventRestFirstPublicationBitstream = new ViewEventRest();
        viewEventRestFirstPublicationBitstream.setTargetType("bitstream");
        viewEventRestFirstPublicationBitstream.setTargetId(bitstreampublication_first.getID());

        //second bitstream visit
        ViewEventRest viewEventRestSecondPublicationBitstream = new ViewEventRest();
        viewEventRestSecondPublicationBitstream.setTargetType("bitstream");
        viewEventRestSecondPublicationBitstream.setTargetId(bitstreampublication_second.getID());

        //create viewevents for all of items and bistreams
        ObjectMapper mapper = new ObjectMapper();
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestItem))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestFirstPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestFirstPublicationBitstream))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationBitstream))
                .contentType(contentType))
                .andExpect(status().isCreated());

        //create expected report points for visits
        UsageReportPointDsoTotalVisitsRest totalVisitRelation = new UsageReportPointDsoTotalVisitsRest();
        totalVisitRelation.addValue("views", 3);
        totalVisitRelation.setType("item");
        totalVisitRelation.setLabel(person.getName());
        totalVisitRelation.setId(person.getID().toString());
        //create expected report points for visits with relation
        UsageReportPointDsoTotalVisitsRest expectedPointTotal = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotal.addValue("views", 1);
        expectedPointTotal.setType("item");
        expectedPointTotal.setLabel(person.getName());
        expectedPointTotal.setId(person.getID().toString());

        //create expected report points for city visits
        UsageReportPointCityRest expectedPointCity = new UsageReportPointCityRest();
        expectedPointCity.addValue("views", 1);
        expectedPointCity.setId("New York");
        //create expected report points for city visits with relation
        UsageReportPointCityRest expectedPointCityWithRelation = new UsageReportPointCityRest();
        expectedPointCityWithRelation.addValue("views", 3);
        expectedPointCityWithRelation.setId("New York");
        //create expected report points for contry visits
        UsageReportPointCountryRest expectedPointCountry = new UsageReportPointCountryRest();
        expectedPointCountry.addValue("views", 1);
        expectedPointCountry.setId("US");
        expectedPointCountry.setLabel("United States");
        //create expected report points for country visits with relation
        UsageReportPointCountryRest expectedPointCountryWithRelation = new UsageReportPointCountryRest();
        expectedPointCountryWithRelation.addValue("views", 3);
        expectedPointCountryWithRelation.setId("US");
        expectedPointCountryWithRelation.setLabel("United States");
        getClient(adminToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + person.getID().toString()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOTAL_VISITS_REPORT_ID,
                                                            TOTAL_VISITS_REPORT_ID,
                                                            Arrays.asList(expectedPointTotal)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOTAL_VISITS_REPORT_ID_RELATION,
                                                            TOTAL_VISITS_REPORT_ID,
                                                            Arrays.asList(totalVisitRelation)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            this.getListOfVisitsPerMonthsPoints(1)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID_RELATION,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            this.getListOfVisitsPerMonthsPoints(3)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOP_CITIES_REPORT_ID, TOP_CITIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCity)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOP_CITIES_REPORT_ID_RELATION,
                                                            TOP_CITIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCityWithRelation)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID,
                                                            TOP_COUNTRIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCountry)),
                        UsageReportMatcher.matchUsageReport(person.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID_RELATION,
                                                            TOP_COUNTRIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCountryWithRelation))
                )));
    }
    //test for inverse relation between orgunit and publication
    @Test
    public void usageReportsSearch_OrgUnitWithPublicationVisited() throws Exception {
        //visit the orgunit
        ViewEventRest viewEventRestItemOrgUnit = new ViewEventRest();
        viewEventRestItemOrgUnit.setTargetType("item");
        viewEventRestItemOrgUnit.setTargetId(orgUnit.getID());

        //visit the person
        ViewEventRest viewEventRestItem = new ViewEventRest();
        viewEventRestItem.setTargetType("item");
        viewEventRestItem.setTargetId(person.getID());

        //visit the first publication
        ViewEventRest viewEventRestFirstPublicationOfPerson = new ViewEventRest();
        viewEventRestFirstPublicationOfPerson.setTargetType("item");
        viewEventRestFirstPublicationOfPerson.setTargetId(publicationVisited1.getID());

        //visit the second publication
        ViewEventRest viewEventRestSecondPublicationOfPerson = new ViewEventRest();
        viewEventRestSecondPublicationOfPerson.setTargetType("item");
        viewEventRestSecondPublicationOfPerson.setTargetId(publicationVisited2.getID());

        //first bitstream visit
        ViewEventRest viewEventRestFirstPublicationBitstream = new ViewEventRest();
        viewEventRestFirstPublicationBitstream.setTargetType("bitstream");
        viewEventRestFirstPublicationBitstream.setTargetId(bitstreampublication_first.getID());

        //second bitstream visit
        ViewEventRest viewEventRestSecondPublicationBitstream = new ViewEventRest();
        viewEventRestSecondPublicationBitstream.setTargetType("bitstream");
        viewEventRestSecondPublicationBitstream.setTargetId(bitstreampublication_second.getID());

        //create viewevents for all of items and bistreams
        ObjectMapper mapper = new ObjectMapper();
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestItemOrgUnit))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestItem))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestFirstPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationOfPerson))
                .contentType(contentType))
                .andExpect(status().isCreated());
        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestFirstPublicationBitstream))
                .contentType(contentType))
                .andExpect(status().isCreated());

        getClient().perform(post("/api/statistics/viewevents")
                .content(mapper.writeValueAsBytes(viewEventRestSecondPublicationBitstream))
                .contentType(contentType))
                .andExpect(status().isCreated());

        //create expected report points for visits
        UsageReportPointDsoTotalVisitsRest totalVisitRelation = new UsageReportPointDsoTotalVisitsRest();
        totalVisitRelation.addValue("views", 3);
        totalVisitRelation.setType("item");
        totalVisitRelation.setLabel("4Science");
        totalVisitRelation.setId(orgUnit.getID().toString());
        //create expected report points for city visits with relation
        UsageReportPointCityRest expectedPointCityWithRelation = new UsageReportPointCityRest();
        expectedPointCityWithRelation.addValue("views", 3);
        expectedPointCityWithRelation.setId("New York");
        //create expected report points for country visits with relation
        UsageReportPointCountryRest expectedPointCountryWithRelation = new UsageReportPointCountryRest();
        expectedPointCountryWithRelation.addValue("views", 3);
        expectedPointCountryWithRelation.setId("US");
        expectedPointCountryWithRelation.setLabel("United States");
        //top items expected report points
        List<UsageReportPointRest> points = new ArrayList<>();
        //first publication
        UsageReportPointDsoTotalVisitsRest expectedPoint1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint1.addValue("views", 2);
        expectedPoint1.setType("item");
        expectedPoint1.setId(publicationVisited2.getID().toString());
        points.add(expectedPoint1);
        //second publication
        UsageReportPointDsoTotalVisitsRest expectedPoint2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPoint2.addValue("views", 1);
        expectedPoint2.setType("item");
        expectedPoint2.setId(publicationVisited1.getID().toString());
        points.add(expectedPoint2);

        //total downloads expected points
        List<UsageReportPointRest> totalDownloadsPoints = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit1 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit1.addValue("views", 1);
        expectedPointTotalVisitsBit1.setId("bitstream2");
        expectedPointTotalVisitsBit1.setType("bitstream");
        UsageReportPointDsoTotalVisitsRest expectedPointTotalVisitsBit2 = new UsageReportPointDsoTotalVisitsRest();
        expectedPointTotalVisitsBit2.addValue("views", 1);
        expectedPointTotalVisitsBit2.setId("bitstream1");
        expectedPointTotalVisitsBit2.setType("bitstream");
        totalDownloadsPoints.add(expectedPointTotalVisitsBit1);
        totalDownloadsPoints.add(expectedPointTotalVisitsBit2);

        //total downloads and views expected points
        //views
        List<UsageReportPointRest> totalDownloadsAndViewsPoints = new ArrayList<>();
        UsageReportPointDsoTotalVisitsRest views = new UsageReportPointDsoTotalVisitsRest();
        views.addValue("views", 3);
        views.setType("item");
        //downloads
        UsageReportPointDsoTotalVisitsRest downloads = new UsageReportPointDsoTotalVisitsRest();
        downloads.addValue("views", 2);
        downloads.setType("bitstream");
        totalDownloadsAndViewsPoints.add(views);
        totalDownloadsAndViewsPoints.add(downloads);

        getClient(adminToken)
                .perform(get("/api/statistics/usagereports/search/object?uri=http://localhost:8080/server/api/core" +
                        "/items/" + orgUnit.getID().toString()))
                // ** THEN **
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usagereports", not(empty())))
                .andExpect(jsonPath("$._embedded.usagereports", Matchers.containsInAnyOrder(
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOTAL_VISITS_REPORT_ID_RELATION,
                                                            TOTAL_VISITS_REPORT_ID,
                                                            Arrays.asList(totalVisitRelation)),
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOTAL_VISITS_PER_MONTH_REPORT_ID_RELATION,
                                                            TOTAL_VISITS_PER_MONTH_REPORT_ID,
                                                            this.getListOfVisitsPerMonthsPoints(3)),
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOP_CITIES_REPORT_ID_RELATION,
                                                            TOP_CITIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCityWithRelation)),
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOP_COUNTRIES_REPORT_ID_RELATION,
                                                            TOP_COUNTRIES_REPORT_ID,
                                                            Arrays.asList(expectedPointCountryWithRelation)),


                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOP_ITEMS_REPORT_RELATION,
                                                            TOTAL_VISITS_REPORT_ID, points),
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOTAL_DOWNLOADS_REPORT_ID_RELATION,
                                                            TOTAL_DOWNLOADS_REPORT_ID, totalDownloadsPoints),
                        UsageReportMatcher.matchUsageReport(orgUnit.getID() + "_" +
                                                                TOTAL_VISITS_TOTAL_DOWNLOADS_RELATION,
                                                            TOTAL_VISITS_TOTAL_DOWNLOADS,
                                                            totalDownloadsAndViewsPoints)
                )));
    }
}
