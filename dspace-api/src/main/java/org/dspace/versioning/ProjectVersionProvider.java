/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.versioning;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.dspace.authority.service.AuthorityValueService.REFERENCE;
import static org.dspace.project.util.ProjectConstants.MD_LAST_VERSION;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IteratorUtils;
import org.dspace.authority.service.ItemSearchService;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.RelationshipType;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.EntityTypeService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.RelationshipService;
import org.dspace.content.service.RelationshipTypeService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.exception.SQLRuntimeException;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultItemIterator;
import org.dspace.discovery.indexobject.IndexableCommunity;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.util.UUIDUtils;
import org.dspace.versioning.service.VersioningService;
import org.dspace.xmlworkflow.storedcomponents.service.XmlWorkflowItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link ItemVersionProvider} that create a new version of
 * the whole project related to the given item.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class ProjectVersionProvider extends AbstractVersionProvider implements ItemVersionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionProvider.class);

    public static final String VERSION_RELATIONSHIP = "isVersionOf";

    public static final String UNIQUE_ID_REFERENCE_PREFIX = "UNIQUE-ID";

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    protected VersioningService versioningService;

    @Autowired
    protected WorkspaceItemService workspaceItemService;

    @Autowired
    protected XmlWorkflowItemService workflowItemService;

    @Autowired
    protected RelationshipService relationshipService;

    @Autowired
    protected RelationshipTypeService relationshipTypeService;

    @Autowired
    protected EntityTypeService entityTypeService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected ItemSearchService itemSearchService;

    @Override
    public Item createNewItem(Context context, Item item) {
        context.turnOffAuthorisationSystem();
        try {
            WorkspaceItem workspaceItem = createWorkspaceItemCopy(context, item);
            return installItemService.installItem(context, workspaceItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    @Override
    public Item updateItemState(Context context, Item itemNew, Item previousItem, Version version) {

        if (isCascadeVersioningNotEnabled()) {
            return itemNew;
        }

        String uniqueIdMetadataValue = composeUniqueId(previousItem.getID().toString(), version);
        addUniqueIdMetadata(context, itemNew, uniqueIdMetadataValue);
        addVersionMetadata(context, itemNew, String.valueOf(version.getVersionNumber()));

        replaceAuthoritiesWithWillBeReferenced(context, itemNew, version);

        if (isProject(previousItem)) {
            markAsLastVersion(context, itemNew, previousItem);
            createNewProjectVersion(context, previousItem, version);
        }

        updateItem(context, itemNew);
        itemNew.setLastModified(previousItem.getLastModified());

        return itemNew;

    }

    private void markAsLastVersion(Context context, Item newItem, Item previousItem) {
        findAllVersions(context, previousItem).forEach(item -> removeIsLastVersionMarker(context, item));
        setIsLastVersionMetadata(context, newItem);
    }

    private Stream<Item> findAllVersions(Context context, Item item) {
        try {
            RelationshipType versionRelationship = getVersionRelationship(context, item);
            return relationshipService.findByItemAndRelationshipType(context, item, versionRelationship).stream()
                .map(relationship -> relationship.getLeftItem());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeIsLastVersionMarker(Context context, Item item) {
        try {
            List<MetadataValue> values = itemService.getMetadataByMetadataString(item, MD_LAST_VERSION.toString());
            itemService.removeMetadataValues(context, item, values);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void createNewProjectVersion(Context context, Item projectItem, Version version) {

        Iterator<Item> itemIterator = findItemsOfProject(context, projectItem);

        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            if (isVersionItem(item) || projectConsumerService.isProjectItem(item)) {
                continue;
            }

            versioningService.createNestedVersion(context, item, version);

        }

    }

    private void updateItem(Context context, Item item) {
        context.turnOffAuthorisationSystem();
        try {
            itemService.update(context, item);
        } catch (SQLException | AuthorizeException e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private boolean isVersionItem(Item item) {
        return isNotEmpty(itemService.getMetadataFirstValue(item, "synsicris", "uniqueid", null, Item.ANY));
    }

    @Override
    public void deleteVersionedItem(Context c, Version versionToDelete, VersionHistory history) throws SQLException {
        Item projectItem = versionToDelete.getItem();
        if (!projectConsumerService.isProjectItem(projectItem)) {
            return;
        }

        Stream<Item> itemsToDelete =
            findVersionedItems(c, projectItem, String.valueOf(versionToDelete.getVersionNumber()));

        deleteItems(c, itemsToDelete);

        forceVisibilityPreviousVersion(c, versionToDelete, history, projectItem);
    }

    private void forceVisibilityPreviousVersion(
        Context c, Version versionToDelete, VersionHistory history, Item projectItem
    ) throws SQLException {
        Boolean lastVersionVisible =
            Boolean.valueOf(
                this.itemService.getMetadataFirstValue(
                    projectItem, ProjectConstants.MD_LAST_VERSION_VISIBLE.schema,
                    ProjectConstants.MD_LAST_VERSION_VISIBLE.element,
                    ProjectConstants.MD_LAST_VERSION_VISIBLE.qualifier,
                    null
                )
            );
        if (lastVersionVisible) {
            Optional<Item> previousVersionItem =
                this.versioningService.getVersionsByHistory(c, history)
                    .stream()
                    .filter(v -> v.getItem() != null && v.getVersionNumber() < versionToDelete.getVersionNumber())
                    .filter(
                        v ->
                        Boolean.valueOf(
                            this.itemService.getMetadataFirstValue(
                                v.getItem(), ProjectConstants.MD_VERSION_VISIBLE.schema,
                                ProjectConstants.MD_VERSION_VISIBLE.element,
                                ProjectConstants.MD_VERSION_VISIBLE.qualifier,
                                null
                            )
                        )
                    )
                    .sorted((v1, v2) -> v2.getVersionNumber() - v1.getVersionNumber())
                    .findFirst()
                    .map(Version::getItem);
            if (previousVersionItem.isPresent()) {
                Item previousProjectItem = previousVersionItem.get();
                this.itemService
                    .setMetadataSingleValue(
                        c, previousProjectItem, ProjectConstants.MD_VERSION_VISIBLE, null, "true"
                );
                this.setIsLastVersionMetadata(c, previousProjectItem);
                try {
                    this.itemService.update(c, previousProjectItem);
                } catch (AuthorizeException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void deleteItems(Context c, Stream<Item> items) {
        items.forEach(item -> {
            try {
                deleteItem(c, item);
            } catch (SQLException | AuthorizeException | IOException e) {
                throw new RuntimeException(
                    "Error while deleting the versioned item with id: " + item.getID(),
                    e
                );
            }
        });
    }

    private Stream<Item> findVersionedItems(Context c, Item projectItem, String versionNumber) throws SQLException {
        Community community = projectConsumerService.getProjectCommunity(c, projectItem);
        Iterator<Item> itemIterator =
            projectConsumerService
                .findVersionedItemsRelatedToProject(c, community, projectItem, versionNumber);
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(itemIterator, Spliterator.ORDERED), false
        );
    }

    private void deleteItem(Context context, Item item) throws SQLException, AuthorizeException, IOException {
        if (isItemInWorkspace(context, item)) {
            workspaceItemService.deleteAll(context, workspaceItemService.findByItem(context, item));
        } else if (isItemInWorkflow(context, item)) {
            workflowItemService.delete(context, workflowItemService.findByItem(context, item));
        } else {
            itemService.delete(context, item);
        }
    }

    private boolean isItemInWorkspace(Context context, Item item) throws SQLException {
        return workspaceItemService.findByItem(context, item) != null;
    }

    private boolean isItemInWorkflow(Context context, Item item) throws SQLException {
        return workflowItemService.findByItem(context, item) != null;
    }

    private Iterator<Item> findItemsOfProject(Context context, Item projectItem) {
        try {

            Community projectCommunity = projectConsumerService.getProjectCommunity(context, projectItem);
            if (projectCommunity == null) {
                return IteratorUtils.emptyIterator();
            }

            return findItemsByCommunity(context, projectCommunity);

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private Iterator<Item> findItemsByCommunity(Context context, Community projectCommunity) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.setScopeObject(new IndexableCommunity(projectCommunity));
        String filterQueries = "-(synsicris.uniqueid:*)";
        discoverQuery.addFilterQueries(filterQueries);
        discoverQuery.setMaxResults(10000);
        return new DiscoverResultItemIterator(context, new IndexableCommunity(projectCommunity), discoverQuery);
    }

    private boolean isCascadeVersioningNotEnabled() {
        return !configurationService.getBooleanProperty("versioning.project-version-provider.cascade-enabled", true);
    }

    private WorkspaceItem createWorkspaceItemCopy(Context context, Item item) throws Exception {

        WorkspaceItem workspaceItem = workspaceItemService.create(context, item.getOwningCollection(), false);

        createIsVersionOfRelationship(context,workspaceItem.getItem(), item );
        copyMetadata(context, workspaceItem.getItem(), item);
        createBundlesAndAddBitstreams(context, workspaceItem.getItem(), item);
        copyResourcePolicies(context, workspaceItem.getItem(), item);
        addFunderGroupIfConfigured(context, workspaceItem.getItem());

        return workspaceItem;

    }

    private void createIsVersionOfRelationship(Context context, Item newItem, Item previousItem)
        throws SQLException, AuthorizeException {
        RelationshipType relationshipType = getVersionRelationship(context, previousItem);
        relationshipService.create(context, newItem, previousItem, relationshipType, false);
    }

    private RelationshipType getVersionRelationship(Context context, Item item)
        throws IllegalArgumentException, SQLException {

        RelationshipType relationshipType = findRelationshipType(context, item, VERSION_RELATIONSHIP);
        if (relationshipType == null) {
            throw new IllegalArgumentException("No relationship type found for " + VERSION_RELATIONSHIP);
        }

        return relationshipType;
    }

    private void copyResourcePolicies(Context context, Item newItem, Item previousItem) throws Exception {

        List<ResourcePolicy> policies = authorizeService.getPolicies(context, previousItem).stream()
            .filter(policy -> isNotRelatedToAnonymousGroup(policy))
            .collect(Collectors.toList());

        authorizeService.addPolicies(context, policies, newItem);

    }

    private boolean isNotRelatedToAnonymousGroup(ResourcePolicy policy) {
        return policy.getGroup() == null || !policy.getGroup().getName().equals(Group.ANONYMOUS);
    }

    private void addFunderGroupIfConfigured(Context context, Item item) throws SQLException, AuthorizeException {
        UUID funderGroupId = UUIDUtils.fromString(configurationService.getProperty("project.funder_programme.group"));
        if (funderGroupId == null) {
            return;
        }

        Group funderGroup = groupService.find(context, funderGroupId);
        if (funderGroup == null) {
            LOGGER.warn("No funder group found by id: " + funderGroupId);
        }

        authorizeService.addPolicy(context, item, Constants.READ, funderGroup);
    }

    private void addUniqueIdMetadata(Context context, Item item, String metadataValue) {
        try {
            itemService.addMetadata(context, item, "synsicris", "uniqueid", null, null, metadataValue);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void addVersionMetadata(Context context, Item item, String metadataValue) {
        try {
            itemService.addMetadata(context, item, "synsicris", "version", null, null, metadataValue);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void setIsLastVersionMetadata(Context context, Item item) {
        try {
            itemService.setMetadataSingleValue(context, item, ProjectConstants.MD_LAST_VERSION.schema,
                ProjectConstants.MD_LAST_VERSION.element, ProjectConstants.MD_LAST_VERSION.qualifier, null, "true");
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void replaceAuthoritiesWithWillBeReferenced(Context context, Item itemNew, Version version) {
        itemNew.getMetadata().stream()
            .filter(metadataValue -> isNotBlank(metadataValue.getAuthority()))
            .filter(metadataValue -> isNotSharedReference(context, metadataValue.getAuthority(), itemNew))
            .forEach(metadataValue -> replaceAuthorityWithWillBeReferenced(metadataValue, version));
    }

    private boolean isNotSharedReference(Context context, String authority, Item itemNew) {
        Item item = find(context, authority, itemNew);
        return item != null ? isNotSharedItem(context, item) : false;
    }

    private boolean isNotSharedItem(Context context, Item item) {

        String sharedCommunityName = configurationService.getProperty("shared.community-name", "Shared");

        try {
            return itemService.getCommunities(context, item).stream()
                .noneMatch(community -> sharedCommunityName.equals(community.getName()));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }

    }

    private void replaceAuthorityWithWillBeReferenced(MetadataValue metadataValue, Version version) {
        String uniqueId = composeUniqueId(metadataValue.getAuthority(), version);
        metadataValue.setAuthority(REFERENCE + UNIQUE_ID_REFERENCE_PREFIX + "::" + uniqueId);
    }

    private String composeUniqueId(String itemId, Version version) {
        return itemId + "_" + version.getVersionNumber();
    }

    private boolean isProject(Item item) {
        return projectConsumerService.isProjectItem(item);
    }

    private RelationshipType findRelationshipType(Context context, Item item, String relationship)
        throws SQLException, IllegalArgumentException {

        EntityType type = entityTypeService.findByItem(context, item);
        if (type == null) {
            return null;
        }

        return relationshipTypeService.findByLeftwardOrRightwardTypeName(context, relationship).stream()
            .filter(relationshipType -> type.equals(relationshipType.getLeftType())).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Relationship type " + relationship + " does not exist"));
    }

    private Item find(Context context, String id, Item source) {
        try {
            return itemSearchService.search(context, id, source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
