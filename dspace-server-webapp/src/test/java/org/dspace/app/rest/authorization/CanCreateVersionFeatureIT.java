/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.SQLException;

import org.dspace.app.rest.authorization.impl.CanCreateVersionFeature;
import org.dspace.app.rest.converter.CollectionConverter;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.model.CollectionRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for the canCreateVersion authorization feature.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class CanCreateVersionFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private CollectionConverter collectionConverter;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    private AuthorizationFeature canCreateVersionFeature;

    private EPerson projectAdmin;

    @Before
    public void setup() throws Exception {
        canCreateVersionFeature = authorizationFeatureService.find(CanCreateVersionFeature.NAME);

        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
            .withName("Parent community")
            .build();

        projectAdmin = EPersonBuilder.createEPerson(context)
            .withEmail("projectAdmnin@test.it")
            .build();

        context.restoreAuthSystemState();
    }

    @Test
    public void testWithoutItemRest() throws SQLException {

        configurationService.setProperty("versioning.enabled", true);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", PROJECT_ENTITY);

        createProjectAdminGroup(parentCommunity, projectAdmin);
        context.setCurrentUser(projectAdmin);

        context.restoreAuthSystemState();

        CollectionRest collectionRest = collectionConverter.convert(collection, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, collectionRest), is(false));

    }

    @Test
    public void testWithVersioningDisabled() throws SQLException {

        configurationService.setProperty("versioning.enabled", false);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", PROJECT_ENTITY);

        Item item = createItem("Item", collection);

        createProjectAdminGroup(parentCommunity, projectAdmin);
        context.setCurrentUser(projectAdmin);

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(false));

    }

    @Test
    public void testWithoutParentProject() throws SQLException {

        configurationService.setProperty("versioning.enabled", true);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", "Publication");

        Item item = createItem("Item", collection);

        createProjectAdminGroup(parentCommunity, projectAdmin);
        context.setCurrentUser(projectAdmin);

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(false));

    }

    @Test
    public void testWithoutProjectAdminGroup() throws SQLException {

        configurationService.setProperty("versioning.enabled", true);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", PROJECT_ENTITY);

        Item item = createItem("Item", collection);

        context.setCurrentUser(projectAdmin);

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(false));

    }

    @Test
    public void testWithNoProjectAdmin() throws SQLException {

        configurationService.setProperty("versioning.enabled", true);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", PROJECT_ENTITY);

        Item item = createItem("Item", collection);

        createProjectAdminGroup(parentCommunity, projectAdmin);
        context.setCurrentUser(eperson);

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(false));

    }

    @Test
    public void testWithVersionItem() throws SQLException {

        configurationService.setProperty("versioning.enabled", true);

        context.turnOffAuthorisationSystem();

        Collection collection = createCollection("Collection", PROJECT_ENTITY);

        Item item = ItemBuilder.createItem(context, collection)
            .withTitle("Item")
            .withUniqueId("unique-id")
            .build();

        createProjectAdminGroup(parentCommunity, projectAdmin);
        context.setCurrentUser(projectAdmin);

        context.restoreAuthSystemState();

        ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
        assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(false));

    }

    @Test
    public void testIsAuthorized() throws SQLException {

        String parentCommId =
            configurationService.getProperty("project.parent-community-id", null);
        try {
            configurationService.setProperty("versioning.enabled", true);

            context.turnOffAuthorisationSystem();

            Community joinProjects = createCommunity("Joint projects");
            configurationService.setProperty("project.parent-community-id", joinProjects.getID().toString());

            Community testProjects = createSubCommunity("Test Projects", joinProjects);
            Collection collection = createCollection("Projects", PROJECT_ENTITY, testProjects);

            Item item = createItem("Item", collection);

            createProjectCoordinatorsGroup(testProjects, projectAdmin);
            context.setCurrentUser(projectAdmin);

            context.restoreAuthSystemState();

            ItemRest itemRest = itemConverter.convert(item, Projection.DEFAULT);
            assertThat(canCreateVersionFeature.isAuthorized(context, itemRest), is(true));
        } finally {
            configurationService.setProperty("project.parent-community-id", parentCommId);
        }

    }

    private Collection createCollection(String name, String entityType) {
        return CollectionBuilder.createCollection(context, parentCommunity)
            .withName(name)
            .withEntityType(entityType)
            .build();
    }

    private Collection createCollection(String name, String entityType, Community parent) {
        return CollectionBuilder.createCollection(context, parent)
            .withName(name)
            .withEntityType(entityType)
            .build();
    }

    private Community createCommunity(String name) {
        return CommunityBuilder.createCommunity(context)
            .withName(name)
            .build();
    }

    private Community createSubCommunity(String name, Community subCommunity) {
        return CommunityBuilder.createSubCommunity(context, subCommunity)
            .withName(name)
            .build();
    }

    private Item createItem(String title, Collection collection) {
        return ItemBuilder.createItem(context, collection)
            .withTitle(title)
            .build();
    }

    private void createProjectAdminGroup(Community community, EPerson ePerson) {
        GroupBuilder.createGroup(context)
            .withName("project_" + community.getID() + "_admin_group")
            .addMember(ePerson)
            .build();
    }

    private void createProjectCoordinatorsGroup(Community community, EPerson ePerson) {
        GroupBuilder.createGroup(context)
        .withName(
            String.format(
                ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE,
                community.getID().toString()
                )
            )
        .addMember(ePerson)
        .build();
    }

}
