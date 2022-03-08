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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
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
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.util.UUIDUtils;
import org.dspace.versioning.service.VersioningService;
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
    protected RelationshipService relationshipService;

    @Autowired
    protected RelationshipTypeService relationshipTypeService;

    @Autowired
    protected EntityTypeService entityTypeService;

    @Autowired
    protected GroupService groupService;

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

        replaceAuthoritiesWithWillBeReferenced(context, itemNew, version);

        if (isParentProject(previousItem)) {
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
            List<MetadataValue> values = itemService.getMetadataByMetadataString(item, "synsicris.isLastVersion");
            itemService.removeMetadataValues(context, item, values);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void createNewProjectVersion(Context context, Item projectItem, Version version) {

        Iterator<Item> itemIterator = findItemsOfProject(context, projectItem);

        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            if (isVersionItem(item) || projectConsumerService.isParentProjectItem(item)) {
                continue;
            }

            versioningService.createNewVersion(context, item);

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

    private void setIsLastVersionMetadata(Context context, Item item) {
        try {
            itemService.setMetadataSingleValue(context, item, "synsicris", "isLastVersion", null, null, "true");
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void replaceAuthoritiesWithWillBeReferenced(Context context, Item itemNew, Version version) {
        itemNew.getMetadata().stream()
            .filter(metadataValue -> isNotBlank(metadataValue.getAuthority()))
            .filter(metadataValue -> isNotSharedReference(context, metadataValue.getAuthority()))
            .forEach(metadataValue -> replaceAuthorityWithWillBeReferenced(metadataValue, version));
    }

    private boolean isNotSharedReference(Context context, String authority) {
        Item item = find(context, authority);
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

    private boolean isParentProject(Item item) {
        return projectConsumerService.isParentProjectItem(item);
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

    private Item find(Context context, String id) {
        try {
            return itemService.find(context, UUIDUtils.fromString(id));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
