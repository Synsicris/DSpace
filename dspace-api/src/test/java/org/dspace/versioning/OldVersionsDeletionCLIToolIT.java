/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.versioning;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_OFFICIAL;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dspace.AbstractIntegrationTestWithDatabase;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.RelationshipTypeBuilder;
import org.dspace.builder.VersionBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.RelationshipType;
import org.dspace.content.authority.Choices;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.versioning.factory.VersionServiceFactory;
import org.dspace.versioning.service.VersionHistoryService;
import org.dspace.versioning.service.VersioningService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 *
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class OldVersionsDeletionCLIToolIT extends AbstractIntegrationTestWithDatabase {

    private static ConfigurableListableBeanFactory beanFactory;
    private static VersioningService versionServiceBean;
    private static ItemVersionProvider itemVersionProviderBean;


    private Community projectCommunity;
    private Community projectACommunity;
    private Community projectBCommunity;

    private Collection collection;
    private Collection collectionA;
    private Collection collectionB;

    private Item projectItem;
    private Item projectAItem;
    private Item projectBItem;

    private ConfigurationService configurationService;
    private CommunityService communityService;
    private ItemService itemService;
    private VersioningService versioningService;
    private VersionHistoryService versionHistoryService;

    @BeforeClass
    public static void configure() {
        // WARN: This code sets the `projectItemVersionProvider` as main provider
        // for the `versionServiceBean`.
        beanFactory =
            DSpaceServicesFactory.getInstance().getServiceManager().getApplicationContext().getBeanFactory();

        versionServiceBean = (VersioningService) beanFactory.getBean(VersioningService.class.getCanonicalName());
        itemVersionProviderBean = ((VersioningServiceImpl) versionServiceBean).getProvider();
        ((VersioningServiceImpl) versionServiceBean).setProvider(
            beanFactory.getBean("projectItemVersionProvider", ProjectVersionProvider.class)
        );
    }

    @AfterClass
    public static void reset() {
        // WARN: This code resets the provider of the `versionServiceBean`
        ((VersioningServiceImpl) versionServiceBean).setProvider(itemVersionProviderBean);
    }

    @Before
    public void setup() throws Exception {

        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        communityService = ContentServiceFactory.getInstance().getCommunityService();
        itemService = ContentServiceFactory.getInstance().getItemService();
        versioningService = VersionServiceFactory.getInstance().getVersionService();
        versionHistoryService = VersionServiceFactory.getInstance().getVersionHistoryService();

        context.turnOffAuthorisationSystem();

        projectCommunity =
            CommunityBuilder.createCommunity(context)
                            .withName("project's community")
                            .build();

        collection = CollectionBuilder.createCollection(context, projectCommunity)
                                      .withName("collection")
                                      .withEntityType("Project")
                                      .build();

        projectItem =
            ItemBuilder.createItem(context, collection)
                       .withTitle("projects")
                       .build();

        projectACommunity =
            CommunityBuilder.createSubCommunity(context, projectCommunity)
                            .withName("project a community")
                            .build();

        collectionA =
            CollectionBuilder.createCollection(context, projectACommunity)
                             .withName("collection a")
                             .withEntityType("Project")
                             .build();

        projectAItem =
            ItemBuilder.createItem(context, collectionA)
                       .withTitle("project a item")
                       .build();

        projectBCommunity =
            CommunityBuilder.createSubCommunity(context, projectCommunity)
                            .withName("project b community")
                            .build();

        collectionB =
            CollectionBuilder.createCollection(context, projectBCommunity)
                             .withName("collection b")
                             .withEntityType("Project")
                             .build();

        projectBItem =
            ItemBuilder.createItem(context, collectionB)
                       .withTitle("project b item")
                       .build();

        communityService
            .addMetadata(context, projectCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, projectItem.getID().toString(), projectItem.getID().toString(), Choices.CF_ACCEPTED);

        communityService
            .addMetadata(context, projectACommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, projectAItem.getID().toString(), projectAItem.getID().toString(), Choices.CF_ACCEPTED);

        communityService
            .addMetadata(context, projectBCommunity, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier,
                null, projectBItem.getID().toString(), projectBItem.getID().toString(), Choices.CF_ACCEPTED);

        configurationService.setProperty("project.parent-community-id", projectCommunity.getID());
        configurationService.setProperty("versioning.delete.threshold", 2);

        context.restoreAuthSystemState();
    }

    @After
    public void tearDown() throws Exception {
        configurationService.setProperty("project.parent-community-id", null);
        configurationService.setProperty("versioning.delete.threshold", null);
    }

    @Test
    public void testDeleteOldVersionsGivenAllVersionsIsOfficial() throws Exception {

        context.turnOffAuthorisationSystem();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        Version versionOne =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        Version versionTwo =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        Version versionThree =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        Item versionedItemOne = context.reloadEntity(versionOne.getItem());
        Item versionedItemTwo = context.reloadEntity(versionTwo.getItem());
        Item versionedItemThree = context.reloadEntity(versionThree.getItem());

        itemService.addMetadata(
            context, versionedItemOne, MD_VERSION_OFFICIAL.schema, MD_VERSION_OFFICIAL.element,
            MD_VERSION_OFFICIAL.qualifier, null, Boolean.TRUE.toString()
        );
        itemService.update(context, versionedItemOne);

        itemService.addMetadata(
            context, versionedItemTwo, MD_VERSION_OFFICIAL.schema, MD_VERSION_OFFICIAL.element,
            MD_VERSION_OFFICIAL.qualifier, null, Boolean.TRUE.toString()
        );
        itemService.update(context, versionedItemTwo);

        itemService.addMetadata(
            context, versionedItemThree, MD_VERSION_OFFICIAL.schema, MD_VERSION_OFFICIAL.element,
            MD_VERSION_OFFICIAL.qualifier, null, Boolean.TRUE.toString()
        );
        itemService.update(context, versionedItemThree);

        context.commit();
        context.restoreAuthSystemState();

        // before running the script
        VersionHistory versionHistoryA = versionHistoryService.findByItem(context, projectAItem);
        List<Version> versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 4);

        runDSpaceScript("delete-old-versions");

        // after running the script all versions will not be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);
        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 4);

    }

    @Test
    public void testDeleteOldVersionsGivenAllVersionsLessThanThreshold() throws Exception {

        configurationService.setProperty("versioning.delete.threshold", 5);

        context.turnOffAuthorisationSystem();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        VersionBuilder.createVersion(context, projectAItem, "test").build();
        VersionBuilder.createVersion(context, projectAItem, "test").build();
        VersionBuilder.createVersion(context, projectAItem, "test").build();

        context.commit();
        context.restoreAuthSystemState();

        // before running the script
        VersionHistory versionHistoryA = versionHistoryService.findByItem(context, projectAItem);
        List<Version> versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 4);

        runDSpaceScript("delete-old-versions");

        // after running the script all versions will not be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);
        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 4);

    }

    @Test
    public void testDeleteOldVersionsGivenAllVersionsIsNotOfficial() throws Exception {

        context.turnOffAuthorisationSystem();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        VersionBuilder.createVersion(context, projectAItem, "test").build();
        Version versionATwo = VersionBuilder.createVersion(context, projectAItem, "test").build();
        Version versionAThree =
            VersionBuilder.createVersion(context, projectAItem, "test")
                .build();

        VersionBuilder.createVersion(context, projectBItem, "test").build();
        VersionBuilder.createVersion(context, projectBItem, "test").build();
        Version versionBThree = VersionBuilder.createVersion(context, projectBItem, "test").build();
        Version versionBFour =
            VersionBuilder.createVersion(context, projectBItem, "test")
                .build();

        context.commit();
        context.restoreAuthSystemState();

        // before running the script
        VersionHistory versionHistoryA = versionHistoryService.findByItem(context, projectAItem);
        VersionHistory versionHistoryB = versionHistoryService.findByItem(context, projectBItem);

        List<Version> versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);
        List<Version> versionsOfB = versioningService.getVersionsByHistory(context, versionHistoryB);

        assertEquals(4, versionsOfA.size());
        assertEquals(5, versionsOfB.size());


        runDSpaceScript("delete-old-versions");

        // after running the script, the not-official versions will be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);
        versionHistoryB = context.reloadEntity(versionHistoryB);

        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);
        versionsOfB = versioningService.getVersionsByHistory(context, versionHistoryB);

        // 3 Versions:
        //  - 1 ProjectItem;
        //  - 2 Not-Official versions.
        assertEquals(3, versionsOfA.size());
        // 3 Versions:
        //  - 1 ProjectItem:
        //  - 2 Not-Official versions.
        assertEquals(3, versionsOfB.size());

        assertEquals(versionAThree.getItem().getID(), versionsOfA.get(0).getItem().getID());
        assertEquals(versionATwo.getItem().getID(), versionsOfA.get(1).getItem().getID());
        assertEquals(projectAItem.getID(), versionsOfA.get(2).getItem().getID());

        assertEquals(versionBFour.getItem().getID(), versionsOfB.get(0).getItem().getID());
        assertEquals(versionBThree.getItem().getID(), versionsOfB.get(1).getItem().getID());
        assertEquals(projectBItem.getID(), versionsOfB.get(2).getItem().getID());

    }

    @Test
    public void testDeleteOldVersionsGivenOfficialAndNotOfficialVersions() throws Exception {

        context.turnOffAuthorisationSystem();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        Version versionOne =
            VersionBuilder.createVersion(context, projectAItem, "test")
                .build();
        // deleted
        VersionBuilder.createVersion(context, projectAItem, "test")
            .build();

        Version versionThree = VersionBuilder.createVersion(context, projectAItem, "test").build();
        Version versionFour = VersionBuilder.createVersion(context, projectAItem, "test").build();

        Item versionedItemOne = context.reloadEntity(versionOne.getItem());
        Item versionedItemThree = context.reloadEntity(versionThree.getItem());
        Item versionedItemFour = context.reloadEntity(versionFour.getItem());

        // 4 Versions:
        //  - 1 Official;
        //  - 3 Not-Official;
        itemService.addMetadata(
            context, versionedItemOne, MD_VERSION_OFFICIAL.schema, MD_VERSION_OFFICIAL.element,
            MD_VERSION_OFFICIAL.qualifier, null, "true");
        itemService.update(context, versionedItemOne);

        context.commit();
        context.restoreAuthSystemState();

        // before running the script
        VersionHistory versionHistoryA = versionHistoryService.findByItem(context, projectAItem);

        List<Version> versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        // 4 Versions + Base item.
        assertEquals(5, versionsOfA.size());

        runDSpaceScript("delete-old-versions");

        // after running the script all not official versions will be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);

        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(4, versionsOfA.size());

        assertEquals(versionedItemFour.getID(), versionsOfA.get(0).getItem().getID());
        assertEquals(versionedItemThree.getID(), versionsOfA.get(1).getItem().getID());
        assertEquals(versionedItemOne.getID(), versionsOfA.get(2).getItem().getID());
        assertEquals(projectAItem.getID(), versionsOfA.get(3).getItem().getID());

    }

    private RelationshipType createIsVersionRelationshipType(String entityType) {

        EntityType type =
            EntityTypeBuilder.createEntityTypeBuilder(context, entityType)
                             .build();

        return RelationshipTypeBuilder
            .createRelationshipTypeBuilder(context, type, type, "isVersionOf", "hasVersion", 0, 1, 0, null)
            .build();
    }

}
