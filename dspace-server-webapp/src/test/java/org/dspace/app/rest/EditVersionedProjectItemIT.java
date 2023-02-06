/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.dspace.project.util.ProjectConstants.MD_LAST_VERSION;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_VISIBLE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

import org.dspace.app.rest.model.patch.AddOperation;
import org.dspace.app.rest.model.patch.Operation;
import org.dspace.app.rest.security.EditProjectItemRestPermissionEvaluatorPlugin;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.RelationshipTypeBuilder;
import org.dspace.builder.VersionBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.RelationshipType;
import org.dspace.content.edit.EditItem;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.versioning.ItemVersionProvider;
import org.dspace.versioning.ProjectVersionProvider;
import org.dspace.versioning.Version;
import org.dspace.versioning.VersioningServiceImpl;
import org.dspace.versioning.service.VersioningService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Integration test for authorization on {@link EditItemRestRepository} using the linked
 * {@link EditProjectItemRestPermissionEvaluatorPlugin}.
 * This IT uses the Synsicris' versioning behavior, by configuring the {@link ProjectVersionProvider} as
 * provider for the {@link VersioningServiceImpl}.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class EditVersionedProjectItemIT extends AbstractControllerIntegrationTest {

    private static final String VERSION_METADATA_TO_PATCH = "project.version.metadata-to-patch";

    private static ConfigurableListableBeanFactory beanFactory;
    private static VersioningService versionServiceBean;
    private static ItemVersionProvider itemVersionProviderBean;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    @BeforeClass
    public static void initConfig() {
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
    public static void restoreConfig() {
        // WARN: This code resets the provider of the `versionServiceBean`
        ((VersioningServiceImpl) versionServiceBean).setProvider(itemVersionProviderBean);
    }

    @Override
    @After
    public void destroy() throws Exception {
        configurationService.setProperty(VERSION_METADATA_TO_PATCH , null);
        super.destroy();
    }


    @Test
    public void patchVersionedPublicationItemByNotAdminTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson userA =
                EPersonBuilder.createEPerson(context)
                        .withNameInMetadata("Simone", "Proni")
                        .withEmail("user.b@example.com")
                        .withPassword(password)
                        .build();

        Group groupA =
                GroupBuilder.createGroup(context)
                        .withName("Group A")
                        .addMember(userA)
                        .build();

        parentCommunity =
                CommunityBuilder.createCommunity(context)
                        .withName("Parent Community")
                        .build();

        // create publication collection
        Collection col1 =
                CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("Publication")
                        .withName("Collection 1")
                        .withSubmissionDefinition("modeA")
                        .build();

        Item itemA =
                ItemBuilder.createItem(context, col1)
                        .withIssueDate("2015-06-25")
                        .withAuthor("Mykhaylo, Boychuk")
                        .build();

        itemService.addMetadata(
                context, itemA,"cris", "policy", "group", null, groupA.getName(),groupA.getID().toString(), 600
        );

        createIsVersionRelationshipType("Publication");

        // create new version
        Version version =
                VersionBuilder.createVersion(context, itemA, "test")
                        .build();

        EditItem editItem = new EditItem(context, version.getItem());

        context.restoreAuthSystemState();

        List<Operation> addTitle = new ArrayList<>();
        List<Map<String, String>> values = new ArrayList<>();
        values.add(Map.of("value", "New Title"));
        addTitle.add(new AddOperation("/sections/titleAndIssuedDate/dc.title", values));

        String patchBody = getPatchContent(addTitle);

        String authToken = getAuthToken(userA.getEmail(), password);
        getClient(authToken).perform(patch("/api/core/edititems/" + editItem.getID() + ":FIRST-CUSTOM")
                        .content(patchBody)
                        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchVersionedPublicationItemByAdminTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity =
                CommunityBuilder.createCommunity(context)
                        .withName("Parent Community")
                        .build();

        Collection col1 =
                CollectionBuilder.createCollection(context, parentCommunity)
                        .withEntityType("Publication")
                        .withName("Collection 1")
                        .withSubmissionDefinition("modeA")
                        .build();

        Item itemA =
                ItemBuilder.createItem(context, col1)
                        .withIssueDate("2015-06-25")
                        .withAuthor("Mykhaylo, Boychuk")
                        .build();

        createIsVersionRelationshipType("Publication");

        // create new version
        Version version =
                VersionBuilder.createVersion(context, itemA, "test")
                        .build();

        EditItem editItem = new EditItem(context, version.getItem());

        context.restoreAuthSystemState();

        List<Operation> addTitle = new ArrayList<>();
        List<Map<String, String>> values = new ArrayList<>();
        values.add(Map.of("value", "New Title"));
        addTitle.add(new AddOperation("/sections/titleAndIssuedDate/dc.title", values));

        String patchBody = getPatchContent(addTitle);

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(patch("/api/core/edititems/" + editItem.getID() + ":FIRST")
                        .content(patchBody)
                        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON))
                .andExpect(jsonPath("$.sections.titleAndIssuedDate", Matchers.allOf(
                        hasJsonPath("$['dc.title'][0].value", is("New Title")),
                        hasJsonPath("$['dc.date.issued'][0].value", is("2015-06-25"))
                )));

        // verify that the patch changes have been persisted
        getClient(tokenAdmin).perform(get("/api/core/edititems/" + editItem.getID() + ":FIRST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections.titleAndIssuedDate", Matchers.allOf(
                        hasJsonPath("$['dc.title'][0].value", is("New Title")),
                        hasJsonPath("$['dc.date.issued'][0].value", is("2015-06-25"))
                )));
    }

    @Test
    public void patchVersionedProjectItemByNotAdminNotAllowedExistValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson userA =
                EPersonBuilder.createEPerson(context)
                        .withNameInMetadata("Simone", "Proni")
                        .withEmail("user.b@example.com")
                        .withPassword(password)
                        .build();

        Group groupA =
                GroupBuilder.createGroup(context)
                        .withName("Group A")
                        .addMember(userA)
                        .build();

        Community projectCommunity =
                CommunityBuilder.createCommunity(context)
                        .withName("project's community")
                        .build();

        Community projectACommunity =
                CommunityBuilder.createSubCommunity(context, projectCommunity)
                        .withName("project a community")
                        .build();

        Collection col1 =
                CollectionBuilder.createCollection(context, projectACommunity)
                        .withEntityType(PROJECT_ENTITY)
                        .withSubmissionDefinition("projects_versioning-edit")
                        .withName("Collection 1")
                        .build();

        Item itemA =
                ItemBuilder.createItem(context, col1)
                        .withTitle("Title of item A")
                        .withIssueDate("2015-06-25")
                        .build();


        itemService.addMetadata(
                context, itemA, "synsicris", "versioning-edit-policy", "group",
                null, groupA.getName(), groupA.getID().toString(), 600
        );

        createIsVersionRelationshipType(PROJECT_ENTITY);

        // create new version
        Version version =
                VersionBuilder.createVersion(context, itemA, "test")
                        .build();

        Item versionedItem = context.reloadEntity(version.getItem());

        itemService.setMetadataSingleValue(context, versionedItem, MD_VERSION_VISIBLE, null, "true");

        EditItem editItem = new EditItem(context, versionedItem);

        configurationService.setProperty(
                VERSION_METADATA_TO_PATCH,
                List.of(
                        MD_LAST_VERSION.toString()
                )
        );

        context.restoreAuthSystemState();

        List<Operation> addTitle = new ArrayList<>();
        List<Map<String, String>> values = new ArrayList<>();
        values.add(Map.of("value", "false"));
        addTitle.add(new AddOperation("/sections/projects_versioning/synsicris.version.visible", values));

        String patchBody = getPatchContent(addTitle);

        String authToken = getAuthToken(userA.getEmail(), password);
        getClient(authToken).perform(patch("/api/core/edititems/" + editItem.getID() + ":VERSIONING-CUSTOM")
                        .content(patchBody)
                        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchVersionedProjectItemByNotAdminAndAllowedExistValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson userA =
                EPersonBuilder.createEPerson(context)
                        .withNameInMetadata("Simone", "Proni")
                        .withEmail("user.b@example.com")
                        .withPassword(password)
                        .build();

        Group groupA =
                GroupBuilder.createGroup(context)
                        .withName("Group A")
                        .addMember(userA)
                        .build();

        Community projectCommunity =
                CommunityBuilder.createCommunity(context)
                        .withName("project's community")
                        .build();

        Community projectACommunity =
                CommunityBuilder.createSubCommunity(context, projectCommunity)
                        .withName("project a community")
                        .build();

        Collection col1 =
                CollectionBuilder.createCollection(context, projectACommunity)
                        .withEntityType(PROJECT_ENTITY)
                        .withSubmissionDefinition("projects_versioning-edit")
                        .withName("Collection 1")
                        .build();

        Item itemA =
                ItemBuilder.createItem(context, col1)
                        .withTitle("Title of item A")
                        .withIssueDate("2015-06-25")
                        .build();


        itemService.addMetadata(
                context, itemA, "synsicris", "versioning-edit-policy", "group",
                null, groupA.getName(), groupA.getID().toString(), 600
        );

        createIsVersionRelationshipType(PROJECT_ENTITY);

        // create new version
        Version version =
                VersionBuilder.createVersion(context, itemA, "test")
                        .build();

        Item versionedItem = context.reloadEntity(version.getItem());

        itemService.setMetadataSingleValue(context, versionedItem, MD_VERSION_VISIBLE, null, "true");

        EditItem editItem = new EditItem(context, versionedItem);

        configurationService.setProperty(
                VERSION_METADATA_TO_PATCH,
                List.of(
                        MD_VERSION_VISIBLE.toString()
                )
        );

        context.restoreAuthSystemState();

        List<Operation> addTitle = new ArrayList<>();
        List<Map<String, String>> values = new ArrayList<>();
        values.add(Map.of("value", "false"));
        addTitle.add(new AddOperation("/sections/projects_versioning/synsicris.version.visible", values));

        String patchBody = getPatchContent(addTitle);

        String authToken = getAuthToken(userA.getEmail(), password);
        getClient(authToken).perform(patch("/api/core/edititems/" + editItem.getID() + ":VERSIONING-CUSTOM")
                        .content(patchBody)
                        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections.projects_versioning", Matchers.allOf(
                        hasJsonPath("$['synsicris.version.visible'][0].value", is("false"))
                )));

        // verify that the patch changes have been persisted
        getClient(authToken).perform(get("/api/core/edititems/" + editItem.getID() + ":VERSIONING-CUSTOM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections.projects_versioning", Matchers.allOf(
                        hasJsonPath("$['synsicris.version.visible'][0].value", is("false"))
                )));
    }

    @Test
    public void patchVersionedProjectItemByAdminNotAllowedExistValueTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Community projectCommunity =
                CommunityBuilder.createCommunity(context)
                        .withName("project's community")
                        .build();

        Community projectACommunity =
                CommunityBuilder.createSubCommunity(context, projectCommunity)
                        .withName("project a community")
                        .build();

        Collection col1 =
                CollectionBuilder.createCollection(context, projectACommunity)
                        .withEntityType(PROJECT_ENTITY)
                        .withSubmissionDefinition("projects_versioning-edit")
                        .withName("Collection 1")
                        .build();

        Item itemA =
                ItemBuilder.createItem(context, col1)
                        .withTitle("Title of item A")
                        .withIssueDate("2015-06-25")
                        .build();

        createIsVersionRelationshipType(PROJECT_ENTITY);

        // create new version
        Version version =
                VersionBuilder.createVersion(context, itemA, "test")
                        .build();

        Item versionedItem = context.reloadEntity(version.getItem());

        itemService.setMetadataSingleValue(context, versionedItem, MD_VERSION_VISIBLE, null, "true");

        EditItem editItem = new EditItem(context, versionedItem);

        configurationService.setProperty(
                VERSION_METADATA_TO_PATCH,
                List.of(
                        MD_LAST_VERSION.toString()
                )
        );

        context.restoreAuthSystemState();

        List<Operation> addTitle = new ArrayList<>();
        List<Map<String, String>> values = new ArrayList<>();
        values.add(Map.of("value", "false"));
        addTitle.add(new AddOperation("/sections/projects_versioning/synsicris.version.visible", values));

        String patchBody = getPatchContent(addTitle);

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(patch("/api/core/edititems/" + editItem.getID() + ":VERSIONING-ADMIN")
                        .content(patchBody)
                        .contentType(MediaType.APPLICATION_JSON_PATCH_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections.projects_versioning", Matchers.allOf(
                        hasJsonPath("$['synsicris.version.visible'][0].value", is("false"))
                )));

        // verify that the patch changes have been persisted
        getClient(tokenAdmin).perform(get("/api/core/edititems/" + editItem.getID() + ":VERSIONING-ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections.projects_versioning", Matchers.allOf(
                        hasJsonPath("$['synsicris.version.visible'][0].value", is("false"))
                )));
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
