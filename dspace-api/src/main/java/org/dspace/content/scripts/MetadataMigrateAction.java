/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.scripts;

import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
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
