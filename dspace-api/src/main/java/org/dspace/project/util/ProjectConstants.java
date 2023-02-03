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

    public static final String TEMPLATE = "template";

    public static final String FUNDER = "funder";

    public static final String FUNDER_PROGRAMME = "funder_programme";

    public static final String SHARED = "shared";

    public static final String OWNING_PROJECT = "owningproject";

    public static final String FUNDING = "funding";

    public static final String PROJECT = "project";

    public static final String FUNDING_ENTITY = "Funding";

    public static final String PROJECT_ENTITY = "Project";

    public static final String PROJECTPARTNER_ENTITY = "projectpartner";

    public static final String COORDINATORS_ROLE = "coordinators";

    public static final String PROGRAMME = "programme";

    public static final String PROGRAMME_MEMBERS_GROUP_TEMPLATE = "programme_%s_members_group";

    public static final String PROGRAMME_MANAGERS_GROUP_TEMPLATE = "programme_%s_managers_group";

    public static final String PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE = "programme_%s_funders_group";

    public static final String PROJECT_COORDINATORS_GROUP_TEMPLATE = "project_%s_coordinators_group";

    public static final String FUNDING_COORDINATORS_GROUP_TEMPLATE = "funding_%s_coordinators_group";

    public static final String MEMBERS_ROLE = "members";

    public static final String PROJECT_MEMBERS_GROUP_TEMPLATE = "project_%s_members_group";

    public static final String FUNDING_MEMBERS_GROUP_TEMPLATE = "funding_%s_members_group";

    public static final String FUNDERS_ROLE = "funders";

    public static final String PROJECT_FUNDERS_GROUP_TEMPLATE = "project_%s_funders_group";

    public static final String READERS_ROLE = "readers";

    public static final String PROJECT_READERS_GROUP_TEMPLATE = "project_%s_readers_group";

    public static final String FUNDER_PROJECT_MANAGERS_GROUP = "funder_project_managers_group";

    public static final String GROUP_POLICY_PLACEHOLDER = "GROUP_POLICY_PLACEHOLDER";

    public static final String RELATION_ITEM_ENTITY_TEMPLATE = "project_%s_item";

    public static final String SYSTEM_MEMBERS_GROUP = "system_members_group";

    public static final String[] SYNSICRIS_GROUPS_PREFIXES = { "funder", "funding", "project", "programme", "system"};

    public static final String[] notAllowedEditGrants = { "Project", "Funding", "subcontractor" };

    public static final MetadataFieldName MD_RELATION_ITEM_ENTITY =
        new MetadataFieldName("synsicris", "relation", "entity_item");

    public static final MetadataFieldName MD_PROJECT_RELATION =
        new MetadataFieldName("synsicris", "relation", "project");

    public static final MetadataFieldName MD_FUNDING_RELATION =
        new MetadataFieldName("synsicris", "relation", "funding");

    public static final MetadataFieldName MD_EASYIMPORT =
        new MetadataFieldName("synsicris", "type", "easyimport");

    public static final MetadataFieldName MD_AGROVOC = new MetadataFieldName("synsicris", "subject", "agrovoc");

    public static final MetadataFieldName MD_RELATION_CALL = new MetadataFieldName("synsicris", "relation", "call");

    public static final MetadataFieldName MD_POLICY_SHARED = new MetadataFieldName("cris", "project", "shared");

    public static final MetadataFieldName MD_RELATION_FUNDINGOBJTOPROGRAMME =
        new MetadataFieldName(
            "synsicris",
            "relation",
            "programme"
        );
    public static final MetadataFieldName MD_RELATION_CALLTOPROGRAMME =
        new MetadataFieldName(
            "oairecerif",
            "fundingParent"
        );

    public static final MetadataFieldName MD_POLICY_GROUP = new MetadataFieldName("cris", "policy", "group");

    public static final MetadataFieldName MD_CURRENCY = new MetadataFieldName("oairecerif", "amount", "currency");
    public static final String DEFAULT_CURRENCY = "Euro";

    public static final MetadataFieldName MD_PROJECT_STATUS = new MetadataFieldName("oairecerif", "project", "status");
    public static final String DEFAULT_STATUS = "In preparation";

    public static final MetadataFieldName MD_ENTITY_TYPE = new MetadataFieldName("dspace", "entity", "type");
    public static final MetadataFieldName MD_VERSION = new MetadataFieldName("synsicris", "version");
    public static final MetadataFieldName MD_VERSION_READ_POLICY_GROUP =
        new MetadataFieldName("synsicris", "versioning-read-policy", "group");
    public static final MetadataFieldName MD_VERSION_VISIBLE = new MetadataFieldName("synsicris", "version", "visible");
    public static final MetadataFieldName MD_VERSION_OFFICIAL =
        new MetadataFieldName("synsicris", "version", "official");
    public static final MetadataFieldName MD_LAST_VERSION_VISIBLE =
        new MetadataFieldName("synsicris", "isLastVersion", "visible");
    public static final MetadataFieldName MD_LAST_VERSION = new MetadataFieldName("synsicris", "isLastVersion");
    public static final MetadataFieldName MD_FUNDER_POLICY_GROUP =
        new MetadataFieldName("synsicris", "funder-policy", "group");
    public static final MetadataFieldName MD_READER_POLICY_GROUP =
        new MetadataFieldName("synsicris", "reader-policy", "group");
    public static final MetadataFieldName MD_MEMBER_POLICY_GROUP =
        new MetadataFieldName("synsicris", "member-policy", "group");
    public static final MetadataFieldName MD_COORDINATOR_POLICY_GROUP =
        new MetadataFieldName("synsicris", "coordinator-policy", "group");
    public static final MetadataFieldName MD_UNIQUE_ID =
        new MetadataFieldName("synsicris", "uniqueid");

    /**
     * Default constructor
     */
    private ProjectConstants() { }
}