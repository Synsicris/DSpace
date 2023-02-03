/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.login.impl;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.login.PostLoggedInAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.discovery.SearchServiceException;
import org.dspace.eperson.EPerson;
import org.dspace.profile.service.ResearcherProfileService;
import org.dspace.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Implementation of {@link PostLoggedInAction} that perform an automatic
 * creation of the researcher profile if the current user doesn't have one.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
public class ResearcherProfileAutomaticCreation implements PostLoggedInAction {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResearcherProfileAutomaticCreation.class);

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private ResearcherProfileService researcherProfileService;

    public ResearcherProfileAutomaticCreation(String ePersonField, String profileField) {
        Assert.notNull(ePersonField, "An eperson field is required to perform automatic claim");
        Assert.notNull(profileField, "An profile field is required to perform automatic claim");
    }

    @Override
    public void loggedIn(Context context) {

        EPerson currentUser = context.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String enabled = configService.getProperty("researcher-profile.automatic-creation-on-login");

        if (StringUtils.isBlank(enabled) || enabled.equalsIgnoreCase("false")) {
            return;
        }

        try {
            claimProfile(context, currentUser);
        } catch (SQLException | AuthorizeException | SearchServiceException | IllegalStateException e) {
            LOGGER.error("An error occurs during the profile claim by email", e);
        }

    }

    private void claimProfile(Context context, EPerson currentUser)
        throws SQLException, AuthorizeException, SearchServiceException {

        currentUser.getID();
        currentUser.getFullName();

        if (currentUserHasAlreadyResearcherProfile(context)) {
            return;
        } else {
            createResearcherProfile(context, currentUser);
        }


    }

    private boolean currentUserHasAlreadyResearcherProfile(Context context) throws SQLException, AuthorizeException {
        return researcherProfileService.findById(context, context.getCurrentUser().getID()) != null;
    }

    private void createResearcherProfile(Context context, EPerson currentUser)
        throws SQLException, AuthorizeException, SearchServiceException {

        researcherProfileService.createAndReturn(context, currentUser);
    }

}
