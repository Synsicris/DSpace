/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.service.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authority.service.AuthorityValueService;
import org.dspace.authority.service.ItemReferenceResolver;
import org.dspace.authority.service.ItemSearcher;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.InProgressSubmission;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.ReloadableEntity;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.DiscoverResultIterator;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.indexobject.IndexableInProgressSubmission;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.discovery.indexobject.IndexableWorkflowItem;
import org.dspace.discovery.indexobject.IndexableWorkspaceItem;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Implementation of {@link ItemSearcher} and {@link ItemReferenceResolver} to
 * search the item by the configured metadata.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class ItemSearcherByMetadata implements ItemSearcher, ItemReferenceResolver {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ChoiceAuthorityService choiceAuthorityService;

    @Autowired
    private ConfigurationService configurationService;

    private ThreadLocal<Map<String, UUID>> valuesToItemIds = ThreadLocal.withInitial(() -> new HashMap<>());

    private ThreadLocal<MultiValuedMap<String, UUID>> referenceResolutionAttempts =
        ThreadLocal.withInitial(() -> new ArrayListValuedHashMap<>());

    private final String metadata;

    private final String authorityPrefix;

    private static Logger log = LogManager.getLogger(ItemSearcherByMetadata.class);

    public ItemSearcherByMetadata(String metadata, String authorityPrefix) {
        this.metadata = metadata;
        this.authorityPrefix = authorityPrefix;
    }

    @Override
    public Item searchBy(Context context, String searchParam, Item source) {
        try {
            if (source != null) {
                referenceResolutionAttempts.get().get(searchParam).add(source.getID());
            }
            if (valuesToItemIds.get().containsKey(searchParam)) {
                Item foundInCache = itemService.find(context, valuesToItemIds.get().get(searchParam));
                if (foundInCache != null) {
                    return foundInCache;
                } else {
                    UUID removedUUID = valuesToItemIds.get().remove(searchParam);
                    log.info("No item with uuid: " + removedUUID + " was found");
                    log.info("Removing uuid: " + removedUUID + " from cache");
                    return performSearchByMetadata(context, searchParam);
                }
            } else {
                return performSearchByMetadata(context, searchParam);
            }
        } catch (SearchServiceException e) {
            throw new RuntimeException("An error occurs searching the item by metadata", e);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurs retrieving the item by identifier", e);
        }
    }

    @Override
    public void resolveReferences(Context context, Item item) {

        List<MetadataValue> metadataValues = itemService.getMetadataByMetadataString(item, metadata);
        if (CollectionUtils.isEmpty(metadataValues)) {
            return;
        }

        try {
            context.turnOffAuthorisationSystem();
            resolveReferences(context, metadataValues, item);
        } catch (SQLException | AuthorizeException e) {
            throw new RuntimeException("An error occurs resolving references", e);
        } finally {
            context.restoreAuthSystemState();
        }

    }

    @SuppressWarnings("rawtypes")
    private Item performSearchByMetadata(Context context, String searchParam) throws SearchServiceException {
        String query = metadata + ":" + searchParam;
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addDSpaceObjectFilter(IndexableWorkspaceItem.TYPE);
        discoverQuery.addDSpaceObjectFilter(IndexableWorkflowItem.TYPE);
        discoverQuery.addFilterQueries(query);

        DiscoverResult discoverResult = searchService.search(context, discoverQuery);
        List<IndexableObject> indexableObjects = discoverResult.getIndexableObjects();

        if (CollectionUtils.isEmpty(indexableObjects)) {
            return null;
        }

        return convertToItems(indexableObjects)
            .filter(item -> hasMetadataValue(item, metadata, searchParam))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("rawtypes")
    private Stream<Item> convertToItems(List<IndexableObject> indexableObjects) {
        return indexableObjects.stream()
            .map(this::convertToItem);
    }

    @SuppressWarnings("rawtypes")
    private Item convertToItem(IndexableObject indexableObject) {
        if (indexableObject instanceof IndexableItem) {
            return ((IndexableItem) indexableObject).getIndexedObject();
        } else {
            return ((IndexableInProgressSubmission) indexableObject).getIndexedObject().getItem();
        }
    }

    private boolean hasMetadataValue(Item item, String metadataField, String value) {
        return itemService.getMetadataByMetadataString(item, metadataField).stream()
            .filter(metadataValue -> metadataField.equals(metadataValue.getMetadataField().toString('.')))
            .anyMatch(metadataValue -> value.equals(metadataValue.getValue()));
    }

    private void resolveReferences(Context context, List<MetadataValue> metadataValues, Item item)
        throws SQLException, AuthorizeException {
        metadataValues.forEach(metadataValue -> {
            valuesToItemIds.get().put(metadataValue.getValue(), item.getID());
        });

        final boolean isValueToUpdate = checkWhetherTitleNeedsToBeSet();
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);

        List<String> authorities = metadataValues.stream()
            .map(MetadataValue::getValue)
            .map(value -> AuthorityValueService.REFERENCE + authorityPrefix + "::" + value)
            .collect(Collectors.toList());

        Iterator<ReloadableEntity<?>> entityIterator = findItemsToResolve(context, authorities, entityType);

        while (entityIterator.hasNext()) {
            Item itemWithReference = getNextItem(entityIterator.next());

            updateReferences(context, itemWithReference, item, authorities, isValueToUpdate);
        }

        metadataValues.forEach(metadataValue -> {
            Collection<UUID> uuids = referenceResolutionAttempts.get().get(metadataValue.getValue());
            uuids.forEach(uuid -> {
                try {
                    Item itemToRes = itemService.find(context, uuid);
                    if (itemToRes != null) {
                        updateReferences(context, itemToRes, item, authorities, isValueToUpdate);
                    }
                } catch (SQLException | AuthorizeException e) {
                    throw new RuntimeException("An error occurs while resolving references", e);
                }
            });
        });

    }

    private void updateReferences(Context context, Item itemWithReference, Item item, List<String> authorities,
        boolean isValueToUpdate) throws SQLException, AuthorizeException {

        itemWithReference.getMetadata().stream()
            .filter(metadataValue -> authorities.contains(metadataValue.getAuthority()))
            .forEach(metadataValue -> setReferences(metadataValue, item, isValueToUpdate));

        itemService.update(context, itemWithReference);
    }

    /**
     * @return whether Title metadata needs to be updated
     */
    private boolean checkWhetherTitleNeedsToBeSet() {
        return configurationService.getBooleanProperty("cris.item-reference-resolution.override-metadata-value");
    }

    private Iterator<ReloadableEntity<?>> findItemsToResolve(Context context, List<String> authorities,
        String entityType) {

        String query = choiceAuthorityService.getAuthorityControlledFieldsByEntityType(entityType).stream()
            .map(field -> getFieldFilter(field, authorities))
            .collect(Collectors.joining(" OR "));

        if (StringUtils.isEmpty(query)) {
            return Collections.emptyIterator();
        }

        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.addDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addDSpaceObjectFilter(IndexableWorkspaceItem.TYPE);
        discoverQuery.addDSpaceObjectFilter(IndexableWorkflowItem.TYPE);
        discoverQuery.addFilterQueries(query);

        return new DiscoverResultIterator<ReloadableEntity<?>, Serializable>(context, discoverQuery, false);

    }

    private void setReferences(MetadataValue metadataValue, Item item, boolean isValueToUpdate) {
        metadataValue.setAuthority(item.getID().toString());
        metadataValue.setConfidence(Choices.CF_ACCEPTED);
        String newMetadataValue = itemService.getMetadata(item, "dc.title");
        if (isValueToUpdate && StringUtils.isNotBlank(newMetadataValue)) {
            metadataValue.setValue(newMetadataValue);
        }
    }

    @SuppressWarnings("unchecked")
    private Item getNextItem(ReloadableEntity<?> nextEntity) {
        if (nextEntity instanceof Item) {
            return (Item) nextEntity;
        }
        return ((InProgressSubmission<Integer>) nextEntity).getItem();
    }

    private String getFieldFilter(String field, List<String> authorities) {
        return authorities.stream()
            .map(authority -> field.replaceAll("_", ".") + "_allauthority: \"" + authority + "\"")
            .collect(Collectors.joining(" OR "));
    }

    @Override
    public void clearCache() {
        valuesToItemIds.get().clear();
        referenceResolutionAttempts.get().clear();
    }

}
