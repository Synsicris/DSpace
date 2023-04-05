/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.dspace.app.rest.authorization.impl.IsFunderProjectManagerOfAnyProject;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test of {@link IsFunderProjectManagerOfAnyProject} implementation.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 */
public class IsFunderProjectManagerOfAnyProjectIT extends AbstractControllerIntegrationTest {

    private AuthorizationFeature isFunderProjectManagerOfAnyProject;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private EPersonConverter ePersonConverter;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Community")
                                          .build();

        context.restoreAuthSystemState();

        isFunderProjectManagerOfAnyProject =
            authorizationFeatureService.find(IsFunderProjectManagerOfAnyProject.NAME);

    }

    @Test
    public void testIsFunderProjectManagerWhenCurrentUserIsMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder_project_managers_group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-project-managers.group", String.valueOf(managerGroup.getID()));

        boolean isAuthorized = isFunderProjectManagerOfAnyProject.isAuthorized(context, null);

        assertThat(isAuthorized, is(true));

    }

    @Test
    public void testIsFunderProjectManagerWhenCurrentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder_project_managers_group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-project-managers.group", String.valueOf(managerGroup.getID()));

        boolean isAuthorized = isFunderProjectManagerOfAnyProject.isAuthorized(context, null);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderProjectManagerWhenManagersGroupPropertyIsNull() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder_project_managers_group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-project-managers.group", null);

        boolean isAuthorized = isFunderProjectManagerOfAnyProject.isAuthorized(context, null);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderProjectManagerWhenSentUserIsMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder_project_managers_group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-project-managers.group", String.valueOf(managerGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectManagerOfAnyProject.isAuthorized(context, ePersonRest);

        assertThat(isAuthorized, is(true));

    }

    @Test
    public void testIsFunderProjectManagerWhenSentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder_project_managers_group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-project-managers.group", String.valueOf(managerGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean isAuthorized = isFunderProjectManagerOfAnyProject.isAuthorized(context, ePersonRest);

        assertThat(isAuthorized, is(false));

    }

}
