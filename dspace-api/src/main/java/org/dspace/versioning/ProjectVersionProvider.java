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
import org.apache.commons.lang3.StringUtils;
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

    public static final String VERSION_REFERENCE_PREFIX = "VERSION";

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
        return createItemCopy(context, item);
    }

    @Override
    public Item updateItemState(Context context, Item itemNew, Item previousItem, Version version) {

        if (isCascadeVersioningNotEnabled()) {
            return itemNew;
        }

        addIdAndVersionMetadata(context, itemNew, version);
        replaceAuthoritiesWithWillBeReferenced(context, itemNew, version);

        if (isNotParentProject(previousItem)) {
            return itemNew;
        }

        Iterator<Item> itemIterator = findItemsOfProject(context, previousItem);
        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            if (isVersionItem(context, item) || projectConsumerService.isParentProjectItem(item)) {
                continue;
            }

            Version cascadeVersion = versioningService.createNewVersion(context, item);

            if (cascadeVersion.getVersionNumber() != version.getVersionNumber()) {
                throw new IllegalStateException(
                    "The version number created for the item " + item.getID() + " (" + cascadeVersion.getVersionNumber()
                        + ") is different to the parent project version number (" + version.getVersionNumber() + ")");
            }

        }

        return itemNew;

    }

    private boolean isVersionItem(Context context, Item item) {
        return isNotEmpty(itemService.getMetadataFirstValue(item, "synsicris", "uniqueid", null, Item.ANY));
    }

    @Override
    public void deleteVersionedItem(Context c, Version versionToDelete, VersionHistory history) throws SQLException {

    }

    private Iterator<Item> findItemsOfProject(Context context, Item projectItem) {
        try {

            Community projectCommunity = projectConsumerService.getParentCommunityByProjectItem(context, projectItem);
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

    private Item createItemCopy(Context context, Item item) {
        try {
            WorkspaceItem workspaceItem = createWorkspaceItemCopy(context, item);
            return installItemService.installItem(context, workspaceItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WorkspaceItem createWorkspaceItemCopy(Context context, Item item) throws Exception {
        return correctionService.createWorkspaceItemAndRelationshipByItem(context, item.getID(), VERSION_RELATIONSHIP);
    }

    private void addIdAndVersionMetadata(Context context, Item item, Version version) {
        try {
            String metadataValue = composeUniqueId(item.getID().toString(), version);
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
        if (item == null) {
            return false;
        }

        return isNotSharedItem(context, item);
    }

    private boolean isNotSharedItem(Context context, Item item) {

        String sharedCommunityName = configurationService.getProperty("shared.community-name");

        if (StringUtils.isEmpty(sharedCommunityName)) {
            return true;
        }

        try {
            return itemService.getCommunities(context, item).stream()
                .noneMatch(community -> sharedCommunityName.equals(community.getName()));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void replaceAuthorityWithWillBeReferenced(MetadataValue metadataValue, Version version) {
        String uniqueId = composeUniqueId(metadataValue.getAuthority(), version);
        metadataValue.setAuthority(REFERENCE + VERSION_REFERENCE_PREFIX + "::" + uniqueId);
    }

    private String composeUniqueId(String itemId, Version version) {
        return itemId + "_" + version.getVersionNumber();
    }

    private boolean isNotParentProject(Item item) {
        return !projectConsumerService.isParentProjectItem(item);
    }

    private Item find(Context context, String id) {
        try {
            return itemService.find(context, UUIDUtils.fromString(id));
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
