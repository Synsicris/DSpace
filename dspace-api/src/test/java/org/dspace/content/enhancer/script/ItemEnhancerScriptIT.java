/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enhancer.script;

import static org.dspace.app.matcher.MetadataValueMatcher.with;
import static org.dspace.content.Item.ANY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dspace.AbstractIntegrationTestWithDatabase;
import org.dspace.app.launcher.ScriptLauncher;
import org.dspace.app.scripts.handler.impl.TestDSpaceRunnableHandler;
import org.dspace.authorize.AuthorizeException;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.WorkspaceItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.ReloadableEntity;
import org.dspace.event.factory.EventServiceFactory;
import org.dspace.event.service.EventService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ItemEnhancerScriptIT extends AbstractIntegrationTestWithDatabase {

    private static String[] consumers;

    private ItemService itemService;

    private Collection collection;

    /**
     * This method will be run before the first test as per @BeforeClass. It will
     * configure the event.dispatcher.default.consumers property to add the
     * CrisConsumer.
     */
    @BeforeClass
    public static void initConsumers() {
        ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
        consumers = configService.getArrayProperty("event.dispatcher.default.consumers");
        Set<String> consumersSet = new HashSet<String>(Arrays.asList(consumers));
        consumersSet.remove("itemenhancer");
        configService.setProperty("event.dispatcher.default.consumers", consumersSet.toArray());
        EventService eventService = EventServiceFactory.getInstance().getEventService();
        eventService.reloadConfiguration();
    }

    /**
     * Reset the event.dispatcher.default.consumers property value.
     */
    @AfterClass
    public static void resetDefaultConsumers() {
        ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
        configService.setProperty("event.dispatcher.default.consumers", consumers);
        EventService eventService = EventServiceFactory.getInstance().getEventService();
        eventService.reloadConfiguration();
    }

    @Before
    public void setup() {

        itemService = ContentServiceFactory.getInstance().getItemService();

        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context)
            .withName("Parent Community")
            .build();

        collection = CollectionBuilder.createCollection(context, parentCommunity)
            .withName("Collection")
            .build();
        context.restoreAuthSystemState();

    }

    @Test
    public void testItemsEnhancement() throws Exception {

        context.turnOffAuthorisationSystem();

        Item firstAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Walter White")
            .withPersonMainAffiliation("4Science")
            .build();

        String firstAuthorId = firstAuthor.getID().toString();

        Item secondAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Jesse Pinkman")
            .withPersonMainAffiliation("Company")
            .build();

        String secondAuthorId = secondAuthor.getID().toString();

        Item firstPublication = ItemBuilder.createItem(context, collection)
            .withTitle("Test publication")
            .withEntityType("Publication")
            .withAuthor("Walter White", firstAuthorId)
            .build();

        Item secondPublication = ItemBuilder.createItem(context, collection)
            .withTitle("Test publication 2")
            .withEntityType("Publication")
            .withAuthor("Walter White", firstAuthorId)
            .withAuthor("Jesse Pinkman", secondAuthorId)
            .build();

        WorkspaceItem thirdPublication = WorkspaceItemBuilder.createWorkspaceItem(context, collection)
            .withTitle("Test publication 3")
            .withEntityType("Publication")
            .withAuthor("Jesse Pinkman", secondAuthorId)
            .build();

        context.commit();

        firstPublication = reload(firstPublication);
        secondPublication = reload(secondPublication);
        thirdPublication = reload(thirdPublication);

        assertThat(getMetadataValues(firstPublication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(firstPublication, "cris.virtualsource.department"), empty());
        assertThat(getMetadataValues(secondPublication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(secondPublication, "cris.virtualsource.department"), empty());
        assertThat(getMetadataValues(thirdPublication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(thirdPublication, "cris.virtualsource.department"), empty());

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        firstPublication = reload(firstPublication);
        secondPublication = reload(secondPublication);

        assertThat(getMetadataValues(firstPublication, "cris.virtual.department"), hasSize(1));
        assertThat(getMetadataValues(firstPublication, "cris.virtualsource.department"), hasSize(1));

        assertThat(getMetadataValues(secondPublication, "cris.virtual.department"), hasSize(2));
        assertThat(getMetadataValues(secondPublication, "cris.virtualsource.department"), hasSize(2));

        assertThat(firstPublication.getMetadata(), hasItem(with("cris.virtual.department", "4Science")));
        assertThat(firstPublication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));
        assertThat(secondPublication.getMetadata(), hasItem(with("cris.virtual.department", "4Science")));
        assertThat(secondPublication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));
        assertThat(secondPublication.getMetadata(), hasItem(with("cris.virtual.department", "Company", 1)));
        assertThat(secondPublication.getMetadata(), hasItem(with("cris.virtualsource.department", secondAuthorId, 1)));

        assertThat(getMetadataValues(thirdPublication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(thirdPublication, "cris.virtualsource.department"), empty());

    }

    @Test
    public void testItemEnhancementWithoutForce() throws Exception {

        context.turnOffAuthorisationSystem();

        Item firstAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Walter White")
            .withPersonMainAffiliation("4Science")
            .build();

        String firstAuthorId = firstAuthor.getID().toString();

        Item secondAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Jesse Pinkman")
            .withPersonMainAffiliation("Company")
            .build();

        String secondAuthorId = secondAuthor.getID().toString();

        Item publication = ItemBuilder.createItem(context, collection)
            .withTitle("Test publication 2 ")
            .withEntityType("Publication")
            .withAuthor("Walter White", firstAuthorId)
            .withAuthor("Jesse Pinkman", secondAuthorId)
            .build();

        context.commit();
        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), empty());

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), hasSize(2));
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), hasSize(2));

        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "4Science")));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "Company", 1)));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", secondAuthorId, 1)));

        context.turnOffAuthorisationSystem();

        MetadataValue authorToRemove = getMetadataValues(publication, "dc.contributor.author").get(1);
        itemService.removeMetadataValues(context, publication, List.of(authorToRemove));

        replaceMetadata(firstAuthor, "person", "affiliation", "name", "University");

        context.restoreAuthSystemState();

        runnableHandler = runScript(false);
        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), hasSize(1));
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), hasSize(1));

        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "4Science")));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));

    }

    @Test
    public void testItemEnhancementWithForce() throws Exception {

        context.turnOffAuthorisationSystem();

        Item firstAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Walter White")
            .withPersonMainAffiliation("4Science")
            .build();

        String firstAuthorId = firstAuthor.getID().toString();

        Item secondAuthor = ItemBuilder.createItem(context, collection)
            .withTitle("Jesse Pinkman")
            .withPersonMainAffiliation("Company")
            .build();

        String secondAuthorId = secondAuthor.getID().toString();

        Item publication = ItemBuilder.createItem(context, collection)
            .withTitle("Test publication 2 ")
            .withEntityType("Publication")
            .withAuthor("Walter White", firstAuthorId)
            .withAuthor("Jesse Pinkman", secondAuthorId)
            .build();

        context.commit();
        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), empty());
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), empty());

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), hasSize(2));
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), hasSize(2));

        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "4Science")));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "Company", 1)));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", secondAuthorId, 1)));

        context.turnOffAuthorisationSystem();

        MetadataValue authorToRemove = getMetadataValues(publication, "dc.contributor.author").get(1);
        itemService.removeMetadataValues(context, publication, List.of(authorToRemove));

        replaceMetadata(firstAuthor, "person", "affiliation", "name", "University");

        context.restoreAuthSystemState();

        runnableHandler = runScript(true);
        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        publication = reload(publication);

        assertThat(getMetadataValues(publication, "cris.virtual.department"), hasSize(1));
        assertThat(getMetadataValues(publication, "cris.virtualsource.department"), hasSize(1));

        assertThat(publication.getMetadata(), hasItem(with("cris.virtual.department", "University")));
        assertThat(publication.getMetadata(), hasItem(with("cris.virtualsource.department", firstAuthorId)));

    }

    @Test
    public void testProjectItemEnhancementAddDates() throws Exception {

        context.turnOffAuthorisationSystem();

//        project item doesn't contain start date or end date
        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmountCurrency("EUR")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withProjectStartDate("2013-08-02")
                                    .withProjectEndDate("2016-08-01")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .build();

        Item funding3  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 3")
                                    .withProjectStartDate("")
                                    .withProjectEndDate("")
                                    .build();

        Item funding4  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 4")
                                    .withProjectStartDate("2013-08-01")
                                    .withProjectEndDate("2016-07-31")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Publication")
                                    .withTitle("Test publication")
                                    .withProjectStartDate("2012-08-01")
                                    .withProjectEndDate("2017-07-31")
                                    .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        funding3 = reload(funding3);
        funding4 = reload(funding4);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding3, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding4, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate"), empty());
        assertThat(getMetadataValues(project, "oairecerif.project.endDate"), empty());
        assertThat(getMetadataValues(project, "synsicris.duration"), empty());

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate"), hasSize(1));
        assertThat(getMetadataValues(project, "synsicris.duration"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.project.startDate").get(0).getValue(), is("2013-08-01"));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate").get(0).getValue(), is("2016-08-01"));
        assertThat(getMetadataValues(project, "synsicris.duration").get(0).getValue(), equalTo("36"));

    }

    @Test
    public void testProjectItemEnhancementUpdateCurrentDates() throws Exception {

        context.turnOffAuthorisationSystem();

//        project item has start date and end date
        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withProjectStartDate("2013-08-16")
                                  .withProjectEndDate("2013-08-20")
                                  .withAmountCurrency("EUR")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withProjectStartDate("2013-08-16")
                                    .withProjectEndDate("2013-08-25")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .build();

        Item funding3  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 3")
                                    .withProjectStartDate("")
                                    .withProjectEndDate("")
                                    .build();

        Item funding4  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 4")
                                    .withProjectStartDate("2013-08-15")
                                    .withProjectEndDate("2013-09-01")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                       .withEntityType("Publication")
                                       .withTitle("Test publication")
                                       .withProjectStartDate("2012-08-01")
                                       .withProjectEndDate("2017-07-31")
                                       .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        funding3 = reload(funding3);
        funding4 = reload(funding4);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding3, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding4, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate").get(0).getValue(),
            is("2013-08-16"));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate").get(0).getValue(),
            is("2013-08-20"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate"), hasSize(1));
        assertThat(getMetadataValues(project, "synsicris.duration"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.project.startDate").get(0).getValue(),
            is("2013-08-15"));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate").get(0).getValue(), is("2013-09-01"));
        assertThat(getMetadataValues(project, "synsicris.duration").get(0).getValue(), is("1"));

    }

    @Test
    public void testProjectItemEnhancementButHasCorrectDates() throws Exception {

        context.turnOffAuthorisationSystem();

//        project item has correct minimum start date and maximum end date of related funding.
        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withProjectStartDate("2013-08-01")
                                  .withProjectEndDate("2016-08-01")
                                  .withAmountCurrency("EUR")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withProjectStartDate("2013-08-01")
                                    .withProjectEndDate("2016-08-01")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .withProjectStartDate("2013-08-02")
                                    .withProjectEndDate("2016-07-31")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                       .withEntityType("Publication")
                                       .withTitle("Test publication")
                                       .withProjectStartDate("2012-08-01")
                                       .withProjectEndDate("2017-07-31")
                                       .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate").get(0).getValue(),
            is("2013-08-01"));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate").get(0).getValue(),
            is("2016-08-01"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.project.startDate"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate"), hasSize(1));
        assertThat(getMetadataValues(project, "synsicris.duration"), empty());

        assertThat(getMetadataValues(project, "oairecerif.project.startDate").get(0).getValue(),
            is("2013-08-01"));
        assertThat(getMetadataValues(project, "oairecerif.project.endDate").get(0).getValue(),
            is("2016-08-01"));

    }

    @Test
    public void testProjectItemEnhancementDatesWithWrongEntityType() throws Exception {

        context.turnOffAuthorisationSystem();


        Item publication = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Publication")
                                  .withTitle("Test Publication")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withProjectStartDate("2013-08-02")
                                    .withProjectEndDate("2016-08-01")
                                    .build();

        funding1 = reload(funding1);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, publication.getName(),
            publication.getID().toString(), 600);

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), hasSize(2));
        assertThat(runnableHandler.getErrorMessages(), containsInAnyOrder(
            containsString("An error occurs during enhancement. The process is aborted"),
            containsString("item:" + publication.getID() + " entity type not equal to Project")));

        publication = reload(publication);

        assertThat(getMetadataValues(publication, "oairecerif.project.startDate"), empty());
        assertThat(getMetadataValues(publication, "oairecerif.project.endDate"), empty());
        assertThat(getMetadataValues(publication, "synsicris.duration"), empty());

    }

    @Test
    public void testProjectItemCalculationEnhancementWithProjectWithNoCurrency() throws Exception {

        context.turnOffAuthorisationSystem();

//        project item doesn't contain currency
        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmount("200")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("300")
                                    .build();

        funding1 = reload(funding1);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), empty());

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), hasSize(2));
        assertThat(runnableHandler.getErrorMessages(), containsInAnyOrder(
            containsString("An error occurs during enhancement. The process is aborted"),
            containsString("item:" + project.getID() + " doesn't contain currency")));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), empty());

    }

    @Test
    public void testProjectItemCalculationEnhancementWithWrongEntityType() throws Exception {

        context.turnOffAuthorisationSystem();

        Item publication = ItemBuilder.createItem(context, collection)
                                      .withEntityType("Publication")
                                      .withTitle("Test Publication")
                                      .withAmountCurrency("EUR")
                                      .withAmount("200")
                                      .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("200")
                                    .build();

        funding1 = reload(funding1);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, publication.getName(),
            publication.getID().toString(), 600);

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), hasSize(2));
        assertThat(runnableHandler.getErrorMessages(), containsInAnyOrder(
            containsString("An error occurs during enhancement. The process is aborted"),
            containsString("item:" + publication.getID() + " entity type not equal to Project")));

    }

    @Test
    public void testProjectItemCalculationEnhancementRelatedItemsWithDifferentCurrencies() throws Exception {

        context.turnOffAuthorisationSystem();

        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmountCurrency("EUR")
                                  .withAmount("100")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("200")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .withAmountCurrency("USD")
                                    .withAmount("300")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Publication")
                                    .withTitle("Test Publication")
                                    .withAmountCurrency("USD")
                                    .withAmount("300")
                                    .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

    }

    @Test
    public void testProjectItemCalculationEnhancementWithRelatedItemWithNoCurrency() throws Exception {

        context.turnOffAuthorisationSystem();

        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmountCurrency("EUR")
                                  .withAmount("100")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("200")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .withAmount("300")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                       .withEntityType("Publication")
                                       .withTitle("Test Publication")
                                       .withAmountCurrency("USD")
                                       .withAmount("300")
                                       .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

    }

    @Test
    public void testProjectItemCalculationEnhancementWithRelatedItemWithNotValidAmount() throws Exception {

        context.turnOffAuthorisationSystem();


        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmountCurrency("EUR")
                                  .withAmount("100")
                                  .build();

        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("200")
                                    .build();

        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .withAmountCurrency("EUR")
                                    .withAmount("2xx")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                       .withEntityType("Publication")
                                       .withTitle("Test Publication")
                                       .withAmountCurrency("USD")
                                       .withAmount("300")
                                       .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), hasSize(2));
        assertThat(runnableHandler.getErrorMessages(), containsInAnyOrder(
            containsString("An error occurs during enhancement. The process is aborted"),
            containsString("item:" + funding2.getID() + " contains incorrect amount value")));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

    }

    @Test
    public void testProjectItemCalculationEnhancementWithoutForce() throws Exception {

        context.turnOffAuthorisationSystem();

//        project item contains currency 'EUR' and amount with 100
        Item project = ItemBuilder.createItem(context, collection)
                                  .withEntityType("Project")
                                  .withTitle("Test Project")
                                  .withAmountCurrency("EUR")
                                  .withAmount("100")
                                  .build();

//      funding contains amount with 200
        Item funding1  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 1")
                                    .withAmountCurrency("EUR")
                                    .withAmount("200")
                                    .build();

//      funding contains amount with 300
        Item funding2  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 2")
                                    .withAmountCurrency("EUR")
                                    .withAmount("300")
                                    .build();

//      funding contains an empty amount
        Item funding3  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 3")
                                    .withAmountCurrency("EUR")
                                    .withAmount("")
                                    .build();

//      funding doesn't contain amount
        Item funding4  = ItemBuilder.createItem(context, collection)
                                    .withEntityType("Funding")
                                    .withTitle("Test Funding 4")
                                    .withAmountCurrency("EUR")
                                    .build();

        Item publication  = ItemBuilder.createItem(context, collection)
                                       .withEntityType("Publication")
                                       .withTitle("Test Publication")
                                       .withAmountCurrency("USD")
                                       .withAmount("300")
                                       .build();

        funding1 = reload(funding1);
        funding2 = reload(funding2);
        funding3 = reload(funding3);
        funding4 = reload(funding4);
        publication = reload(publication);

        itemService.addMetadata(context, funding1, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding2, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding3, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, funding4, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);
        itemService.addMetadata(context, publication, "synsicris", "relation", "project", null, project.getName(),
            project.getID().toString(), 600);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("100"));

        context.commit();

        TestDSpaceRunnableHandler runnableHandler = runScript(false);

        assertThat(runnableHandler.getErrorMessages(), empty());
        assertThat(runnableHandler.getInfoMessages(), contains("Enhancement completed with success"));

        project = reload(project);

        assertThat(getMetadataValues(project, "oairecerif.amount.currency"), hasSize(1));
        assertThat(getMetadataValues(project, "oairecerif.amount"), hasSize(1));

        assertThat(getMetadataValues(project, "oairecerif.amount.currency").get(0).getValue(), is("EUR"));
        assertThat(getMetadataValues(project, "oairecerif.amount").get(0).getValue(), is("500"));

    }

    private TestDSpaceRunnableHandler runScript(boolean force) throws InstantiationException, IllegalAccessException {
        TestDSpaceRunnableHandler runnableHandler = new TestDSpaceRunnableHandler();
        String[] args = force ? new String[] { "item-enhancer", "-f" } : new String[] { "item-enhancer" };
        ScriptLauncher.handleScript(args, ScriptLauncher.getConfig(kernelImpl), runnableHandler, kernelImpl);
        return runnableHandler;
    }

    @SuppressWarnings("rawtypes")
    private <T extends ReloadableEntity> T reload(T entity) throws SQLException, AuthorizeException {
        return context.reloadEntity(entity);
    }

    private void replaceMetadata(Item item, String schema, String element, String qualifier, String newValue)
        throws SQLException, AuthorizeException {
        itemService.replaceMetadata(context, reload(item), schema, element, qualifier, ANY, newValue, null, -1, 0);
    }

    private List<MetadataValue> getMetadataValues(Item item, String metadataField) {
        return itemService.getMetadataByMetadataString(item, metadataField);
    }

    private List<MetadataValue> getMetadataValues(WorkspaceItem item, String metadataField) {
        return itemService.getMetadataByMetadataString(item.getItem(), metadataField);
    }

}
