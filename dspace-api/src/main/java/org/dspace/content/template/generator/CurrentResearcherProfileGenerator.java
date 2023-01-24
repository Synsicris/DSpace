/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.dspace.profile.ResearcherProfile;
import org.dspace.profile.service.ResearcherProfileService;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link TemplateValueGenerator} that returns a metadata
 * value with the researcher profile related to the submitter if any.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 *
 */
public class CurrentResearcherProfileGenerator implements TemplateValueGenerator {

    private static final Logger log = LoggerFactory.getLogger(EPersonValueGenerator.class);

    @Autowired
    private ResearcherProfileService researcherProfileService;

    @Override
    public List<MetadataValueVO> generator(Context context, Item targetItem, Item templateItem, String extraParams) {
        EPerson submitter = targetItem.getSubmitter();
        ResearcherProfile rp;
        try {
            rp = researcherProfileService.findById(context, submitter.getID());
        } catch (SQLException | AuthorizeException e) {
            return new ArrayList<MetadataValueVO>();
        }
        if (!Objects.isNull(rp)) {
            return Arrays.asList(new MetadataValueVO(rp.getFullName(), rp.getItem().getID().toString()));
        } else {
            return new ArrayList<MetadataValueVO>();
        }
    }

}
