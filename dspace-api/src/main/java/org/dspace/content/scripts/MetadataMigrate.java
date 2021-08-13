package org.dspace.content.scripts;

import java.util.ArrayList;
import java.util.List;

public class MetadataMigrate {

    public List<MetadataMigrateAction> actions = new ArrayList<MetadataMigrateAction>();

    public List<MetadataMigrateAction> getActions() {
        return actions;
    }

    public void setActions(List<MetadataMigrateAction> actions) {
        this.actions = actions;
    } 
}
