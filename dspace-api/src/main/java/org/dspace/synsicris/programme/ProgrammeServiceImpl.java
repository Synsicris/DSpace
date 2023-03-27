/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.synsicris.programme;

import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;
import static org.dspace.project.util.ProjectConstants.MANAGERS_ROLE;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_CALLTOPROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ProgrammeServiceImpl implements ProgrammeService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private GroupService groupService;

    @Override
    public Group getProgrammeGroupByRole(Context context, UUID programmeUUID, String role) throws SQLException {

        if (programmeUUID == null) {
            return null;
        }

        String template;
        switch (role) {
            case READERS_ROLE:
                template = PROGRAMME_MEMBERS_GROUP_TEMPLATE;
                break;
            case FUNDERS_ROLE:
                template = PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE;
                break;
            case MANAGERS_ROLE:
                template = PROGRAMME_MANAGERS_GROUP_TEMPLATE;
                break;
            default:
                return null;
        }
        return groupService.findByName(context, String.format(template, programmeUUID.toString()));
    }

    @Override
    public Item getProgrammeByProgrammeRelation(Context context, Item relatedItem) throws SQLException {
        if (relatedItem == null) {
            return null;
        }
        List<MetadataValue> values =
            itemService.getMetadata(
                relatedItem,
                MD_RELATION_CALLTOPROGRAMME.schema,
                MD_RELATION_CALLTOPROGRAMME.element,
                MD_RELATION_CALLTOPROGRAMME.qualifier,
                null
            );
        UUID programmeUUID = null;
        if (
                values.isEmpty() ||
                values.get(0) == null ||
                (programmeUUID = UUIDUtils.fromString(values.get(0).getAuthority())) == null
        ) {
            return null;
        }
        return itemService.find(context, programmeUUID);
    }

}
