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
    
    public static final String ADMIN_ROLE = "admin";
    
    public static final String ADMIN_GROUP_TEMPLATE = "project_%s_admin_group";
    
    public static final String MEMBERS_ROLE = "members";

    public static final String MEMBERS_GROUP_TEMPLATE = "project_%s_members_group";
    
    public static final String[] notAllowedEditGrants = { "Project", "Funding", "subcontractor" };

    public static final MetadataFieldName MD_PROJECT_ENTITY =
                    new MetadataFieldName("synsicris", "relation", "entity_project");

    public static final MetadataFieldName MD_PARENTPROJECT_RELATION =
            new MetadataFieldName("synsicris", "relation", "parentproject");

    public static final MetadataFieldName MD_AGROVOC = new MetadataFieldName("synsicris", "subject", "agrovoc");

    public static final MetadataFieldName MD_RELATION_CALL = new MetadataFieldName("synsicris", "relation", "call");
    
    public static final MetadataFieldName MD_POLICY_SHARED = new MetadataFieldName("cris", "project", "shared");
   
    public static final MetadataFieldName MD_RELATION_FUNDINGOBJTOPROGRAMME = new MetadataFieldName("synsicris",
            "relation","programme");
    public static final MetadataFieldName MD_RELATION_CALLTOPROGRAMME = new MetadataFieldName("oairecerif",
            "fundingParent");

    /**
     * Default constructor
     */
    private ProjectConstants() { }
}