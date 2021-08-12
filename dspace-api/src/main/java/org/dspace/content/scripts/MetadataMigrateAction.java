package org.dspace.content.scripts;

import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;

public class MetadataMigrateAction {
    public String action;
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getEntityType() {
        return entityType;
    }
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    public MetadataFieldConfig getMetadata() {
        return metadata;
    }
    public void setMetadata(MetadataFieldConfig metadata) {
        this.metadata = metadata;
    }
    public String entityType;
    public MetadataFieldConfig metadata;  
}
