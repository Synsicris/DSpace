/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.project.util;

import org.dspace.content.MetadataFieldName;

/**
 * Class with constants for Project services.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectConstants {

    public static final String FUNDER = "funder";

    public static final String FUNDER_PROGRAMME = "funder_programme";

    public static final String SHARED = "shared";

    public static final String OWNING_PROJECT = "owningproject";

    public static final String PROJECT = "project";

    public static final String PARENTPROJECT = "parentproject";

    public static final String PROJECT_ENTITY = "Project";

    public static final String PARENTPROJECT_ENTITY = "parentproject";

    public static final MetadataFieldName MD_PROJECT_ENTITY =
                    new MetadataFieldName("synsicris", "relation", "entity_project");

    public static final MetadataFieldName MD_AGROVOC = new MetadataFieldName("synsicris", "subject", "agrovoc");

    /**
     * Default constructor
     */
    private ProjectConstants() { }
}