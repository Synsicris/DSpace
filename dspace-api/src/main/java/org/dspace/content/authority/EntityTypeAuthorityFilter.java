/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.authority;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

public class EntityTypeAuthorityFilter extends CustomAuthorityFilter {

    private List<String> supportedEntities;

    public void setSupportedEntities(List<String> supportedEntities) {
        this.supportedEntities = supportedEntities;
    }

    @Override
    public boolean appliesTo(LinkableEntityAuthority linkableEntityAuthority) {
        if (CollectionUtils.isEmpty(supportedEntities)) {
            return true;
        }

        String[] linkedEntityTypeArray = linkableEntityAuthority.getLinkedEntityType();
        List<String> linkedEntityTypes = List.of();
        if (ArrayUtils.isNotEmpty(linkedEntityTypeArray)) {
            linkedEntityTypes = Arrays.asList(linkedEntityTypeArray);
        }
        return CollectionUtils.containsAny(
                supportedEntities,
                linkedEntityTypes
            );
    }

    public EntityTypeAuthorityFilter(List<String> customQueries) {
        super.customQueries = customQueries;
    }
}
