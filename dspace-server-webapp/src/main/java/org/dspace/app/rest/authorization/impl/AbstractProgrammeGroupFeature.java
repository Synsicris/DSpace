/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import java.sql.SQLException;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.synsicris.programme.ProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractProgrammeGroupFeature extends IsMemberOfProjectFeature {

    @Autowired
    private ProgrammeService programmeService;

    @Override
    protected abstract String getRoleName();

    @Override
    protected Group getGroupFromRelatedItem(Context context, Item relatedItem, String roleName) throws SQLException {
        return getGroupFromItem(
            context,
            programmeService.getProgrammeByProgrammeRelation(context, relatedItem),
            roleName
        );
    }

    @Override
    protected Group getGroupFromItem(Context context, Item programmeItem, String roleName) throws SQLException {
        if (programmeItem == null) {
            return null;
        }
        return programmeService.getProgrammeGroupByRole(
            context,
            programmeItem.getID(),
            roleName
        );
    }

}