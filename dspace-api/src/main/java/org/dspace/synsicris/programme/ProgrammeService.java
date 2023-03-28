/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.synsicris.programme;

import java.sql.SQLException;
import java.util.UUID;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.Group;

public interface ProgrammeService {

    Group getProgrammeGroupByRole(Context context, UUID programmeUUID, String role) throws SQLException;

    Item getProgrammeByProgrammeRelation(Context context, Item projectItem) throws SQLException;

}