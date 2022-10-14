/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.dspace.project.util.ProjectConstants.PROGRAMME_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.submit.consumer.LinkProgrammeGroupWithProjectConsumer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test against {@link LinkProgrammeGroupWithProjectConsumer}.
 * 
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 */
public class LinkProgrammeGroupWithProjectConsumerIT extends AbstractControllerIntegrationTest {

    private Collection projectCollection;
    private Collection programmeCollection;
    private Community projectCommunity;

    @Autowired
    private GroupService groupService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("community").build();

        programmeCollection = CollectionBuilder.createCollection(context, parentCommunity)
                                               .withName("programme collection")
                                               .withEntityType("programme")
                                               .build();

        projectCommunity = CommunityBuilder.createCommunity(context)
                                           .withName("project's community").build();

        projectCollection = CollectionBuilder.createCollection(context, projectCommunity)
                                      .withName("project collection")
                                      .withEntityType("Project")
                                      .build();

        context.restoreAuthSystemState();
    }

    @Test
    public void testCreateProjectWithFundingParentMetadataWithoutAuthority() throws Exception {
        context.turnOffAuthorisationSystem();

        Group projectCommunityGroup =
            GroupBuilder.createGroup(context)
                        .withName(String.format(PROJECT_READERS_GROUP_TEMPLATE, projectCommunity.getID()))
                        .build();

        Group subGroup = GroupBuilder.createGroup(context)
                                     .withName("sub-group one")
                                     .withParent(projectCommunityGroup)
                                     .build();

        Item programmeItem = ItemBuilder.createItem(context, programmeCollection)
                                        .withTitle("new programme")
                                        .build();

        String groupName = String.format(PROGRAMME_GROUP_TEMPLATE, programmeItem.getID());
        deleteGroupIfExist(context, groupName);
        Group programmeGroup = GroupBuilder.createGroup(context)
                                           .withName(groupName)
                                           .build();

        // create project item with Funding Parent Metadata without authority
        Item projectItem = ItemBuilder.createItem(context, projectCollection)
                                      .withTitle("new project")
                                      .withFundingParent(programmeItem.getName())
                                      .build();

        context.restoreAuthSystemState();

        projectCommunityGroup = context.reloadEntity(projectCommunityGroup);

        // check that member groups of projectCommunityGroup still the same no changes
        assertEquals(projectCommunityGroup.getMemberGroups().size(), 1);
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getID(), subGroup.getID());
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getName(), subGroup.getName());
    }

    @Test
    public void testCreateProjectWithFundingParentMetadataWithPublicationAuthority() throws Exception {
        context.turnOffAuthorisationSystem();

        Group projectCommunityGroup =
            GroupBuilder.createGroup(context)
                        .withName(String.format(PROJECT_READERS_GROUP_TEMPLATE, projectCommunity.getID()))
                        .build();

        Group subGroup = GroupBuilder.createGroup(context)
                                     .withName("sub-group one")
                                     .withParent(projectCommunityGroup)
                                     .build();


        Collection publicationCollection = CollectionBuilder.createCollection(context, parentCommunity)
                                                            .withName("publication collection")
                                                            .withEntityType("Publication")
                                                            .build();

        Item publicationItemItem = ItemBuilder.createItem(context, publicationCollection)
                                        .withTitle("new programme")
                                        .build();

        // create project item with Funding Parent Metadata with authority but not programme
        Item projectItem = ItemBuilder.createItem(context, projectCollection)
                                      .withTitle("new project")
                                      .withFundingParent(
                                          publicationItemItem.getName(), String.valueOf(publicationItemItem.getID()))
                                      .build();

        context.restoreAuthSystemState();

        projectCommunityGroup = context.reloadEntity(projectCommunityGroup);

        // check that member groups of projectCommunityGroup still the same no changes
        assertEquals(projectCommunityGroup.getMemberGroups().size(), 1);
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getID(), subGroup.getID());
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getName(), subGroup.getName());
    }

    @Test
    public void testCreateProjectWithFundingParentMetadataButNoProgrammeGroupExist() throws Exception {
        context.turnOffAuthorisationSystem();

        Group projectCommunityGroup =
            GroupBuilder.createGroup(context)
                        .withName(String.format(PROJECT_READERS_GROUP_TEMPLATE, projectCommunity.getID()))
                        .build();

        Group subGroup = GroupBuilder.createGroup(context)
                                     .withName("sub-group one")
                                     .withParent(projectCommunityGroup)
                                     .build();

        Item programmeItem = ItemBuilder.createItem(context, programmeCollection)
                                        .withTitle("new programme")
                                        .build();

        String groupName = String.format(PROGRAMME_GROUP_TEMPLATE, programmeItem.getID());
        deleteGroupIfExist(context, groupName);

        // create project item with Funding Parent Metadata with programme authority
        Item projectItem = ItemBuilder.createItem(context, projectCollection)
                                      .withTitle("new project")
                                      .withFundingParent(programmeItem.getName(), String.valueOf(programmeItem.getID()))
                                      .build();

        context.restoreAuthSystemState();

        projectCommunityGroup = context.reloadEntity(projectCommunityGroup);
        assertEquals(projectCommunityGroup.getMemberGroups().size(), 1);

        // check that member groups of projectCommunityGroup still the same no changes
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getID(), subGroup.getID());
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getName(), subGroup.getName());
        context.restoreAuthSystemState();
    }

    @Test
    public void testCreateProjectWithFundingParentMetadataWithProgrammeAuthority() throws Exception {
        context.turnOffAuthorisationSystem();

        Group projectCommunityGroup =
            GroupBuilder.createGroup(context)
                        .withName(String.format(PROJECT_READERS_GROUP_TEMPLATE, projectCommunity.getID()))
                        .build();

        Group subGroup = GroupBuilder.createGroup(context)
                                     .withName("sub-group one")
                                     .withParent(projectCommunityGroup)
                                     .build();

        Group subGroupTwo = GroupBuilder.createGroup(context)
                                      .withName("sub-group two")
                                      .withParent(projectCommunityGroup)
                                      .build();

        Item programmeItem = ItemBuilder.createItem(context, programmeCollection)
                                        .withTitle("new programme")
                                        .build();

        String groupName = String.format(PROGRAMME_GROUP_TEMPLATE, programmeItem.getID());
        deleteGroupIfExist(context, groupName);
        Group programmeGroup = GroupBuilder.createGroup(context)
                                           .withName(groupName)
                                           .build();

        // create project item with Funding Parent Metadata with programme authority
        Item projectItem = ItemBuilder.createItem(context, projectCollection)
                                      .withTitle("new project")
                                      .withFundingParent(programmeItem.getName(), String.valueOf(programmeItem.getID()))
                                      .build();

        context.restoreAuthSystemState();

        projectCommunityGroup = context.reloadEntity(projectCommunityGroup);
        assertEquals(projectCommunityGroup.getMemberGroups().size(), 1);

        // check that member groups of projectCommunityGroup now is only programmeGroup
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getID(), programmeGroup.getID());
        assertEquals(projectCommunityGroup.getMemberGroups().get(0).getName(), programmeGroup.getName());
        context.restoreAuthSystemState();
    }

    private void deleteGroupIfExist(Context context, String groupName)
        throws SQLException, AuthorizeException, IOException {
        Group group = groupService.findByName(context, groupName);
        if (!Objects.isNull(group)) {
            groupService.delete(context, group);
        }
    }

}