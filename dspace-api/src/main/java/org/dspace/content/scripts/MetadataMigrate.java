/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.scripts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class MetadataMigrate {

    public List<MetadataMigrateAction> actions = new ArrayList<MetadataMigrateAction>();

    public List<MetadataMigrateAction> getActions() {
        return actions;
    }

    public void setActions(List<MetadataMigrateAction> actions) {
        this.actions = actions;
    }
}
