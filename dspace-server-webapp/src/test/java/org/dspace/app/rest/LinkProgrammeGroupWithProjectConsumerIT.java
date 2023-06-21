/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.dspace.project.util.ProjectConstants.PROJECT_EXTERNALREADERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
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
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.LinkProgrammeGroupWithProjectConsumer;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test against {@link LinkProgrammeGroupWithProjectConsumer}.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 */
public class LinkProgrammeGroupWithProjectConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ConfigurationService configurationService;

    private Collection projectCollection;
    private Collection programmeCollection;
    private Community projectCommunity;
    private Community rootCommunity;
    private Community projectSharedCommunity;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        parentCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("community")
                .build();

        programmeCollection =
            CollectionBuilder.createCollection(context, parentCommunity)
                .withName("programme collection")
                .withEntityType(PROGRAMME)
                .build();

        rootCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("root community")
                .build();

        projectSharedCommunity =
            CommunityBuilder.createSubCommunity(context, rootCommunity)
                .withName("project shared community")
                .build();

        projectCommunity =
            CommunityBuilder.createSubCommunity(context, projectSharedCommunity)
                .withName("project's community")
                .build();

        projectCollection =
            CollectionBuilder.createCollection(context, projectCommunity)
                .withName("project collection")
                .withEntityType(PROJECT_ENTITY)
                .build();

        context.restoreAuthSystemState();
    }

    @Test
    public void testCreateProjectWithFundingParentMetadataWithoutAuthority() throws Exception {
        context.turnOffAuthorisationSystem();

        Group projectCommunityGroup =
            GroupBuilder.createGroup(context)
                .withName(String.format(PROJECT_READERS_GROUP_TEMPLATE, projectSharedCommunity.getID()))
                .build();

        Group subGroup =
            GroupBuilder.createGroup(context)
                .withName("sub-group one")
                .withParent(projectCommunityGroup)
                .build();

        Item programmeItem =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("new programme")
                .build();

        deleteGroupIfExist(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()));

        GroupBuilder.createGroup(context)
            .withName(String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()))
            .build();

        // create project item with Funding Parent Metadata without authority
        ItemBuilder.createItem(context, projectCollection)
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

        Group subGroup =
            GroupBuilder.createGroup(context)
                .withName("sub-group one")
                .withParent(projectCommunityGroup)
                .build();

        Collection publicationCollection =
            CollectionBuilder.createCollection(context, parentCommunity)
                .withName("publication collection")
                .withEntityType("Publication")
                .build();

        Item publicationItemItem =
            ItemBuilder.createItem(context, publicationCollection)
                .withTitle("new programme")
                .build();

        // create project item with Funding Parent Metadata with authority but not
        // programme
        ItemBuilder.createItem(context, projectCollection)
            .withTitle("new project")
            .withFundingParent(
                publicationItemItem.getName(), String.valueOf(publicationItemItem.getID())
            )
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

        Group subGroup =
            GroupBuilder.createGroup(context)
                .withName("sub-group one")
                .withParent(projectCommunityGroup)
                .build();

        Item programmeItem =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("new programme")
                .build();

        deleteGroupIfExist(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()));

        // create project item with Funding Parent Metadata with programme authority
        ItemBuilder.createItem(context, projectCollection)
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

        String projectParentCommunity = this.configurationService.getProperty("project.parent-community-id");
        this.configurationService.setProperty("project.parent-community-id", projectSharedCommunity.getID().toString());
        try {
            context.turnOffAuthorisationSystem();
            Group projectCommunityGroup =
                GroupBuilder.createGroup(context)
                    .withName(String.format(PROJECT_EXTERNALREADERS_GROUP_TEMPLATE, projectCommunity.getID()))
                    .build();

            Group sub1 =
                GroupBuilder.createGroup(context)
                    .withName("sub-group one")
                    .withParent(projectCommunityGroup)
                    .build();

            Group sub2 =
                GroupBuilder.createGroup(context)
                    .withName("sub-group two")
                    .withParent(projectCommunityGroup)
                    .build();

            Item programmeItem =
                ItemBuilder.createItem(context, programmeCollection)
                    .withTitle("new programme")
                    .build();

            deleteGroupIfExist(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()));

            Group programmeMembersGroup =
                GroupBuilder.createGroup(context)
                    .withName(String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()))
                    .build();

            // create project item with Funding Parent Metadata with programme authority
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("new project")
                .withFundingParent(programmeItem.getName(), String.valueOf(programmeItem.getID()))
                .build();

            context.restoreAuthSystemState();

            projectCommunityGroup = context.reloadEntity(projectCommunityGroup);
            assertEquals(2, projectCommunityGroup.getMemberGroups().size());

            Group programmeManagerGroups =
                this.groupService.findByName(
                    context,
                    String.format(
                        PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeItem.getID()
                        )
                    );

            // check that member groups of projectCommunityGroup now is only programmeGroup
            MatcherAssert.assertThat(
                projectCommunityGroup.getMemberGroups(),
                not(
                    containsInAnyOrder(sub1, sub2)
                )
            );
            MatcherAssert.assertThat(
                projectCommunityGroup.getMemberGroups(),
                containsInAnyOrder(programmeMembersGroup, programmeManagerGroups)
            );
        } finally {
            this.configurationService.setProperty("project.parent-community-id", projectParentCommunity);
        }

    }

    private void deleteGroupIfExist(Context context, String groupName)
        throws SQLException, AuthorizeException, IOException {
        Group group = groupService.findByName(context, groupName);
        if (!Objects.isNull(group)) {
            groupService.delete(context, group);
        }
    }

}