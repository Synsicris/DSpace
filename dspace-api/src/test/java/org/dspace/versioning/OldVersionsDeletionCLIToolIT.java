package org.dspace.versioning;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
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
import org.junit.Before;
import org.junit.Test;

public class OldVersionsDeletionCLIToolIT extends AbstractIntegrationTestWithDatabase {

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

        itemService.addMetadata(context, versionedItemOne, "synsicris", "version", "official", null, "");
        itemService.update(context, versionedItemOne);

        itemService.addMetadata(context, versionedItemTwo, "synsicris", "version", "official", null, "");
        itemService.update(context, versionedItemTwo);

        itemService.addMetadata(context, versionedItemThree, "synsicris", "version", "official", null, "");
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
        VersionBuilder.createVersion(context, projectAItem, "test").build();
        Version versionAThree =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        VersionBuilder.createVersion(context, projectBItem, "test").build();
        VersionBuilder.createVersion(context, projectBItem, "test").build();
        VersionBuilder.createVersion(context, projectBItem, "test").build();
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

        assertEquals(versionsOfA.size(), 4);
        assertEquals(versionsOfB.size(), 5);


        runDSpaceScript("delete-old-versions");

        // after running the script, the not-official versions will be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);
        versionHistoryB = context.reloadEntity(versionHistoryB);

        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);
        versionsOfB = versioningService.getVersionsByHistory(context, versionHistoryB);

        assertEquals(versionsOfA.size(), 2);
        assertEquals(versionsOfB.size(), 2);

        assertEquals(versionsOfA.get(0).getItem().getID(), versionAThree.getItem().getID());
        assertEquals(versionsOfA.get(1).getItem().getID(), projectAItem.getID());

        assertEquals(versionsOfB.get(0).getItem().getID(), versionBFour.getItem().getID());
        assertEquals(versionsOfB.get(1).getItem().getID(), projectBItem.getID());

    }

    @Test
    public void testDeleteOldVersionsGivenOfficialAndNotOfficialVersions() throws Exception {

        context.turnOffAuthorisationSystem();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        Version versionOne =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        Version versionTwo =
            VersionBuilder.createVersion(context, projectAItem, "test")
                          .build();

        VersionBuilder.createVersion(context, projectAItem, "test").build();

        Item versionedItemOne = context.reloadEntity(versionOne.getItem());
        Item versionedItemTwo = context.reloadEntity(versionTwo.getItem());


        itemService.addMetadata(context, versionedItemOne, "synsicris", "version", "official", null, "");
        itemService.update(context, versionedItemOne);

        itemService.addMetadata(context, versionedItemTwo, "synsicris", "version", "official", null, "");
        itemService.update(context, versionedItemTwo);

        context.commit();
        context.restoreAuthSystemState();

        // before running the script
        VersionHistory versionHistoryA = versionHistoryService.findByItem(context, projectAItem);

        List<Version> versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 4);

        runDSpaceScript("delete-old-versions");

        // after running the script all not official versions will be deleted
        versionHistoryA = context.reloadEntity(versionHistoryA);

        versionsOfA = versioningService.getVersionsByHistory(context, versionHistoryA);

        assertEquals(versionsOfA.size(), 3);

        assertEquals(versionsOfA.get(0).getItem().getID(), versionedItemTwo.getID());
        assertEquals(versionsOfA.get(1).getItem().getID(), versionedItemOne.getID());
        assertEquals(versionsOfA.get(2).getItem().getID(), projectAItem.getID());

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
