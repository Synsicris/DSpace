/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;

import static org.dspace.project.util.ProjectConstants.MD_UNIQUE_ID;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.synsicris.programme.ProgrammeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class that contains common feature for the programme-related features.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class AbstractProgrammeGroupFeature extends IsMemberOfProjectFeature {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProgrammeGroupFeature.class);

    @Autowired
    private ProgrammeService programmeService;

    @Autowired
    private ItemService itemService;

    @Override
    protected abstract String getRoleName();

    @Override
    protected Group getGroupFromRelatedItem(Context context, Item relatedItem, String roleName) throws SQLException {
        return getGroupFromItem(
            context,
            projectConsumerService.getProjectItemByRelatedItem(context, getOriginalItem(context, relatedItem)),
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
            Optional.ofNullable(
                programmeService.getProgrammeByProgrammeRelation(context, getOriginalItem(context, projectItem))
                )
                .map(Item::getID)
                .orElse(null),
            roleName
        );
    }

    protected Item getOriginalItem(Context context, Item relatedItem) {
        return Optional.ofNullable(
            this.itemService.getMetadataFirstValue(relatedItem, MD_UNIQUE_ID, Item.ANY)
        )
        .map(uniqueId -> uniqueId.split("_")[0])
        .filter(Objects::nonNull)
        .map(uuid -> {
            Item foundItem = null;
            try {
                foundItem = this.itemService.find(context, UUID.fromString(uuid));
            } catch (SQLException e) {
                logger.error("Error while retrieving the original item from the unique_id with uuid: " + uuid, e);
            }
            return foundItem;
        })
        .orElse(relatedItem);
    }

}