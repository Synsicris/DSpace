/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.synsicris.security;

import static org.dspace.content.Item.ANY;
import static org.dspace.project.util.ProjectConstants.COMMENT_ENTITY;
import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_COMMENT_PROJECT;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {SecuritySynsicrisDeleteService}
 * to check if items with entity type 'Comment' can be deleted.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
public class SecuritySynsicrisCommentDeleteService extends SecuritySynsicrisDeleteService {

    private static final Logger log = LoggerFactory.getLogger(SecuritySynsicrisCommentDeleteService.class);

    @Override
    public boolean canDelete(Context context, Item item) {
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", ANY);
        if (!StringUtils.equals(entityType, COMMENT_ENTITY)) {
            return false;
        }
        // get project item uuid
        List<MetadataValue> metadataValues = itemService.getMetadata(item, MD_RELATION_COMMENT_PROJECT.schema,
                                                                           MD_RELATION_COMMENT_PROJECT.element,
                                                                           MD_RELATION_COMMENT_PROJECT.qualifier,
                                                                           ANY);
        MetadataValue mv;
        if (CollectionUtils.isNotEmpty(metadataValues) && (mv = metadataValues.get(0)) != null) {
            try {
                Item projectItem = itemService.find(context, UUID.fromString(mv.getAuthority()));
                for (String roleGroupName : this.getSupportedRoleGroup()) {
                    Group roleGroup = getCommunityGroupByRole(context, projectItem, roleGroupName);
                    if (Objects.nonNull(roleGroup) && groupService.isMember(context, roleGroup)) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    public List<String> getSupportedRoleGroup() {
        return Arrays.asList(FUNDERS_ROLE);
    }

}
