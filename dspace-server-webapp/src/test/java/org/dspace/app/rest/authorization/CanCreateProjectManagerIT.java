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

import org.dspace.app.rest.authorization.impl.CanCreateProjectManager;
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
 * Test of {@link CanCreateProjectManager} implementation.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 */
public class CanCreateProjectManagerIT extends AbstractControllerIntegrationTest {

    private AuthorizationFeature canCreateProjectManager;

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

        canCreateProjectManager = authorizationFeatureService.find(CanCreateProjectManager.NAME);

    }

    @Test
    public void testCanCreateProjectManagerWhenCurrentUserIsMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder-organisational-managers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funder-organisational-managers.group", String.valueOf(managerGroup.getID()));

        boolean canCreate = canCreateProjectManager.isAuthorized(context, null);

        assertThat(canCreate, is(true));

    }

    @Test
    public void testCanCreateProjectManagerWhenCurrentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder-organisational-managers.group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funder-organisational-managers.group", String.valueOf(managerGroup.getID()));

        boolean canCreate = canCreateProjectManager.isAuthorized(context, null);

        assertThat(canCreate, is(false));

    }

    @Test
    public void testCanCreateProjectManagerWhenManagersGroupPropertyIsNull() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder-organisational-managers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funder-organisational-managers.group", null);

        boolean canCreate = canCreateProjectManager.isAuthorized(context, null);

        assertThat(canCreate, is(false));

    }

    @Test
    public void testCanCreateProjectManagerWhenSentUserIsMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder-organisational-managers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funder-organisational-managers.group", String.valueOf(managerGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean canCreate = canCreateProjectManager.isAuthorized(context, ePersonRest);

        assertThat(canCreate, is(true));

    }

    @Test
    public void testCanCreateProjectManagerWhenSentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group managerGroup = GroupBuilder.createGroup(context)
                                         .withName("funder-organisational-managers.group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funder-organisational-managers.group", String.valueOf(managerGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean canCreate = canCreateProjectManager.isAuthorized(context, ePersonRest);

        assertThat(canCreate, is(false));

    }

}
