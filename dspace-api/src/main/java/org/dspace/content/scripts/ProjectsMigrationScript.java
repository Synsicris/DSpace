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
import java.util.HashMap;
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
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
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
import org.dspace.eperson.service.GroupService;
import org.dspace.metrics.UpdateCrisMetricsWithExternalSource;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link DSpaceRunnable}
 * The purpose of this script is to migrate projects according to a certain structure .
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectsMigrationScript extends
             DSpaceRunnable<ProjectsMigrationScriptConfiguration<ProjectsMigrationScript>> {

    private static final Logger log = LogManager.getLogger(UpdateCrisMetricsWithExternalSource.class);

    private List<UUID> errorDeposit = new LinkedList<UUID>();

    private ConfigurationService configurationService;

    private InstallItemService installItemService;

    private WorkspaceItemService wsItemService;

    private CollectionService collectionService;

    private CommunityService communityService;

    private ItemService itemService;

    private GroupService groupService;

    private Context context;

    @Override
    public void setup() throws ParseException {
        this.configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.installItemService = ContentServiceFactory.getInstance().getInstallItemService();
        this.collectionService = ContentServiceFactory.getInstance().getCollectionService();
        this.wsItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
        this.communityService = ContentServiceFactory.getInstance().getCommunityService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
        this.itemService = ContentServiceFactory.getInstance().getItemService();
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
        Map<String, Collection> names2collection = getCollectionsNameOfProgectTemplate();
        Community projectCommunity = getCommunityByProperty("project.parent-community-id");
        if (Objects.isNull(projectCommunity)) {
            throw new RuntimeException("The ProjectCommunity has not been found,"
                                     + " check the propery : project.parent-community-id");
        }
        for (Community project : projectCommunity.getSubcommunities()) {

            // Step 1 : rename groups
            renameGroup(project);

            // Step 2: check project collection, if exist some collection that not exist in
            // project-template will be deleted
            cleanProjects(project, names2collection.keySet());

            // Step 3 : check project collections, add itemTemplate, deposite workspaceItems
            checkProjectCollection(project, names2collection);

            // Step 4 : Print errors
            System.out.println("List of items which have not been deposited for the community with uuid:"
                               + project.getID().toString());
            for (UUID uuid : errorDeposit) {
                System.out.println(" ->: " + uuid.toString());
            }
            errorDeposit.clear();
        }
    }

    private void checkProjectCollection(Community project, Map<String, Collection> names2collection)
            throws SQLException {
        if (project.getCollections().size() == names2collection.size()) {
            return;
        }
        Map<String, Collection> map = new HashMap<String, Collection>(names2collection);

        for (Collection collection : project.getCollections()) {
            processCollection(collection, project);
            map.remove(collection.getName());
        }

        for (Collection collection : map.values()) {
            try {
                Collection newCollection = collectionService.create(context, project);
                cloneMetadata(context, collectionService, newCollection, collection);
                processCollection(collection, project);
            } catch (SQLException | AuthorizeException e) {
                log.error(e.getMessage());
            }
        }
        context.reloadEntity(project);
    }

    private void checkWorkspaceItem(Collection collection, Community project) {
        try {
            List<WorkspaceItem> workspaceItems = wsItemService.findByCollection(context, collection);
            for (WorkspaceItem workspaceItem : workspaceItems) {
                itemService.replaceMetadata(context, workspaceItem.getItem(), "cris", "policy", "group", null,
                                                          "GROUP_POLICY_PLACEHOLDER", null, Choices.CF_UNSET, 0);
                itemService.replaceMetadata(context, workspaceItem.getItem(), "cris", "project", "shared", null,
                                                                              "project", null, Choices.CF_UNSET, 0);
                addSubmitterToProjectGroup(project, workspaceItem);
                depositeWorkspaceItem(workspaceItem);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private void depositeWorkspaceItem(WorkspaceItem workspaceItem) {
        try {
            installItemService.installItem(context, workspaceItem);
        } catch (SQLException | AuthorizeException e) {
            errorDeposit.add(workspaceItem.getItem().getID());
        }
    }

    private void addSubmitterToProjectGroup(Community project, WorkspaceItem workspaceItem) throws SQLException {
        EPerson submitter = workspaceItem.getSubmitter();
        StringBuilder memberGroupName = new StringBuilder("project_")
                                                  .append(project.getID().toString())
                                                  .append("_members_group");
        Group projectGroup = groupService.findByName(context, memberGroupName.toString());
        if (!groupService.isMember(context, submitter, projectGroup)) {
            groupService.addMember(context, projectGroup, submitter);
        }
    }

    private void addTemplateItem(Collection collection) {
        try {
            if (Objects.isNull(collection.getTemplateItem())) {
                Item template = itemService.createTemplateItem(context, collection);
                itemService.addMetadata(context, template, "cris", "policy", "group", null, "GROUP_POLICY_PLACEHOLDER");
                itemService.addMetadata(context, template, "cris", "project", "shared", null, "project");
            }
        } catch (SQLException | AuthorizeException e) {
            log.error(e.getMessage());
        }
    }

    private void processCollection(Collection collection, Community project) {
        if (!StringUtils.equals(collection.getName(), "Projects")) {
            addTemplateItem(collection);
            checkWorkspaceItem(collection, project);
        }
    }

    private void cleanProjects(Community project, Set<String> names) {
        for (Collection collection : project.getCollections()) {
            deleteCollectionIfNotExistInTemplate(collection, names);
        }
    }

    private void deleteCollectionIfNotExistInTemplate(Collection collection, Set<String> names) {
        if (!names.contains(collection.getName())) {
            try {
                collectionService.delete(context, collection);
            } catch (SQLException | AuthorizeException | IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void renameGroup(Community community) throws SQLException, AuthorizeException {
        StringBuilder groupName = new StringBuilder("project_")
                                            .append(community.getID().toString())
                                            .append("_group");

        Group group = groupService.findByName(context, groupName.toString());

        StringBuilder newName = new StringBuilder("project_")
                                          .append(community.getID().toString())
                                          .append("_admin_group");

        groupService.setName(group, newName.toString());
        groupService.update(context, group);

        StringBuilder memberGroupName = new StringBuilder("project_")
                                                  .append(community.getID().toString())
                                                  .append("_members_group");

        Group newMemberGroup = groupService.create(context);
        groupService.setName(group, memberGroupName.toString());
        groupService.update(context, newMemberGroup);

        for (EPerson member : group.getMembers()) {
            groupService.addMember(context, newMemberGroup, member);
        }
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

    private Community getCommunityByProperty(String property) throws SQLException {
        String communityUuid = configurationService.getProperty(property);
        if (StringUtils.isBlank(communityUuid)) {
            return communityService.find(context, UUID.fromString(communityUuid));
        }
        return null;
    }

    private <T extends DSpaceObject> void cloneMetadata(Context context, DSpaceObjectService<T> service, T target,
            T dsoToClone) throws SQLException {
        for (MetadataValue metadata : dsoToClone.getMetadata()) {
            service.addMetadata(context, target, metadata.getSchema(), metadata.getElement(), metadata.getQualifier(),
                    null, metadata.getValue());
        }
    }

    private void assignCurrentUserInContext() throws SQLException {
        context = new Context();
        UUID uuid = getEpersonIdentifier();
        if (uuid != null) {
            EPerson ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
            context.setCurrentUser(ePerson);
        }
    }

}