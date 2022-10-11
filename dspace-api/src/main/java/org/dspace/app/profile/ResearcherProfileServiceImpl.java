/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.profile;

import static org.dspace.content.authority.Choices.CF_ACCEPTED;
import static org.dspace.core.Constants.READ;
import static org.dspace.eperson.Group.ANONYMOUS;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.exception.ResourceConflictException;
import org.dspace.app.profile.service.AfterResearcherProfileCreationAction;
import org.dspace.app.profile.service.ResearcherProfileService;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.IndexableObject;
import org.dspace.discovery.SearchService;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.indexobject.IndexableCollection;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.dspace.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Implementation of {@link ResearcherProfileService}.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class ResearcherProfileServiceImpl implements ResearcherProfileService {

    private static Logger log = LoggerFactory.getLogger(ResearcherProfileServiceImpl.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired(required = false)
    private List<AfterResearcherProfileCreationAction> afterCreationActions;

    @PostConstruct
    public void postConstruct() {

        if (afterCreationActions == null) {
            afterCreationActions = Collections.emptyList();
        }

    }

    @Override
    public ResearcherProfile findById(Context context, UUID id) throws SQLException, AuthorizeException {
        Assert.notNull(id, "An id must be provided to find a researcher profile");

        Item profileItem = findResearcherProfileItemById(context, id);
        if (profileItem == null) {
            return null;
        }

        return new ResearcherProfile(profileItem);
    }

    @Override
    public ResearcherProfile createAndReturn(Context context, EPerson ePerson)
        throws AuthorizeException, SQLException, SearchServiceException {

        Item profileItem = findResearcherProfileItemById(context, ePerson.getID());
        if (profileItem != null) {
            ResearcherProfile profile = new ResearcherProfile(profileItem);
            throw new ResourceConflictException("A profile is already linked to the provided User", profile);
        }

        Collection collection = findProfileCollection(context);
        if (collection == null) {
            throw new IllegalStateException("No collection found for researcher profiles");
        }

        context.turnOffAuthorisationSystem();
        Item item = createProfileItem(context, ePerson, collection);
        context.restoreAuthSystemState();

        ResearcherProfile researcherProfile = new ResearcherProfile(item);

        for (AfterResearcherProfileCreationAction afterCreationAction : afterCreationActions) {
            afterCreationAction.perform(context, researcherProfile, ePerson);
        }

        return researcherProfile;
    }

    @Override
    public void deleteById(Context context, UUID id) throws SQLException, AuthorizeException {
        Assert.notNull(id, "An id must be provided to find a researcher profile");

        Item profileItem = findResearcherProfileItemById(context, id);
        if (profileItem == null) {
            return;
        }

        if (isHardDeleteEnabled()) {
            deleteItem(context, profileItem);
        } else {
            removeCrisOwnerMetadata(context, profileItem);
        }

    }

    @Override
    public void changeVisibility(Context context, ResearcherProfile profile, ResearcherProfileVisibility visibility)
        throws AuthorizeException, SQLException {

        if (this.getVisibility(profile).equals(visibility)) {
            return;
        }

        Group internalGroup = getInternalGroup(context);
        Group anonymous = groupService.findByName(context, ANONYMOUS);
        switch (visibility.name()) {
            case "PUBLIC" :
                authorizeService.addPolicy(context, profile.getItem(), READ, anonymous);
                break;
            case "INTERNAL":
                authorizeService.removeGroupPolicies(context, profile.getItem(), anonymous);
                if (Objects.nonNull(internalGroup)) {
                    authorizeService.addPolicy(context, profile.getItem(), READ, internalGroup);
                }
                break;
            case "PRIVATE":
                authorizeService.removeGroupPolicies(context, profile.getItem(), anonymous);
                authorizeService.removeGroupPolicies(context, profile.getItem(), internalGroup);
                break;
            default:
        }
    }

    private Group getInternalGroup(Context context) throws SQLException {
        String uuid = configurationService.getProperty("system_members.group");
        if (StringUtils.isBlank(uuid)) {
            throw new RuntimeException("The property 'system_members.group' must be configured!");
        }
        Group internalGroup = groupService.find(context, UUID.fromString(uuid));
        if (Objects.isNull(internalGroup)) {
            throw new RuntimeException("The 'Internal Group' was not found!");
        }
        return internalGroup;
    }

    @Override
    public ResearcherProfile claim(final Context context, final EPerson ePerson, final URI uri)
        throws SQLException, AuthorizeException, SearchServiceException {
        Item profileItem = findResearcherProfileItemById(context, ePerson.getID());
        if (profileItem != null) {
            ResearcherProfile profile = new ResearcherProfile(profileItem);
            throw new ResourceConflictException("A profile is already linked to the provided User", profile);
        }

        Collection collection = findProfileCollection(context);
        if (collection == null) {
            throw new IllegalStateException("No collection found for researcher profiles");
        }

        final String path = uri.getPath();
        final UUID uuid = UUIDUtils.fromString(path.substring(path.lastIndexOf("/") + 1 ));
        Item item = itemService.find(context, uuid);
        if (Objects.isNull(item) || !item.isArchived() || item.isWithdrawn() || notClaimableEntityType(item)) {
            throw new IllegalArgumentException("Provided uri does not represent a valid Item to be claimed");
        }
        final String existingOwner = itemService.getMetadataFirstValue(item, "cris", "owner",
                                                                       null, null);

        if (StringUtils.isNotBlank(existingOwner)) {
            throw new IllegalArgumentException("Item with provided uri has already an owner");
        }

        context.turnOffAuthorisationSystem();
        itemService.addMetadata(context, item, "cris", "owner", null, null, ePerson.getName(),
                                ePerson.getID().toString(), CF_ACCEPTED);
        itemService.addMetadata(context, item, "cris", "sourceId", null, null, ePerson.getID().toString());

        context.restoreAuthSystemState();
        return new ResearcherProfile(item);
    }

    private boolean notClaimableEntityType(final Item item) {
        final String entityType = itemService.getEntityType(item);
        return Arrays.stream(configurationService.getArrayProperty("claimable.entityType"))
                     .noneMatch(entityType::equals);
    }

    private Item findResearcherProfileItemById(Context context, UUID id) throws SQLException, AuthorizeException {

        String profileType = getProfileType();

        Iterator<Item> items = itemService.findByAuthorityValue(context, "cris", "owner", null, id.toString());
        while (items.hasNext()) {
            Item item = items.next();
            if (hasEntityTypeMetadataEqualsTo(item, profileType)) {
                return item;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private Collection findProfileCollection(Context context) throws SQLException, SearchServiceException {
        UUID uuid = UUIDUtils.fromString(configurationService.getProperty("researcher-profile.collection.uuid"));
        if (uuid != null) {
            return collectionService.find(context, uuid);
        }

        String profileType = getProfileType();

        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.setDSpaceObjectFilter(IndexableCollection.TYPE);
        discoverQuery.addFilterQueries("dspace.entity.type:" + profileType);

        DiscoverResult discoverResult = searchService.search(context, discoverQuery);
        List<IndexableObject> indexableObjects = discoverResult.getIndexableObjects();

        if (CollectionUtils.isEmpty(indexableObjects)) {
            return null;
        }

        if (indexableObjects.size() > 1) {
            log.warn("Multiple " + profileType + " type collections were found during profile creation");
            return null;
        }

        return (Collection) indexableObjects.get(0).getIndexedObject();
    }

    private Item createProfileItem(Context context, EPerson ePerson, Collection collection)
        throws AuthorizeException, SQLException {

        String id = ePerson.getID().toString();

        WorkspaceItem workspaceItem = workspaceItemService.create(context, collection, true);
        Item item = workspaceItem.getItem();
        itemService.addMetadata(context, item, "dc", "title", null, null, ePerson.getFullName());
        itemService.addMetadata(context, item, "cris", "sourceId", null, null, id);
        itemService.addMetadata(context, item, "cris", "owner", null, null, ePerson.getFullName(), id, CF_ACCEPTED);

        item = installItemService.installItem(context, workspaceItem);

        Group anonymous = groupService.findByName(context, ANONYMOUS);
        authorizeService.removeGroupPolicies(context, item, anonymous);
        authorizeService.addPolicy(context, item, READ, ePerson);

        return item;
    }

    private boolean hasEntityTypeMetadataEqualsTo(Item item, String entityType) {
        return item.getMetadata().stream().anyMatch(metadataValue -> {
            return "dspace.entity.type".equals(metadataValue.getMetadataField().toString('.')) &&
                entityType.equals(metadataValue.getValue());
        });
    }

    private boolean isHardDeleteEnabled() {
        return configurationService.getBooleanProperty("researcher-profile.hard-delete.enabled");
    }

    private void removeCrisOwnerMetadata(Context context, Item profileItem) throws SQLException {
        List<MetadataValue> metadata = itemService.getMetadata(profileItem, "cris", "owner", null, Item.ANY);
        itemService.removeMetadataValues(context, profileItem, metadata);
    }

    private void deleteItem(Context context, Item profileItem) throws SQLException, AuthorizeException {
        try {
            context.turnOffAuthorisationSystem();
            itemService.delete(context, profileItem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private String getProfileType() {
        return configurationService.getProperty("researcher-profile.type", "Person");
    }

    @Override
    public ResearcherProfileVisibility getVisibility(ResearcherProfile profile) {
        if (isPublic(profile)) {
            return ResearcherProfileVisibility.PUBLIC;
        }
        if (isInternal(profile)) {
            return ResearcherProfileVisibility.INTERNAL;
        }
        return ResearcherProfileVisibility.PRIVATE;
    }

    private boolean isPublic(ResearcherProfile profile) {
        return profile.getItem().getResourcePolicies().stream()
                                                      .filter(policy -> Objects.nonNull(policy.getGroup()))
                                                      .anyMatch(policy -> READ == policy.getAction() &&
                                                                ANONYMOUS.equals(policy.getGroup().getName()));
    }

    private boolean isInternal(ResearcherProfile profile) {
        String uuid = configurationService.getProperty("project.creation.group");
        if (StringUtils.isBlank(uuid)) {
            return false;
        }
        return profile.getItem().getResourcePolicies().stream()
                                                      .filter(policy -> Objects.nonNull(policy.getGroup()))
                                                      .anyMatch(policy -> READ == policy.getAction() &&
                                                                uuid.equals(policy.getGroup().getID().toString()));
    }
}
