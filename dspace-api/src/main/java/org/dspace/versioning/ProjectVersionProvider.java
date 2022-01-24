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

import org.apache.commons.collections4.IteratorUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.exception.SQLRuntimeException;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultItemIterator;
import org.dspace.discovery.indexobject.IndexableCommunity;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.util.UUIDUtils;
import org.dspace.versioning.service.VersioningService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link ItemVersionProvider} that create a new version of
 * the whole project related to the given item.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class ProjectVersionProvider extends AbstractVersionProvider implements ItemVersionProvider {

    public static final String VERSION_RELATIONSHIP = "isVersionOf";

    public static final String UNIQUE_ID_REFERENCE_PREFIX = "UNIQUE-ID";

    @Autowired
    private ItemCorrectionService correctionService;

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

        updateItem(context, itemNew);

        if (isParentProject(previousItem)) {
            createNewProjectVersion(context, previousItem, version);
        }

        return itemNew;

    }

    private void createNewProjectVersion(Context context, Item projectItem, Version version) {

        Iterator<Item> itemIterator = findItemsOfProject(context, projectItem);

        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            if (isVersionItem(context, item) || projectConsumerService.isParentProjectItem(item)) {
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

    private boolean isVersionItem(Context context, Item item) {
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
        return correctionService.createWorkspaceItemAndRelationshipByItem(context, item.getID(), VERSION_RELATIONSHIP);
    }

    private void addUniqueIdMetadata(Context context, Item item, String metadataValue) {
        try {
            itemService.addMetadata(context, item, "synsicris", "uniqueid", null, null, metadataValue);
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

    private Item find(Context context, String id) {
        try {
            return itemService.find(context, UUIDUtils.fromString(id));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
