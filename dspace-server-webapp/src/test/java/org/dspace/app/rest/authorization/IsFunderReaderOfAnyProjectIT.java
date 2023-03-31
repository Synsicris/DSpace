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

import org.dspace.app.rest.authorization.impl.IsFunderReaderOfAnyProject;
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
 * Test of {@link IsFunderReaderOfAnyProject} implementation.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 */
public class IsFunderReaderOfAnyProjectIT extends AbstractControllerIntegrationTest {

    private AuthorizationFeature isFunderReader;

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

        isFunderReader =
            authorizationFeatureService.find(IsFunderReaderOfAnyProject.NAME);

    }

    @Test
    public void testIsFunderReaderWhenCurrentUserIsMemberOfReadersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group readersGroup = GroupBuilder.createGroup(context)
                                         .withName("funders-readers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-readers.group", String.valueOf(readersGroup.getID()));

        boolean isAuthorized = isFunderReader.isAuthorized(context, null);

        assertThat(isAuthorized, is(true));

    }

    @Test
    public void testIsFunderReaderWhenCurrentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group readersGroup = GroupBuilder.createGroup(context)
                                         .withName("funders-readers.group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-readers.group", String.valueOf(readersGroup.getID()));

        boolean isAuthorized = isFunderReader.isAuthorized(context, null);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderReaderWhenManagersGroupPropertyIsNull() throws Exception {

        context.setCurrentUser(eperson);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group readersGroup = GroupBuilder.createGroup(context)
                                         .withName("funders-readers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-readers.group", null);

        boolean isAuthorized = isFunderReader.isAuthorized(context, null);

        assertThat(isAuthorized, is(false));

    }

    @Test
    public void testIsFunderReaderWhenSentUserIsMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add eperson as member of group
        Group readersGroup = GroupBuilder.createGroup(context)
                                         .withName("funders-readers.group")
                                         .addMember(eperson)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-readers.group", String.valueOf(readersGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean isAuthorized = isFunderReader.isAuthorized(context, ePersonRest);

        assertThat(isAuthorized, is(true));

    }

    @Test
    public void testIsFunderReaderWhenSentUserIsNotMemberOfManagersGroup() throws Exception {

        context.setCurrentUser(admin);
        context.turnOffAuthorisationSystem();

        // create a manager Group and add admin as member of group
        Group readersGroup = GroupBuilder.createGroup(context)
                                         .withName("funders-readers.group")
                                         .addMember(admin)
                                         .build();

        context.restoreAuthSystemState();

        configurationService.setProperty("funders-readers.group", String.valueOf(readersGroup.getID()));
        EPersonRest ePersonRest = ePersonConverter.convert(eperson, Projection.DEFAULT);
        boolean isAuthorized = isFunderReader.isAuthorized(context, ePersonRest);

        assertThat(isAuthorized, is(false));

    }

}
