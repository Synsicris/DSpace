/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.Group;
import org.dspace.eperson.GroupConfiguration;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.ProgrammeCreateGroupConsumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * test against {@link ProgrammeCreateGroupConsumer}.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ProgrammeCreateGroupConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ConfigurationService configurationService;

    private Collection collection;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("parent community")
                                          .build();
        collection = CollectionBuilder.createCollection(context, parentCommunity)
                                      .withName("collection")
                                      .withEntityType("programme")
                                      .build();

        context.restoreAuthSystemState();

    }

    @After
    public void tearDown() {
        configurationService.setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, "");
    }

    @Test
    public void testCreateProgrammeItem() throws Exception {

        context.turnOffAuthorisationSystem();

        Group organisationalManagerGroup =
            GroupBuilder.createGroup(context)
                .withName("funder-organisational-managers.group")
                .addMember(context.getCurrentUser())
                .build();

        configurationService
            .setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, organisationalManagerGroup.getID().toString());

        Item programmeItem = ItemBuilder.createItem(context, collection)
                                    .withTitle("new programme")
                                    .build();

        context.restoreAuthSystemState();

        String groupMembersName = String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID());
        String groupManagerName = String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeItem.getID());

        Group programmeMembersGroup = groupService.findByName(context, groupMembersName);
        Group programmeManagerGroup = groupService.findByName(context, groupManagerName);

        assertNotNull(programmeMembersGroup);
        assertEquals(programmeMembersGroup.getName(), groupMembersName);
        assertNotNull(programmeManagerGroup);
        assertEquals(programmeManagerGroup.getName(), groupManagerName);
    }

    @Test
    public void testCreateNotProgrammeItem() throws Exception {

        context.turnOffAuthorisationSystem();
        Collection collection =
            CollectionBuilder.createCollection(context, parentCommunity)
                .withName("collection")
                .withEntityType("Project")
                .build();
        Item projectItem =
            ItemBuilder.createItem(context, collection)
                .withTitle("new project")
                .build();
        context.restoreAuthSystemState();

        String groupMembersName = String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, projectItem.getID());
        String groupManagerName = String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, projectItem.getID());

        Group programmeMembersGroup = groupService.findByName(context, groupMembersName);
        Group programmeManagerGroup = groupService.findByName(context, groupManagerName);
        assertNull(programmeMembersGroup);
        assertNull(programmeManagerGroup);
    }

    @Test
    public void testDeleteProgrammeItem() throws Exception {

        context.turnOffAuthorisationSystem();

        Group organisationalManagerGroup =
            GroupBuilder.createGroup(context)
                .withName("funder-organisational-managers.group")
                .addMember(context.getCurrentUser())
                .build();

        configurationService
            .setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, organisationalManagerGroup.getID().toString());

        // create a programme item
        Item programmeItem = ItemBuilder.createItem(context, collection)
                                    .withTitle("new programme")
                                    .build();
        context.restoreAuthSystemState();

        String groupMembersName = String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID());
        String groupManagerName = String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeItem.getID());

        // check that programme group has been created
        Group programmeMembersGroup = groupService.findByName(context, groupMembersName);
        Group programmeManagerGroup = groupService.findByName(context, groupManagerName);
        assertNotNull(programmeMembersGroup);
        assertNotNull(programmeManagerGroup);

        programmeItem = context.reloadEntity(programmeItem);

        context.turnOffAuthorisationSystem();
        // when delete the programme item
        itemService.delete(context, programmeItem);
        context.restoreAuthSystemState();
        context.commit();

        // then programme group will be deleted
        programmeMembersGroup = groupService.findByName(context, groupMembersName);
        programmeManagerGroup = groupService.findByName(context, groupManagerName);
        assertNull(programmeMembersGroup);
        assertNull(programmeManagerGroup);

    }

}
