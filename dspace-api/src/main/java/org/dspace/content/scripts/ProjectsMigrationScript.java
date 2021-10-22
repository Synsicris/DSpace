/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.scripts;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.Choices;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.dspace.metrics.UpdateCrisMetricsWithExternalSource;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.util.UUIDUtils;
import org.dspace.utils.DSpace;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link DSpaceRunnable}
 * The purpose of this script is to migrate projects according to a certain structure .
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectsMigrationScript extends
             DSpaceRunnable<ProjectsMigrationScriptConfiguration<ProjectsMigrationScript>> {

    private static final Logger log = LogManager.getLogger(ProjectsMigrationScript.class);

    private List<UUID> errorDeposit = new LinkedList<UUID>();

    private ConfigurationService configurationService;

    private InstallItemService installItemService;

    private WorkspaceItemService wsItemService;

    private CollectionService collectionService;

    private CommunityService communityService;

    private ItemService itemService;

    private GroupService groupService;
    
    private EPersonService epersonService;

    private Context context;

    private AuthorizeService authorizeService;
    
    private Map<String, MetadataMigrate> metadataMigrationMap;


    private String matadataToSkip[] = new String[] {
        "dc.date.accessioned", "dc.date.available", "dc.identifier.uri", "dspace.entity.type" };

    @Override
    public void setup() throws ParseException {
        this.configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.installItemService = ContentServiceFactory.getInstance().getInstallItemService();
        this.collectionService = ContentServiceFactory.getInstance().getCollectionService();
        this.wsItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
        this.communityService = ContentServiceFactory.getInstance().getCommunityService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
        this.epersonService = EPersonServiceFactory.getInstance().getEPersonService();
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
        this.metadataMigrationMap = new DSpace().getServiceManager().getServiceByName("metadataMigrationMap", Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProjectsMigrationScriptConfiguration<ProjectsMigrationScript> getScriptConfiguration() {
        return new DSpace().getServiceManager().getServiceByName("migrate-projects",
                                                                 ProjectsMigrationScriptConfiguration.class);
    }

    @Override
    public void internalRun() throws Exception {
        assignCurrentUserInContext();
        context.turnOffAuthorisationSystem();
//        Map<String, Collection> names2collection = getCollectionsNameOfProgectTemplate();
        Map<String, Community> names2comm = getCommunitiesNameOfProgectTemplate();
        Map<String, Collection> entities2collection = getCollectionsEntityOfProgectTemplate();  
        Community projectCommunity = getCommunityByProperty("project.parent-community-id");
        if (Objects.isNull(projectCommunity)) {
            throw new RuntimeException("The ProjectCommunity has not been found,"
                                     + " check the propery : project.parent-community-id");
        }
        Community projectTemplateCommunity = getCommunityByProperty("project.template-id");
        Map<String, Group> scopedRoles = new HashMap<>();
        List<Collection> projectCollections = new ArrayList<Collection>();
        
        EPerson coordinator = epersonService.find(context, UUIDUtils.fromString("77e03ec3-e0bb-42c5-8622-006bde576c70")); 
        
        for (Community project : projectCommunity.getSubcommunities()) {
            try {
                System.out.println("Process project " + project.getName());
                projectCollections = new ArrayList<Collection>();
                scopedRoles = new HashMap<>();
                boolean hasSubproject = hasSubproject(project);
                if (hasSubproject) {
                    System.out.println(project.getName());
                }
    
                // rename groups
                renameGroup(project, scopedRoles, coordinator);
    
                clonePolicies(context, project, projectTemplateCommunity, scopedRoles, true);
    
                // check project communities
                checkProjectCommunities(project, entities2collection, names2comm, scopedRoles, projectCollections);
                
                // check project collection, if exist some collection that not exist in
                // project-template will be deleted
                cleanProjects(project, entities2collection.keySet());

                // check project collections, add itemTemplate, deposite workspaceItems
                checkProjectCollection(project, entities2collection, scopedRoles, projectCollections);
    
    
                Item projectItem = createProjectItem(project, false);
    
                // fix workspaceitems and items metadata
                System.out.println("Collectio to process : " + projectCollections.size());
                for (Collection projectCollection : projectCollections) {
                    try {
                        checkCollectionWorkspaceItems(projectCollection, project, projectItem);
                        checkCollectionItems(projectCollection, project, scopedRoles, projectItem);
                    } catch (Exception e) {
                        log.error("Error project " + project.getName() + " collection " + projectCollection.getID()
                        + e.getMessage());
                        continue;
                    }
                }
                
                if (hasSubproject) {
                    migrateSubprojects(project, getSubprojectCommunity(project), projectItem);
                }
                // Step 4 : Print errors
                if (errorDeposit.size() > 0) {
                    System.out.println("List of items which have not been deposited for the community with uuid:"
                                       + project.getID().toString());
                    for (UUID uuid : errorDeposit) {
                        System.out.println(" ->: " + uuid.toString());
                    }
                }
                errorDeposit.clear();
                System.out.println("Done project " + project.getName());
            } catch (Exception e) {
                log.error("Error project " + project.getName() + e.getMessage());
                errorDeposit.clear();
                continue;
            }
        }
        context.complete();
        context.restoreAuthSystemState();
    }

    private boolean hasSubproject(Community parentProject) {
        String subprojectCommunityName = configurationService.getProperty("project.subproject-community-name");
        for (final Community subcommunity : parentProject.getSubcommunities()) {
            if (subcommunity.getName().equals(subprojectCommunityName) && subcommunity.getSubcommunities().size() > 0) {
                return true;
            }
        }
        return false;
    }

    private Community getSubprojectCommunity(Community parentProject) {
        String subprojectCommunityName = configurationService.getProperty("project.subproject-community-name");
        for (final Community subcommunity : parentProject.getSubcommunities()) {
            if (subcommunity.getName().equals(subprojectCommunityName) && subcommunity.getSubcommunities().size() > 0) {
                return subcommunity;
            }
        }
        return null;
    }
    
    private Community getEmptySubprojectCommunity(Community parentProject) {
        String subprojectCommunityName = configurationService.getProperty("project.subproject-community-name");
        for (final Community subcommunity : parentProject.getSubcommunities()) {
            if (subcommunity.getName().equals(subprojectCommunityName) && subcommunity.getSubcommunities().size() == 0) {
                return subcommunity;
            }
        }
        return null;
    }

    private void migrateSubprojects(Community parentProject, Community subprojects, Item projectItem) throws Exception {
        Map<String, Collection> entities2collection = getCollectionsEntityOfSubprojectTemplate();  
        if (Objects.isNull(subprojects)) {
            throw new RuntimeException("The ProjectCommunity has not been found,"
                                     + " check the propery : project.parent-community-id");
        }

        Community emptySubprojectComm = getEmptySubprojectCommunity(parentProject);
        if (!Objects.isNull(emptySubprojectComm)) {
            communityService.delete(context, emptySubprojectComm);
        }

        Map<String, Group> scopedRoles = new HashMap<>();
        List<Collection> projectCollections;
        Item subprojectItem = null;
        for (Community subproject : subprojects.getSubcommunities()) {
            
            projectCollections = new ArrayList<Collection>();

            // Step 1 : rename groups
            renameGroup(subproject, scopedRoles, null);

            // Step 2: check project collection, if exist some collection that not exist in
            // project-template will be deleted
            cleanProjects(subproject, entities2collection.keySet());

            // Step 3 : check project collections, add itemTemplate, deposite workspaceItems
            checkProjectCollection(subproject, entities2collection, scopedRoles, projectCollections);
           
            subprojectItem = createProjectItem(subproject, true);
            
            for (Collection subprojectCollection : projectCollections) {
                if (subprojectCollection.getName().equals("Exploitation plan")) {
                    Iterator<Item> itemIterator = itemService.findAllByCollection(context, subprojectCollection);
                    while (itemIterator.hasNext()) {
                        Item targetItem = itemIterator.next();

                        itemService.replaceMetadata(context, targetItem, "synsicris", "relation",
                                "parentproject", null, projectItem.getName(), projectItem.getID().toString(),
                                Choices.CF_ACCEPTED, 0);
                        itemService.replaceMetadata(context, targetItem, "synsicris", "relation",
                                "project", null, subprojectItem.getName(), subprojectItem.getID().toString(),
                                Choices.CF_ACCEPTED, 0);
                    }
                }
            }
            
        }
        

    }

    private void checkProjectCommunities(Community project, Map<String, Collection> entities2templateCollection,
            Map<String, Community> name2templateCommunity, Map<String, Group> scopedRoles,
            List<Collection> projectCollections)
                    throws SQLException, AuthorizeException {
        Map<String, Community> map = new HashMap<String, Community>(name2templateCommunity);
        
        for (Community subComm : project.getSubcommunities()) {
            if (name2templateCommunity.containsKey(subComm.getName())) {
                clonePolicies(context, subComm, name2templateCommunity.get(subComm.getName()), scopedRoles, true);
                processCommunity(subComm, project, name2templateCommunity.get(subComm.getName()), false);
            }
            for (Collection coll : subComm.getCollections()) {
                if (entities2templateCollection.containsKey(coll.getEntityType())) {
                    clonePolicies(context, subComm, entities2templateCollection.get(coll.getEntityType()),
                            scopedRoles, true);
                    addTemplateItem(coll, entities2templateCollection.get(coll.getEntityType()));
                    projectCollections.add(coll);
                }
            }
            map.remove(subComm.getName());

        }
        
        for (Community templateComm : map.values()) {
            try {
                Community newComm = communityService.create(project, context);
                clonePolicies(context, newComm, templateComm, scopedRoles, true);
                processCommunity(newComm, project, templateComm, false);
                System.out.println("create new community "+ templateComm.getName() + templateComm.getID());
            } catch (SQLException | AuthorizeException e) {
                log.error(e.getMessage());
            }
        }
    }
    
    private void checkProjectCollection(Community project, Map<String, Collection> entities2templateCollection,
            Map<String, Group> scopedRoles, List<Collection> projectCollections) throws SQLException, AuthorizeException {

        Map<String, Collection> map = new HashMap<String, Collection>(entities2templateCollection);

        for (Collection projectCollection : project.getCollections()) {
            System.out.println("Process collection " + projectCollection.getID());
            Collection templateCollection = map.get(projectCollection.getEntityType());
            clonePolicies(context, projectCollection, templateCollection, scopedRoles, true);
            processCollection(projectCollection, project, templateCollection, true);
            map.remove(projectCollection.getEntityType());
            projectCollections.add(projectCollection);
        }

        for (Collection templateCollection : map.values()) {
            try {
                Collection newCollection = collectionService.create(context, project);
                clonePolicies(context, newCollection, templateCollection, scopedRoles, true);
                processCollection(newCollection, project, templateCollection, false);
                System.out.println("create new collection " + newCollection.getID());
                projectCollections.add(newCollection);
            } catch (SQLException | AuthorizeException e) {
                log.error(e.getMessage());
            }
        }
        context.reloadEntity(project);
    }

    private void checkCollectionWorkspaceItems(Collection collection, Community project, Item projectItem) {
        try {
            List<WorkspaceItem> workspaceItems = wsItemService.findByCollection(context, collection);
            System.out.println("Collection " + collection.getID() + " wsi " + workspaceItems.size());
            
            Item itemTemplate = collection.getTemplateItem();
            Item item = null;
            String wsiID = null;
            for (WorkspaceItem workspaceItem : workspaceItems) {
                wsiID = workspaceItem.getID().toString();
                System.out.println("Migrate workspaceitem " + wsiID);
                try {
                    addSubmitterToProjectGroup(project, workspaceItem.getItem());
                    item = workspaceItem.getItem();
                    depositeWorkspaceItem(workspaceItem);
                    item = workspaceItem.getItem();
                    // migrateItemMetadata(item, collection.getEntityType(), itemTemplate);
                } catch (SQLException e) {
                    log.error("Error depositing workspaceItem " + wsiID
                            + " within the collection " + collection.getID().toString());
                    continue;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }


    private void checkCollectionItems(Collection collection, Community project, Map<String, Group> scopedRoles,
            Item projectItem) {
        try {
            Iterator<Item> items = itemService.findAllByCollection(context, collection);
            Item itemTemplate = collection.getTemplateItem();
            String itemID = null;
            while (items.hasNext()) {
                Item item = items.next();
                itemID = item.getID().toString();
                try {
                    addSubmitterToProjectGroup(project, item);
                    migrateItemMetadata(item, collection.getEntityType(), itemTemplate, scopedRoles, projectItem);
                } catch (SQLException e) {
                    log.error("Error processing Item " + itemID
                            + " within the collection " + collection.getID().toString());
                    continue;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        
    }

    private Item createProjectItem(Community project, boolean isSubproject) throws SQLException, AuthorizeException {
        
        Item projectItem;
        List<MetadataValue> projectMetadataList = communityService.getMetadataByMetadataString(project,
                "synsicris.relation.entity_project");
        if (projectMetadataList.size() > 0 && StringUtils.isNotBlank(projectMetadataList.get(0).getAuthority())) {
            MetadataValue projectMetadata = projectMetadataList.get(0);
            UUID uuid = UUIDUtils.fromString(projectMetadata.getAuthority());
            projectItem = itemService.find(context, uuid);
        } else {
            Collection parentProjectColl = null;
            String entitTypeToFind = isSubproject ? "Project" : "parentproject";
            for (Collection coll : project.getCollections()) {
                if (coll.getEntityType().equals(entitTypeToFind)) {
                    parentProjectColl = coll;
                    break;
                }
            }
            WorkspaceItem workspaceItem = wsItemService.create(context, parentProjectColl, true);
            projectItem = installItemService.installItem(context, workspaceItem);
            collectionService.addItem(context, parentProjectColl, projectItem);
            itemService.addMetadata(context, projectItem, "dc", "title", null, null, project.getName(),
                    null, Choices.CF_UNSET);
            communityService.addMetadata(context, project, "synsicris", "relation", "entity_project", null, project.getName(),
                    projectItem.getID().toString(), Choices.CF_ACCEPTED);
        }
        return projectItem; 
    }

    private void depositeWorkspaceItem(WorkspaceItem workspaceItem) {
        try {
            System.out.println("Deposit workspaceitem " + workspaceItem.getID().toString());
            installItemService.installItem(context, workspaceItem);
        } catch (SQLException | AuthorizeException e) {
            log.error(e.getMessage());
            errorDeposit.add(workspaceItem.getItem().getID());
        }
    }

    private void addSubmitterToProjectGroup(Community project, Item item) throws SQLException {
        EPerson submitter = item.getSubmitter();
        StringBuilder memberGroupName = new StringBuilder("project_")
                                                  .append(project.getID().toString())
                                                  .append("_members_group");
        Group projectGroup = groupService.findByName(context, memberGroupName.toString());
        if (!groupService.isMember(context, submitter, projectGroup)) {
            groupService.addMember(context, projectGroup, submitter);
        }
    }

    private void addTemplateItem(Collection projectCollection, Collection templateCollection) {
        try {
            if (!Objects.isNull(templateCollection.getTemplateItem())) {
                Item templateTemplateItem = templateCollection.getTemplateItem();
                Item projectTemplateItem = projectCollection.getTemplateItem();
                if (Objects.isNull(projectTemplateItem)) {
                    projectTemplateItem = itemService.createTemplateItem(context, projectCollection);
                } else {
                    itemService.clearMetadata(context, projectTemplateItem, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
                }
                List<MetadataValue> templateMetadata = templateTemplateItem.getMetadata();
                for (MetadataValue metadata : templateMetadata) {
                    itemService.addMetadata(context, projectTemplateItem, metadata.getSchema(), metadata.getElement(),
                            metadata.getQualifier(), metadata.getLanguage(), metadata.getValue());
                }
            }
        } catch (SQLException | AuthorizeException e) {
            log.error(e.getMessage());
        }
    }

    private void processCollection(Collection projectCollection, Community project, Collection templateCollection,
            boolean clear) throws SQLException {
        //if (!StringUtils.equals(projectCollection.getName(), "Projects")) {
            cloneMetadata(context, collectionService, projectCollection, templateCollection, clear);
            addTemplateItem(projectCollection, templateCollection);
            context.reloadEntity(projectCollection);
        //}
    }


    private void processCommunity(Community projectCommunity, Community project, Community templateCommunity,
            boolean clear) throws SQLException {
        //if (!StringUtils.equals(projectCollection.getName(), "Projects")) {
            cloneMetadata(context, communityService, projectCommunity, templateCommunity, clear);
            context.reloadEntity(projectCommunity);
        //}
    }

    private void cleanProjects(Community project, Set<String> entities) {
        for (Collection collection : project.getCollections()) {
            deleteCollectionIfNotExistInTemplate(collection, entities);
        }
    }

    private void deleteCollectionIfNotExistInTemplate(Collection collection, Set<String> entities) {
        if (!entities.contains(collection.getEntityType())) {
            try {
                System.out.println("Delete collection " + collection.getID());
                collectionService.delete(context, collection);
            } catch (SQLException | AuthorizeException | IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void renameGroup(Community community, Map<String, Group> scopedRoles, EPerson coordinator)
            throws SQLException, AuthorizeException {
        String submitterGroupId = configurationService.getProperty("project.creation.group");
        Group submitterGroup = groupService.findByIdOrLegacyId(context, submitterGroupId);
        
        
        StringBuilder groupName = new StringBuilder("project_")
                                            .append(community.getID().toString())
                                            .append("_group");
        StringBuilder newAdminName = new StringBuilder("project_")
                .append(community.getID().toString())
                .append("_admin_group");

        Group adminGroup = groupService.findByName(context, groupName.toString());
        if (!Objects.isNull(adminGroup)) {
        
            groupService.setName(adminGroup, newAdminName.toString());
            groupService.update(context, adminGroup);

        } else {
            adminGroup = groupService.findByName(context, newAdminName.toString());
            if (Objects.isNull(adminGroup)) {
                adminGroup = groupService.create(context);
                
                groupService.setName(adminGroup, newAdminName.toString());
                groupService.update(context, adminGroup);
                groupService.addMember(context, adminGroup, context.getCurrentUser());
            }
        }
        if (!Objects.isNull(coordinator)) {
            groupService.addMember(context, adminGroup, coordinator);
        }
        scopedRoles.put("project_template_admin_group", adminGroup);
        
        StringBuilder memberGroupName = new StringBuilder("project_")
                                                  .append(community.getID().toString())
                                                  .append("_members_group");

        Group newMemberGroup = groupService.findByName(context, memberGroupName.toString());
        if (Objects.isNull(newMemberGroup)) {
            newMemberGroup = groupService.create(context);
    
            groupService.setName(newMemberGroup, memberGroupName.toString());
            groupService.update(context, newMemberGroup);
    
            for (EPerson member : adminGroup.getMembers()) {
                groupService.addMember(context, newMemberGroup, member);
                groupService.addMember(context, submitterGroup, member);
            }
        }
        
        if (!Objects.isNull(coordinator)) {
            groupService.addMember(context, adminGroup, coordinator);
            groupService.addMember(context, newMemberGroup, coordinator);
        }

        scopedRoles.put("project_template_members_group", newMemberGroup);
    }

    private Map<String, Community> getCommunitiesNameOfProgectTemplate() throws SQLException {
        Map<String, Community>  commNames = new HashMap<String, Community>();
        Community projectTemplateCommunity = getCommunityByProperty("project.template-id");
        if (Objects.isNull(projectTemplateCommunity)) {
            throw new RuntimeException(
                    "The project-template Community has not been found, check the propery : project.template-id");
        }
        for (Community comm : projectTemplateCommunity.getSubcommunities()) {
            commNames.put(comm.getName(), comm);
        }
        return commNames;
    }

    private Map<String, Collection> getCollectionsNameOfProgectTemplate() throws SQLException {
        Map<String, Collection>  collectionNames = new HashMap<String, Collection>();
        Community projectTemplateCommunity = getCommunityByProperty("project.template-id");
        if (Objects.isNull(projectTemplateCommunity)) {
            throw new RuntimeException(
                    "The project-template Community has not been found, check the propery : project.template-id");
        }
        for (Collection collection : projectTemplateCommunity.getCollections()) {
            collectionNames.put(collection.getName(), collection);
        }
        return collectionNames;
    }

    private Map<String, Collection> getCollectionsEntityOfProgectTemplate() throws SQLException {
        Map<String, Collection>  collectionEntities = new HashMap<String, Collection>();
        Community projectTemplateCommunity = getCommunityByProperty("project.template-id");
        if (Objects.isNull(projectTemplateCommunity)) {
            throw new RuntimeException(
                    "The project-template Community has not been found, check the propery : project.template-id");
        }
        for (Collection collection : projectTemplateCommunity.getCollections()) {
            collectionEntities.put(collection.getEntityType(), collection);
        }
        for (Community subcomm : projectTemplateCommunity.getSubcommunities()) {
            for (Collection collection : subcomm.getCollections()) {
                collectionEntities.put(collection.getEntityType(), collection);
            }
        }
        return collectionEntities;
    }


    private Map<String, Collection> getCollectionsEntityOfSubprojectTemplate() throws SQLException {
        Map<String, Collection>  collectionEntities = new HashMap<String, Collection>();
        Community projectTemplateCommunity = getCommunityByProperty("subproject.template-id");
        if (Objects.isNull(projectTemplateCommunity)) {
            throw new RuntimeException(
                    "The project-template Community has not been found, check the propery : project.template-id");
        }
        for (Collection collection : projectTemplateCommunity.getCollections()) {
            collectionEntities.put(collection.getEntityType(), collection);
        }
        return collectionEntities;
    }

    private Community getCommunityByProperty(String property) throws SQLException {
        String communityUuid = configurationService.getProperty(property);
        if (StringUtils.isNotBlank(communityUuid)) {
            return communityService.find(context, UUID.fromString(communityUuid));
        }
        return null;
    }

    private <T extends DSpaceObject> void cloneMetadata(Context context, DSpaceObjectService<T> service, T target,
            T dsoToClone, boolean clear) throws SQLException {
        if (clear) {
            for (MetadataValue metadata : dsoToClone.getMetadata()) {
                if (isMetadataToSkip(service, target, metadata) || StringUtils.isBlank(metadata.getValue())) {
                    continue;
                }
                service.clearMetadata(context, target, metadata.getSchema(), metadata.getElement(),
                        metadata.getQualifier(), null);
            } 
        }
        for (MetadataValue metadata : dsoToClone.getMetadata()) {
            if (isMetadataToSkip(service, target, metadata)) {
                continue;
            }
            service.addMetadata(context, target, metadata.getSchema(), metadata.getElement(),
                    metadata.getQualifier(), metadata.getLanguage(), metadata.getValue());
        }
    }

    private void clonePolicies(Context context, DSpaceObject clone, DSpaceObject objectToClone,
            Map<String, Group> scopedRoles, boolean clear) throws SQLException, AuthorizeException {
        System.out.println("Clone policies " + clone.getID());
        if (clear) {
            authorizeService.removeAllPolicies(context, clone);
        }

        for (ResourcePolicy policy : objectToClone.getResourcePolicies()) {
            Group group = policy.getGroup();
            if (group != null && scopedRoles.containsKey(group.getName())) {
                authorizeService.addPolicy(context, clone, policy.getAction(), scopedRoles.get(group.getName()));
            }
        }
    }

    private void migrateItemMetadata(Item targetItem, String entityType, Item templateItem, 
            Map<String, Group> scopedRoles, Item projectItem) throws SQLException {
        if (!Objects.isNull(targetItem)) {
            System.out.println("Migrate Item " + targetItem.getID());
            String itemEntityType = itemService.getMetadataFirstValue(targetItem, "dspace", "entity", "type", null);
            if (StringUtils.isBlank(itemEntityType) || !itemEntityType.equals(entityType)) {
                itemService.replaceMetadata(context, targetItem, "dspace", "entity", "type", null,
                        entityType, null, Choices.CF_UNSET, 0);
            }
            if (!Objects.isNull(templateItem)) {
                List<MetadataValue> templateMetadata = templateItem.getMetadata();
                for (MetadataValue metadata : templateMetadata) {
                    String value = metadata.getValue();
                    String authority = metadata.getAuthority();
                    int confidence = metadata.getConfidence();
                    if (metadata.getMetadataField().toString('.').equals("synsicris.​subject.​agrovoc")) {
                        continue;
                    }
                    if (metadata.getMetadataField().toString('.').equals("cris.policy.group")) {
                        value = scopedRoles.get("project_template_members_group").getName();
                        authority = scopedRoles.get("project_template_members_group").getID().toString();
                        confidence = Choices.CF_ACCEPTED;
                    }
                    if (metadata.getMetadataField().toString('.').equals("synsicris.relation.parentproject")) {
                        value = projectItem.getName();
                        authority = projectItem.getID().toString();
                        confidence = Choices.CF_ACCEPTED;
                    }
                    itemService.replaceMetadata(context, targetItem, metadata.getSchema(), metadata.getElement(),
                            metadata.getQualifier(), metadata.getLanguage(), value, authority,
                            confidence, metadata.getPlace());
                }
            }
            
            List<MetadataValue> itemMetadata = targetItem.getMetadata();
            List<MetadataField> metadataFields = new ArrayList<MetadataField>();
            for (MetadataValue metadata : itemMetadata) {
                if (!metadataFields.contains(metadata.getMetadataField())) {
                    metadataFields.add(metadata.getMetadataField());
                }
            }
            
            
            for (MetadataField metadataField : metadataFields) {
                try {
                    System.out.println("process " + metadataField.toString('.') + " item " + targetItem.getID());
                    migrateMetadata(targetItem, entityType, metadataField);
                } catch (SQLException e) {
                    log.error("Error migrating metadata " + metadataField.toString('.')
                            + "for item " + targetItem.getID().toString());
                    continue;
                }

            }
        }
    }
    
    private void migrateMetadata(Item targetItem, String entityType, MetadataField metadataField) throws SQLException {
        if (!Objects.isNull(targetItem) && !Objects.isNull(metadataField)) {
            if (metadataMigrationMap.containsKey(metadataField.toString('.'))) {
                MetadataMigrate metadataMigrate = metadataMigrationMap.get(metadataField.toString('.'));
                MetadataMigrateAction matchingAction = findMatchingAction(metadataMigrate, entityType);
                if (!Objects.isNull(matchingAction)) {
                    processMetadataMigrateAction(targetItem, matchingAction, metadataField);
                }
            }
        }
    }

    private MetadataMigrateAction findMatchingAction(MetadataMigrate metadataMigrate, String entityType) {
        MetadataMigrateAction matchingAction = null;
        if (!Objects.isNull(metadataMigrate)) {
            MetadataMigrateAction generalAction = null;
            for(MetadataMigrateAction action: metadataMigrate.actions) {
                if (action.entityType.equals("ALL")) {
                    generalAction = action;
                }
                if (action.entityType.equals(entityType)) {
                    matchingAction = action;
                    break;
                }
            }
            if (Objects.isNull(matchingAction)) {
                matchingAction = generalAction;
            }
        }
        
        return matchingAction;
    }

    private void processMetadataMigrateAction(Item targetItem, MetadataMigrateAction matchingAction,
            MetadataField metadataField) throws SQLException {
        if (!Objects.isNull(targetItem) && !Objects.isNull(matchingAction)) {
            List<MetadataValue> metadataValues = itemService.getMetadata(targetItem,
                    metadataField.getMetadataSchema().getName(), metadataField.getElement(),
                    metadataField.getQualifier(), null);
            switch (matchingAction.getAction()) {
                case "remove":
                    System.out.println("Remove metdata " + metadataField.toString('.') + " item " + targetItem.getID());
                    itemService.removeMetadataValues(context, targetItem, metadataValues);
                    break;
                case "rename":
                    MetadataFieldConfig newMetdata = matchingAction.getMetadata();
                    System.out.println("Replace metdata " + metadataField.toString('.') + " with metadata " +
                            newMetdata.toString() + " item " + targetItem.getID());
                    for (MetadataValue metadata : metadataValues) {
                        itemService.addMetadata(context, targetItem, newMetdata.getSchema(), newMetdata.getElement(),
                                newMetdata.getQualifier(), metadata.getLanguage(), metadata.getValue(),
                                metadata.getAuthority(), metadata.getConfidence(), metadata.getPlace());
                    }     
                    itemService.removeMetadataValues(context, targetItem, metadataValues);
                default:
                    break;
                }
        }
    }
    
    private <T extends DSpaceObject> boolean isMetadataToSkip(DSpaceObjectService<T> service, T target,
            MetadataValue metadata) {
        String metadataName = metadata.getSchema() + "." + metadata.getElement();
        if (StringUtils.isNotBlank(metadata.getQualifier())) {
            metadataName += "." + metadata.getQualifier();
        }

        if (Arrays.stream(matadataToSkip).anyMatch(metadataName::equals)) {
            List<MetadataValue> metadataList = service.getMetadataByMetadataString(target, metadataName);
            return metadataList.size() > 0;
        }
        return false;
    }

    private void assignCurrentUserInContext() throws SQLException {
        context = new Context();
        EPerson ePerson = null;
        UUID uuid = getEpersonIdentifier();
        if (uuid != null) {
            ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
        } else {
            ePerson = EPersonServiceFactory.getInstance().getEPersonService().findByEmail(context, "shared@synsicris.de");
        }
        context.setCurrentUser(ePerson);
    }
    

}