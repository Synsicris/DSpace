/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import static org.dspace.app.orcid.model.OrcidEntityType.FUNDING;
import static org.dspace.app.orcid.model.OrcidEntityType.PUBLICATION;

import java.util.List;
import java.util.stream.Collectors;

import org.dspace.app.orcid.service.OrcidSynchronizationService;
import org.dspace.app.profile.OrcidEntitySyncPreference;
import org.dspace.app.profile.OrcidProfileSyncPreference;
import org.dspace.app.profile.OrcidSynchronizationMode;
import org.dspace.app.profile.ResearcherProfile;
import org.dspace.app.profile.ResearcherProfileVisibility;
import org.dspace.app.profile.service.ResearcherProfileService;
import org.dspace.app.rest.model.ResearcherProfileRest;
import org.dspace.app.rest.model.ResearcherProfileRest.OrcidSynchronizationRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This converter is responsible for transforming an model that represent a
 * ResearcherProfile to the REST representation of an ResearcherProfile.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
@Component
public class ResearcherProfileConverter implements DSpaceConverter<ResearcherProfile, ResearcherProfileRest> {

    @Autowired
    private ResearcherProfileService researcherProfileService;
    @Autowired
    private OrcidSynchronizationService orcidSynchronizationService;

    @Override
    public ResearcherProfileRest convert(ResearcherProfile profile, Projection projection) {
        ResearcherProfileRest researcherProfileRest = new ResearcherProfileRest();

        ResearcherProfileVisibility visibility = researcherProfileService.getVisibility(profile);
        researcherProfileRest.setVisibility(visibility);
        researcherProfileRest.setId(profile.getId());
        researcherProfileRest.setProjection(projection);

        Item item = profile.getItem();

        if (orcidSynchronizationService.isLinkedToOrcid(item)) {
            profile.getOrcid().ifPresent(researcherProfileRest::setOrcid);

            OrcidSynchronizationRest orcidSynchronization = new OrcidSynchronizationRest();
            orcidSynchronization.setMode(getMode(item));
            orcidSynchronization.setProfilePreferences(getProfilePreferences(item));
            orcidSynchronization.setFundingsPreference(getFundingsPreference(item));
            orcidSynchronization.setPublicationsPreference(getPublicationsPreference(item));
            researcherProfileRest.setOrcidSynchronization(orcidSynchronization);
        }

        return researcherProfileRest;
    }

    private String getPublicationsPreference(Item item) {
        return orcidSynchronizationService.getEntityPreference(item, PUBLICATION)
            .map(OrcidEntitySyncPreference::name)
            .orElse(OrcidEntitySyncPreference.DISABLED.name());
    }

    private String getFundingsPreference(Item item) {
        return orcidSynchronizationService.getEntityPreference(item, FUNDING)
            .map(OrcidEntitySyncPreference::name)
            .orElse(OrcidEntitySyncPreference.DISABLED.name());
    }

    private List<String> getProfilePreferences(Item item) {
        return orcidSynchronizationService.getProfilePreferences(item).stream()
            .map(OrcidProfileSyncPreference::name)
            .collect(Collectors.toList());
    }

    private String getMode(Item item) {
        return orcidSynchronizationService.getSynchronizationMode(item)
            .map(OrcidSynchronizationMode::name)
            .orElse(OrcidSynchronizationMode.MANUAL.name());
    }

    @Override
    public Class<ResearcherProfile> getModelClass() {
        return ResearcherProfile.class;
    }

}
