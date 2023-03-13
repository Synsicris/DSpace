/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import java.sql.SQLException;
import java.util.Optional;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.synsicris.programme.ProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class that contains common feature for the programme-related features.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class AbstractProgrammeGroupFeature extends IsMemberOfProjectFeature {

    @Autowired
    private ProgrammeService programmeService;

    @Override
    protected abstract String getRoleName();

    @Override
    protected Group getGroupFromRelatedItem(Context context, Item relatedItem, String roleName) throws SQLException {
        return getGroupFromItem(
            context,
            projectConsumerService.getProjectItemByRelatedItem(context, relatedItem),
            roleName
        );
    }

    @Override
    protected Group getGroupFromItem(Context context, Item projectItem, String roleName) throws SQLException {
        if (projectItem == null) {
            return null;
        }
        return programmeService.getProgrammeGroupByRole(
            context,
            Optional.ofNullable(programmeService.getProgrammeByProgrammeRelation(context, projectItem))
                .map(Item::getID)
                .orElse(null),
            roleName
        );
    }

}