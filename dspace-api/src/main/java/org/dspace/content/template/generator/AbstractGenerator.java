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

    protected Community getProjectCommunity(Item templateItem) throws SQLException {
        Community parentProjectCommunity = null;
        Collection owningCollection = templateItem.getTemplateItemOf();
        if (owningCollection == null) {
            // the item is a template item
            return null;
        }
        String[] commToSkip = configurationService.getArrayProperty("project.community-name.to-skip", new String[] {});
        parentProjectCommunity = owningCollection.getCommunities().get(0);
        while(Arrays.stream(commToSkip).anyMatch(parentProjectCommunity.getName()::equals)) {
            parentProjectCommunity = parentProjectCommunity.getParentCommunities().get(0);
        }
        return parentProjectCommunity;
    }

    protected Community getParentProjectCommunity(Item templateItem) throws SQLException {
        Community projectCommunity = getProjectCommunity(templateItem);
        Community parentProjectCommunity = projectCommunity.getParentCommunities().get(0)
                .getParentCommunities().get(0);
        return parentProjectCommunity;
    }
	
}
