/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;

import org.dspace.content.Community;
import org.dspace.content.Item;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractProjectGenerator implements TemplateValueGenerator {

    @Autowired
    protected ProjectGeneratorService projectGeneratorService;

    protected Community getOwningCommunity(Item templateItem) throws SQLException {
        return this.projectGeneratorService.getOwningCommunity(templateItem);
    }

    protected Community getProjectCommunity(Item templateItem) throws SQLException {
        return this.projectGeneratorService.getProjectCommunity(templateItem);
    }

}
