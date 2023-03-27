/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.dspace.project.util.ProjectConstants.FUNDING_ENTITY;
import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.SQLException;

import org.dspace.app.rest.authorization.impl.IsFunderProjectOfProgrammeFeature;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.GroupConfiguration;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * ITs for the related Authorization Feature {@link IsFunderProjectOfProgrammeFeature}
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class IsFunderProjectOfProgrammeFeatureIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;
    @Autowired
    private GroupService groupService;

    private AuthorizationFeature isFunderProjectOfProgramme;
    private Community sharedCommunity;
    private Collection programmeCollection;

    private Community projectsCommunity;
    private Collection projectCollection;

    private Group organisationalManagerGroup;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        organisationalManagerGroup =
            GroupBuilder.createGroup(context)
                .withName("funder-organisational-managers.group")
                .addMember(context.getCurrentUser())
                .build();

        configurationService
            .setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, organisationalManagerGroup.getID().toString());

        parentCommunity =
            CommunityBuilder.createCommunity(context)
              .withName("Root Community")
              .build();

        sharedCommunity =
            CommunityBuilder.createSubCommunity(context, parentCommunity)
              .withName("Shared")
              .build();

        projectsCommunity =
            CommunityBuilder.createSubCommunity(context, parentCommunity)
              .withName("Projects")
              .build();

        projectCollection =
            CollectionBuilder.createCollection(context, projectsCommunity)
              .withName("First Project")
              .withEntityType(PROJECT_ENTITY)
              .build();

        programmeCollection =
            CollectionBuilder.createCollection(context, sharedCommunity)
              .withName("Programmes")
              .withEntityType(PROGRAMME)
              .build();

        context.restoreAuthSystemState();

        isFunderProjectOfProgramme =
            authorizationFeatureService.find(IsFunderProjectOfProgrammeFeature.NAME);

    }

    @After
    public void after() throws Exception {
        configurationService
            .setProperty(GroupConfiguration.ORGANISATIONAL_MANAGER, null);
    }

    @Test
    public void testIsFunderOrganizationalManagerOfProgrammeWhenContextUserIsInGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();
        Item programmeItem =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme Test")
                .build();
        Item projectItem =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project Test")
                .withFundingParent(programmeItem.getName(), programmeItem.getID().toString())
                .build();
        context.restoreAuthSystemState();

        this.addUserToGroup(programmeItem, PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE, eperson);

        ItemRest projectItemRest = itemConverter.convert(projectItem, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectOfProgramme.isAuthorized(context, projectItemRest);

        assertThat(isAuthorized, is(true));

    }

    @Test
    public void testIsFunderOrganizationalManagerOfProgrammeWhenContextUserIsNotInGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();
        Item programmeItem =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme Test")
                .build();
        Item projectItem =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project Test")
                .withFundingParent(programmeItem.getName(), programmeItem.getID().toString())
                .build();
        context.restoreAuthSystemState();

        ItemRest projectItemRest = itemConverter.convert(projectItem, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectOfProgramme.isAuthorized(context, projectItemRest);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderOrganizationalManagerOfProgrammeWhenGroupNotExists() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();
        Item projectItem =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project Test")
                .build();
        context.restoreAuthSystemState();

        ItemRest projectItemRest = itemConverter.convert(projectItem, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectOfProgramme.isAuthorized(context, projectItemRest);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderOrganizationalManagerOfProgrammeWhenContextUserInGroupAndRelatedItemChecked()
        throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();
        Item programmeItem =
            ItemBuilder.createItem(context, programmeCollection)
                .withTitle("Programme Test")
                .build();
        Item projectItem =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project Test")
                .withFundingParent(programmeItem.getName(), programmeItem.getID().toString())
                .build();

        Collection fundingCollection =
            CollectionBuilder.createCollection(context, projectsCommunity)
              .withName("Funding")
              .withEntityType(FUNDING_ENTITY)
              .build();

        Item fundingItem =
            ItemBuilder.createItem(context, fundingCollection)
                .withTitle("Funding Test")
                .withSynsicrisRelationProject(projectItem.getName(), projectItem.getID().toString())
                .build();
        context.restoreAuthSystemState();

        this.addUserToGroup(programmeItem, PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE, eperson);

        ItemRest fundingItemRest = itemConverter.convert(fundingItem, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectOfProgramme.isAuthorized(context, fundingItemRest);

        assertThat(isAuthorized, is(true));

    }

    protected Group addUserToGroup(Item programmeItem, String groupTemplate, EPerson user) throws SQLException {
        String groupMembersName = String.format(groupTemplate, programmeItem.getID());
        Group membersGroup = this.groupService.findByName(context, groupMembersName);
        assertThat(membersGroup, notNullValue());

        context.turnOffAuthorisationSystem();
        this.groupService.addMember(context, membersGroup, user);
        context.restoreAuthSystemState();

        return context.reloadEntity(membersGroup);
    }

}
