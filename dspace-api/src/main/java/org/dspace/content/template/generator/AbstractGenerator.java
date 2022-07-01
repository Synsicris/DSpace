/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;
import java.util.Arrays;

import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGenerator implements TemplateValueGenerator {

    @Autowired
    private ConfigurationService configurationService;

    protected Community getOwningCommunity(Item templateItem) throws SQLException {
        Community parentCommunity = null;
        Collection owningCollection = templateItem.getTemplateItemOf();
        if (owningCollection == null) {
            // the item is a template item
            return null;
        }
        String[] commToSkip = configurationService.getArrayProperty("project.community-name.to-skip", new String[] {});
        parentCommunity = owningCollection.getCommunities().get(0);
        while (Arrays.stream(commToSkip).anyMatch(parentCommunity.getName()::equals)) {
            parentCommunity = parentCommunity.getParentCommunities().get(0);
        }
        return parentCommunity;
    }

    protected Community getProjectCommunity(Item templateItem) throws SQLException {
        Community owningCommunity = getOwningCommunity(templateItem);
        Community parentCommunity = owningCommunity.getParentCommunities().get(0);
        
        String parentCommId = configurationService.getProperty("project.parent-community-id", null);
        
        if (parentCommunity.getID().toString().equals(parentCommId)) {
            return owningCommunity;
        } else {
            return parentCommunity.getParentCommunities().get(0);
        }
    }

}
