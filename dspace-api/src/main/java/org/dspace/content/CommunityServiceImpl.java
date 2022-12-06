/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import static org.dspace.project.util.ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.FUNDING_MEMBERS_GROUP_TEMPLATE;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.metrics.service.CrisMetricsService;
import org.dspace.app.util.AuthorizeUtil;
import org.dspace.authorize.AuthorizeConfiguration;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.authority.Choices;
import org.dspace.content.dao.CommunityDAO;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.SiteService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.core.LogHelper;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.GroupService;
import org.dspace.eperson.service.SubscribeService;
import org.dspace.event.Event;
import org.dspace.identifier.IdentifierException;
import org.dspace.identifier.service.IdentifierService;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Service implementation for the Community object.
 * This class is responsible for all business logic calls for the Community object and is autowired by spring.
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class CommunityServiceImpl extends DSpaceObjectServiceImpl<Community> implements CommunityService {

    /**
     * log4j category
     */
    private static final Logger log = LogManager.getLogger(CommunityServiceImpl.class);

    @Autowired
    protected CommunityDAO communityDAO;

    @Autowired
    protected CollectionService collectionService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected AuthorizeService authorizeService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected BitstreamService bitstreamService;

    @Autowired
    protected SiteService siteService;
    @Autowired(required = true)
    protected IdentifierService identifierService;
    @Autowired(required = true)
    protected SubscribeService subscribeService;
    @Autowired(required = true)
    protected CrisMetricsService crisMetricsService;

    @Autowired
    protected ResourcePolicyService resourcePolicyService;

    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    protected WorkspaceItemService workspaceItemService;

    @Autowired
    protected InstallItemService installItemService;

    private String matadataToSkipIfAlreadyPresent[] = new String[] {
        "dc.date.accessioned", "dc.date.available", "dc.identifier.uri", "dspace.entity.type" };

    private String matadataToSkip[] = new String[] { "synsicris.struct-builder.identifier" };

    protected CommunityServiceImpl() {
        super();

    }

    @Override
    public Community create(Community parent, Context context) throws SQLException, AuthorizeException {
        return create(parent, context, null);
    }

    @Override
    public Community create(Community parent, Context context, String handle) throws SQLException, AuthorizeException {
        return create(parent, context, handle, null);
    }

    @Override
    public Community create(Community parent, Context context, String handle,
                            UUID uuid) throws SQLException, AuthorizeException {
        if (!(authorizeService.isAdmin(context) ||
                parent != null && authorizeService.authorizeActionBoolean(context, parent, Constants.ADD))) {
            throw new AuthorizeException(
                    "Only administrators can create communities");
        }

        Community newCommunity;
        if (uuid != null) {
            newCommunity = communityDAO.create(context, new Community(uuid));
        } else {
            newCommunity = communityDAO.create(context, new Community());
        }

        if (parent != null) {
            parent.addSubCommunity(newCommunity);
            newCommunity.addParentCommunity(parent);
        }


        // create the default authorization policy for communities
        // of 'anonymous' READ
        Group anonymousGroup = groupService.findByName(context, Group.ANONYMOUS);

        authorizeService.createResourcePolicy(context, newCommunity, anonymousGroup, null, Constants.READ, null);

        communityDAO.save(context, newCommunity);

        try {
            if (handle == null) {
                identifierService.register(context, newCommunity);
            } else {
                identifierService.register(context, newCommunity, handle);
            }
        }  catch (IllegalStateException | IdentifierException ex) {
            throw new IllegalStateException(ex);
        }

        context.addEvent(new Event(Event.CREATE, Constants.COMMUNITY, newCommunity.getID(), newCommunity.getHandle(),
                getIdentifiers(context, newCommunity)));

        // if creating a top-level Community, simulate an ADD event at the Site.
        if (parent == null) {
            context.addEvent(new Event(Event.ADD, Constants.SITE, siteService.findSite(context).getID(),
                    Constants.COMMUNITY, newCommunity.getID(), newCommunity.getHandle(),
                    getIdentifiers(context, newCommunity)));
        }

        log.info(LogHelper.getHeader(context, "create_community",
                "community_id=" + newCommunity.getID())
                + ",handle=" + newCommunity.getHandle());

        return newCommunity;
    }

    @Override
    public Community find(Context context, UUID id) throws SQLException {
        return communityDAO.findByID(context, Community.class, id);
    }

    @Override
    public List<Community> findAll(Context context) throws SQLException {
        MetadataField sortField = metadataFieldService.findByElement(context, MetadataSchemaEnum.DC.getName(),
                                                                     "title", null);
        if (sortField == null) {
            throw new IllegalArgumentException(
                "Required metadata field '" + MetadataSchemaEnum.DC.getName() + ".title' doesn't exist!");
        }

        return communityDAO.findAll(context, sortField);
    }

    @Override
    public List<Community> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        MetadataField nameField = metadataFieldService.findByElement(context, MetadataSchemaEnum.DC.getName(),
                                                                     "title", null);
        if (nameField == null) {
            throw new IllegalArgumentException(
                "Required metadata field '" + MetadataSchemaEnum.DC.getName() + ".title' doesn't exist!");
        }

        return communityDAO.findAll(context, nameField, limit, offset);
    }

    @Override
    public List<Community> findAllTop(Context context) throws SQLException {
        // get all communities that are not children
        MetadataField sortField = metadataFieldService.findByElement(context, MetadataSchemaEnum.DC.getName(),
                                                                     "title", null);
        if (sortField == null) {
            throw new IllegalArgumentException(
                "Required metadata field '" + MetadataSchemaEnum.DC.getName() + ".title' doesn't exist!");
        }

        return communityDAO.findAllNoParent(context, sortField);
    }

    @Override
    public void setMetadataSingleValue(Context context, Community community,
            MetadataFieldName field, String language, String value)
            throws MissingResourceException, SQLException {
        if (field.equals(MD_NAME) && (value == null || value.trim().equals(""))) {
            try {
                value = I18nUtil.getMessage("org.dspace.content.untitled");
            } catch (MissingResourceException e) {
                value = "Untitled";
            }
        }

        /*
         * Set metadata field to null if null
         * and trim strings to eliminate excess
         * whitespace.
         */
        if (value == null) {
            clearMetadata(context, community, field.schema, field.element, field.qualifier, Item.ANY);
            community.setMetadataModified();
        } else {
            super.setMetadataSingleValue(context, community, field, null, value);
        }

        community.addDetails(field.toString());
    }

    @Override
    public Bitstream setLogo(Context context, Community community, InputStream is)
            throws AuthorizeException, IOException, SQLException {
        // Check authorisation
        // authorized to remove the logo when DELETE rights
        // authorized when canEdit
        if (!(is == null && authorizeService.authorizeActionBoolean(
                context, community, Constants.DELETE))) {
            canEdit(context, community);
        }

        // First, delete any existing logo
        Bitstream oldLogo = community.getLogo();
        if (oldLogo != null) {
            log.info(LogHelper.getHeader(context, "remove_logo", "community_id=" + community.getID()));
            community.setLogo(null);
            bitstreamService.delete(context, oldLogo);
        }

        if (is != null) {
            Bitstream newLogo = bitstreamService.create(context, is);
            community.setLogo(newLogo);

            // now create policy for logo bitstream
            // to match our READ policy
            List<ResourcePolicy> policies = authorizeService
                    .getPoliciesActionFilter(context, community, Constants.READ);
            authorizeService.addPolicies(context, policies, newLogo);

            log.info(LogHelper.getHeader(context, "set_logo",
                "community_id=" + community.getID() + "logo_bitstream_id=" + newLogo.getID()));
        }

        return community.getLogo();
    }

    @Override
    public void update(Context context, Community community) throws SQLException, AuthorizeException {
        // Check authorisation
        canEdit(context, community);

        log.info(LogHelper.getHeader(context, "update_community",
                                      "community_id=" + community.getID()));

        super.update(context, community);

        communityDAO.save(context, community);
        if (community.isModified()) {
            context.addEvent(new Event(Event.MODIFY, Constants.COMMUNITY, community.getID(), null,
                                       getIdentifiers(context, community)));
            community.clearModified();
        }
        if (community.isMetadataModified()) {
            context.addEvent(
                new Event(Event.MODIFY_METADATA, Constants.COMMUNITY, community.getID(), community.getDetails(),
                          getIdentifiers(context, community)));
            community.clearModified();
        }
        community.clearDetails();
    }

    @Override
    public Group createAdministrators(Context context, Community community) throws SQLException, AuthorizeException {
        // Check authorisation - Must be an Admin to create more Admins
        AuthorizeUtil.authorizeManageAdminGroup(context, community);

        Group admins = community.getAdministrators();
        if (admins == null) {
            //turn off authorization so that Community Admins can create Sub-Community Admins
            context.turnOffAuthorisationSystem();
            admins = groupService.create(context);
            context.restoreAuthSystemState();

            groupService.setName(admins, "COMMUNITY_" + community.getID() + "_ADMIN");
            groupService.update(context, admins);
        }

        authorizeService.addPolicy(context, community, Constants.ADMIN, admins);

        // register this as the admin group
        community.setAdmins(admins);
        context.addEvent(new Event(Event.MODIFY, Constants.COMMUNITY, community.getID(),
                                             null, getIdentifiers(context, community)));
        return admins;
    }

    @Override
    public void removeAdministrators(Context context, Community community) throws SQLException, AuthorizeException {
        // Check authorisation - Must be an Admin of the parent community (or system admin) to delete Admin group
        AuthorizeUtil.authorizeRemoveAdminGroup(context, community);

        // just return if there is no administrative group.
        if (community.getAdministrators() == null) {
            return;
        }

        // Remove the link to the community table.
        community.setAdmins(null);
        context.addEvent(new Event(Event.MODIFY, Constants.COMMUNITY, community.getID(),
                                             null, getIdentifiers(context, community)));
    }

    @Override
    public List<Community> getAllParents(Context context, Community community) throws SQLException {
        List<Community> parentList = new ArrayList<>();
        Community parent = (Community) getParentObject(context, community);
        while (parent != null) {
            parentList.add(parent);
            parent = (Community) getParentObject(context, parent);
        }
        return parentList;
    }

    @Override
    public List<Community> getAllParents(Context context, Collection collection) throws SQLException {
        List<Community> result = new ArrayList<>();
        List<Community> communities = collection.getCommunities();
        result.addAll(communities);
        for (Community community : communities) {
            result.addAll(getAllParents(context, community));
        }
        return result;
    }

    @Override
    public List<Collection> getAllCollections(Context context, Community community) throws SQLException {
        List<Collection> collectionList = new ArrayList<>();
        List<Community> subCommunities = community.getSubcommunities();
        for (Community subCommunity : subCommunities) {
            addCollectionList(subCommunity, collectionList);
        }

        List<Collection> collections = community.getCollections();
        for (Collection collection : collections) {
            collectionList.add(collection);
        }
        return collectionList;
    }


    /**
     * Internal method to process subcommunities recursively
     *
     * @param community      community
     * @param collectionList list of collections
     * @throws SQLException if database error
     */
    protected void addCollectionList(Community community, List<Collection> collectionList) throws SQLException {
        for (Community subcommunity : community.getSubcommunities()) {
            addCollectionList(subcommunity, collectionList);
        }

        for (Collection collection : community.getCollections()) {
            collectionList.add(collection);
        }
    }

    @Override
    public void addCollection(Context context, Community community, Collection collection)
        throws SQLException, AuthorizeException {
        // Check authorisation
        authorizeService.authorizeAction(context, community, Constants.ADD);

        log.info(LogHelper.getHeader(context, "add_collection",
                                      "community_id=" + community.getID() + ",collection_id=" + collection.getID()));

        if (!community.getCollections().contains(collection)) {
            community.addCollection(collection);
            collection.addCommunity(community);
        }
        context.addEvent(
            new Event(Event.ADD, Constants.COMMUNITY, community.getID(), Constants.COLLECTION, collection.getID(),
                      community.getHandle(), getIdentifiers(context, community)));
    }

    @Override
    public Community createSubcommunity(Context context, Community parentCommunity)
            throws SQLException, AuthorizeException {
        return createSubcommunity(context, parentCommunity, null);
    }


    @Override
    public Community createSubcommunity(Context context, Community parentCommunity, String handle)
            throws SQLException, AuthorizeException {
        return createSubcommunity(context, parentCommunity, handle, null);
    }

    @Override
    public Community createSubcommunity(Context context, Community parentCommunity, String handle,
                                        UUID uuid) throws SQLException, AuthorizeException {
        // Check authorisation
        authorizeService.authorizeAction(context, parentCommunity, Constants.ADD);

        Community c;
        c = create(parentCommunity, context, handle, uuid);

        addSubcommunity(context, parentCommunity, c);

        return c;
    }

    @Override
    public void addSubcommunity(Context context, Community parentCommunity, Community childCommunity)
        throws SQLException, AuthorizeException {
        // Check authorisation
        authorizeService.authorizeAction(context, parentCommunity, Constants.ADD);

        log.info(LogHelper.getHeader(context, "add_subcommunity",
                                      "parent_comm_id=" + parentCommunity.getID() + ",child_comm_id=" + childCommunity
                                          .getID()));

        if (!parentCommunity.getSubcommunities().contains(childCommunity)) {
            parentCommunity.addSubCommunity(childCommunity);
            childCommunity.addParentCommunity(parentCommunity);
        }
        context.addEvent(new Event(Event.ADD, Constants.COMMUNITY, parentCommunity.getID(), Constants.COMMUNITY,
                                   childCommunity.getID(), parentCommunity.getHandle(),
                                   getIdentifiers(context, parentCommunity)));
    }

    @Override
    public void removeCollection(Context context, Community community, Collection collection)
        throws SQLException, AuthorizeException, IOException {
        // Check authorisation
        authorizeService.authorizeAction(context, community, Constants.REMOVE);

        ArrayList<String> removedIdentifiers = collectionService.getIdentifiers(context, collection);
        String removedHandle = collection.getHandle();
        UUID removedId = collection.getID();

        if (collection.getCommunities().size() == 1) {
            collectionService.delete(context, collection);
        } else {
            community.removeCollection(collection);
            collection.removeCommunity(community);
        }

        log.info(LogHelper.getHeader(context, "remove_collection",
                                      "community_id=" + community.getID() + ",collection_id=" + collection.getID()));

        // Remove any mappings
        context.addEvent(new Event(Event.REMOVE, Constants.COMMUNITY, community.getID(),
                                   Constants.COLLECTION, removedId, removedHandle, removedIdentifiers));
    }

    @Override
    public void removeSubcommunity(Context context, Community parentCommunity, Community childCommunity)
        throws SQLException, AuthorizeException, IOException {
        // Check authorisation
        authorizeService.authorizeAction(context, parentCommunity, Constants.REMOVE);

        ArrayList<String> removedIdentifiers = getIdentifiers(context, childCommunity);
        String removedHandle = childCommunity.getHandle();
        UUID removedId = childCommunity.getID();

        rawDelete(context, childCommunity);

        log.info(LogHelper.getHeader(context, "remove_subcommunity",
                                      "parent_comm_id=" + parentCommunity.getID() + ",child_comm_id=" + childCommunity
                                          .getID()));

        context.addEvent(
            new Event(Event.REMOVE, Constants.COMMUNITY, parentCommunity.getID(), Constants.COMMUNITY, removedId,
                      removedHandle, removedIdentifiers));
    }

    @Override
    public void delete(Context context, Community community) throws SQLException, AuthorizeException, IOException {
        crisMetricsService.deleteByResourceID(context, community);
        // Check authorisation
        // FIXME: If this was a subcommunity, it is first removed from it's
        // parent.
        // This means the parentCommunity == null
        // But since this is also the case for top-level communities, we would
        // give everyone rights to remove the top-level communities.
        // The same problem occurs in removing the logo
        if (!authorizeService.authorizeActionBoolean(context, getParentObject(context, community), Constants.REMOVE)) {
            authorizeService.authorizeAction(context, community, Constants.DELETE);
        }
        ArrayList<String> removedIdentifiers = getIdentifiers(context, community);
        String removedHandle = community.getHandle();
        UUID removedId = community.getID();

        subscribeService.deleteByDspaceObject(context, community);

        // If not a top-level community, have parent remove me; this
        // will call rawDelete() before removing the linkage
        Community parent = (Community) getParentObject(context, community);

        if (parent != null) {
            // remove the subcommunities first
            Iterator<Community> subcommunities = community.getSubcommunities().iterator();
            while (subcommunities.hasNext()) {
                Community subCommunity = subcommunities.next();
                community.removeSubCommunity(subCommunity);
                delete(context, subCommunity);
            }
            // now let the parent remove the community
            removeSubcommunity(context, parent, community);

            return;
        }

        rawDelete(context, community);
        context.addEvent(
            new Event(Event.REMOVE, Constants.SITE, siteService.findSite(context).getID(), Constants.COMMUNITY,
                      removedId, removedHandle, removedIdentifiers));

    }

    @Override
    public int getSupportsTypeConstant() {
        return Constants.COMMUNITY;
    }

    /**
     * Internal method to remove the community and all its children from the
     * database, and perform any pre/post-cleanup
     *
     * @param context   context
     * @param community community
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     * @throws IOException        if IO error
     */
    protected void rawDelete(Context context, Community community)
        throws SQLException, AuthorizeException, IOException {
        log.info(LogHelper.getHeader(context, "delete_community",
                                      "community_id=" + community.getID()));

        context.addEvent(new Event(Event.DELETE, Constants.COMMUNITY, community.getID(), community.getHandle(),
                                   getIdentifiers(context, community)));

        // Remove collections
        Iterator<Collection> collections = community.getCollections().iterator();

        while (collections.hasNext()) {
            Collection collection = collections.next();
            community.removeCollection(collection);
            removeCollection(context, community, collection);
        }
        // delete subcommunities
        Iterator<Community> subCommunities = community.getSubcommunities().iterator();

        while (subCommunities.hasNext()) {
            Community subComm = subCommunities.next();
            community.removeSubCommunity(subComm);
            delete(context, subComm);
        }

        // Remove the logo
        setLogo(context, community, null);

        // Remove any Handle
        handleService.unbindHandle(context, community);

        // Remove the parent-child relationship for the community we want to delete
        Community parent = (Community) getParentObject(context, community);
        if (parent != null) {
            community.removeParentCommunity(parent);
            parent.removeSubCommunity(community);
        }

        Group g = community.getAdministrators();

        // Delete community row
        communityDAO.delete(context, community);

        // Remove administrators group - must happen after deleting community

        if (g != null) {
            groupService.delete(context, g);
        }
    }

    @Override
    public boolean canEditBoolean(Context context, Community community) throws SQLException {
        try {
            canEdit(context, community);

            return true;
        } catch (AuthorizeException e) {
            return false;
        }
    }

    @Override
    public void canEdit(Context context, Community community) throws AuthorizeException, SQLException {
        List<Community> parents = getAllParents(context, community);

        for (Community parent : parents) {
            if (authorizeService.authorizeActionBoolean(context, parent,
                                                        Constants.WRITE)) {
                return;
            }

            if (authorizeService.authorizeActionBoolean(context, parent,
                                                        Constants.ADD)) {
                return;
            }
        }

        authorizeService.authorizeAction(context, community, Constants.WRITE);
    }

    @Override
    public Community findByAdminGroup(Context context, Group group) throws SQLException {
        return communityDAO.findByAdminGroup(context, group);
    }

    @Override
    public List<Community> findAuthorized(Context context, List<Integer> actions) throws SQLException {
        return communityDAO.findAuthorized(context, context.getCurrentUser(), actions);
    }

    @Override
    public List<Community> findAuthorizedGroupMapped(Context context, List<Integer> actions) throws SQLException {
        return communityDAO.findAuthorizedByGroup(context, context.getCurrentUser(), actions);
    }

    @Override
    public DSpaceObject getAdminObject(Context context, Community community, int action) throws SQLException {
        DSpaceObject adminObject = null;
        switch (action) {
            case Constants.REMOVE:
                if (AuthorizeConfiguration.canCommunityAdminPerformSubelementDeletion()) {
                    adminObject = community;
                }
                break;

            case Constants.DELETE:
                if (AuthorizeConfiguration.canCommunityAdminPerformSubelementDeletion()) {
                    adminObject = getParentObject(context, community);
                    if (adminObject == null) {
                        //top-level community, has to be admin of the current community
                        adminObject = community;
                    }
                }
                break;
            case Constants.ADD:
                if (AuthorizeConfiguration.canCommunityAdminPerformSubelementCreation()) {
                    adminObject = community;
                }
                break;
            default:
                adminObject = community;
                break;
        }
        return adminObject;
    }


    @Override
    public DSpaceObject getParentObject(Context context, Community community) throws SQLException {
        List<Community> parentCommunities = community.getParentCommunities();
        if (CollectionUtils.isNotEmpty(parentCommunities)) {
            return parentCommunities.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public void updateLastModified(Context context, Community community) {
        //Also fire a modified event since the community HAS been modified
        context.addEvent(new Event(Event.MODIFY, Constants.COMMUNITY,
                                   community.getID(), null, getIdentifiers(context, community)));

    }

    @Override
    public Community findByIdOrLegacyId(Context context, String id) throws SQLException {
        if (StringUtils.isNumeric(id)) {
            return findByLegacyId(context, Integer.parseInt(id));
        } else {
            return find(context, UUID.fromString(id));
        }
    }

    @Override
    public Community findByLegacyId(Context context, int id) throws SQLException {
        return communityDAO.findByLegacyId(context, id, Community.class);
    }

    @Override
    public int countTotal(Context context) throws SQLException {
        return communityDAO.countRows(context);
    }

    @Override
    public Community cloneCommunity(Context context, Community template, Community parent, String name, String grants)
        throws SQLException, AuthorizeException {
        Assert.notNull(name, "The name of the new community must be provided");

        List<Item> newItems = new ArrayList<Item>();
        Map<UUID, CloneDTO> oldItem2clonedItem = new HashMap<UUID, CloneDTO>();
        Community newCommunity = create(parent, context);
        setCommunityName(context, newCommunity, name);
        UUID rootCommunityUUID = newCommunity.getID();
        String projectRootCommId = configurationService.getProperty("project.parent-community-id", "");
        boolean isProject = Objects.nonNull(parent) && parent.getID().toString().equals(projectRootCommId);
        boolean isFunding = !isProject;
        Map<UUID, Group> scopedRoles = createScopedRoles(context, newCommunity, parent, projectRootCommId, isProject);

        List<MetadataValue> relationMd =
            this.getMetadata(
                template,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.schema,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.element,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier,
                null
        );
        UUID uuidProjectItem = relationMd.size() > 0 ? UUIDUtils.fromString(relationMd.get(0).getAuthority()) : null;
        newCommunity = cloneCommunity(context, parent, template, newCommunity, scopedRoles, uuidProjectItem, rootCommunityUUID,
                                      name, grants, newItems, oldItem2clonedItem, isFunding);

        updateClonedItems(context, newItems, oldItem2clonedItem);

        return newCommunity;
    }

    private Community cloneCommunity(Context context, Community parentCommunity, Community communityToClone,
            Community clone, Map<UUID, Group> scopedRoles, UUID uuidProjectItem, UUID rootCommunityUUID,
            String newName, String grants, List<Item> newItems, Map<UUID, CloneDTO> oldItem2clonedItem,
            boolean isFunding) throws SQLException, AuthorizeException {

        List<Community> subCommunities = communityToClone.getSubcommunities();
        List<Collection> subCollections = communityToClone.getCollections();
        cloneMetadata(context, this, clone, communityToClone);
        cloneCommunityGroups(context, clone, communityToClone, scopedRoles);

        for (Community c : subCommunities) {
            Community newSubCommunity = create(clone, context);
            cloneCommunity(context, parentCommunity, c, newSubCommunity, scopedRoles, uuidProjectItem, rootCommunityUUID,
                           newName, grants, newItems, oldItem2clonedItem, isFunding);
        }

        for (Collection collection : subCollections) {
            Collection newCollection = collectionService.create(context, clone);
            cloneMetadata(context, collectionService, newCollection, collection);
            cloneTemplateItem(context, newCollection, collection);
            cloneCollectionGroups(context, newCollection, collection, scopedRoles);
            cloneCollectionItems(context, parentCommunity, newCollection, collection, uuidProjectItem,
                    rootCommunityUUID, newName, grants,newItems, oldItem2clonedItem, isFunding);
        }

        return clone;
    }

    private void updateClonedItems(Context context, List<Item> newItems, Map<UUID, CloneDTO> oldItem2clonedItem) {
        for (Item item : newItems) {
            for (MetadataValue metadataValue : item.getMetadata()) {
                if (StringUtils.isNotBlank(metadataValue.getAuthority())) {
                    updateClone(metadataValue, oldItem2clonedItem.get(UUID.fromString(metadataValue.getAuthority())));
                }

            }
        }
    }

    private void updateClone(MetadataValue metadataValue, CloneDTO cloneDTO) {
        if (Objects.nonNull(cloneDTO)) {
            metadataValue.setAuthority(cloneDTO.getItemUuid().toString());
            metadataValue.setValue(cloneDTO.getTitle());
        }
    }

    private void cloneCollectionItems(Context context, Community parentCommunity, Collection newCollection,
            Collection collection, UUID uuidProjectItem, UUID rootCommunityUUID, String newName, String grants,
            List<Item> newItems, Map<UUID, CloneDTO> oldItem2clonedItem, boolean isFunding) throws SQLException {
        Iterator<Item> items = itemService.findAllByCollection(context, collection);
        try {
            while (items.hasNext()) {
                Item item = items.next();
                WorkspaceItem workspaceItem = workspaceItemService.create(context, newCollection, true);
                Item newItem = installItemService.installItem(context, workspaceItem);
                collectionService.addItem(context, newCollection, newItem);
                cloneMetadata(context, itemService, newItem, item);
                if (Objects.nonNull(uuidProjectItem)) {
                    if (item.getID().equals(uuidProjectItem)) {
                        Community rootCommunity = this.find(context, rootCommunityUUID);
                        replacePlaceholderValue(context, rootCommunity, newItem, newName, grants);
                        if (isFunding) {
                            setCrisPolicy(context, parentCommunity, rootCommunity, newItem, grants);
                        }
                    }
                }
                newItems.add(newItem);
                oldItem2clonedItem.put(item.getID(), new CloneDTO(newItem.getID(), newItem.getName()));
            }
        } catch (AuthorizeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setCrisPolicy(Context context, Community parentCommunity, Community newRootCommunity, Item newItem,
            String grants) throws SQLException {

        if (StringUtils.isBlank(grants)) {
            grants = ProjectConstants.PROJECT;
        }
        
        itemService.replaceMetadata(context, newItem, "cris", "project", "shared", null, grants, null,
                Choices.CF_UNSET, 0);
        
        String groupName = null;
        Group group;
        switch (grants) {
            case ProjectConstants.PROJECT:
                groupName = String.format(PROJECT_MEMBERS_GROUP_TEMPLATE,
                        parentCommunity.getParentCommunities().get(0).getID().toString());
                break;
            case ProjectConstants.FUNDING:
                groupName = String.format(FUNDING_MEMBERS_GROUP_TEMPLATE, newRootCommunity.getID().toString());
                break;
        }
        group = groupService.findByName(context, groupName);
        if (Objects.nonNull(group)) {
            itemService.replaceMetadata(context, newItem, ProjectConstants.MD_POLICY_GROUP.schema,
                    ProjectConstants.MD_POLICY_GROUP.element, ProjectConstants.MD_POLICY_GROUP.qualifier,
                    null, group.getName(), group.getID().toString(), Choices.CF_ACCEPTED, 0);
        }
        
    }

    private void replacePlaceholderValue(Context context, Community newRootCommunity, Item newItem, String newName,
            String grants) throws SQLException {
        this.replaceMetadata(context, newRootCommunity, ProjectConstants.MD_RELATION_ITEM_ENTITY.schema,
                ProjectConstants.MD_RELATION_ITEM_ENTITY.element, ProjectConstants.MD_RELATION_ITEM_ENTITY.qualifier,
                null, newName, newItem.getID().toString(), Choices.CF_ACCEPTED, 0);
        context.reloadEntity(newItem);
        itemService.replaceMetadata(context, newItem, "dc", "title", null, null, newName, null, Choices.CF_UNSET, 0);
    }

    private void cloneTemplateItem(Context context, Collection col, Collection collection)
        throws SQLException, AuthorizeException {
        Item item = collection.getTemplateItem();
        if (item != null) {
            collectionService.createTemplateItem(context, col);
            Item clonecommunityToClone = col.getTemplateItem();
            cloneMetadata(context, itemService, clonecommunityToClone, item);
        }
    }

    private <T extends DSpaceObject> void cloneMetadata(Context context, DSpaceObjectService<T> service,
        T target, T dsoToClone) throws SQLException {

        List<MetadataValue> metadataValue = dsoToClone.getMetadata();
        for (MetadataValue metadata : metadataValue) {
            if (isMetadataToSkip(service, target, metadata)) {
                continue;
            }
            if (StringUtils.isNotBlank(metadata.getAuthority())) {
                service.addMetadata(context, target, metadata.getSchema(), metadata.getElement(),
                        metadata.getQualifier(), metadata.getLanguage(), metadata.getValue(), metadata.getAuthority(),
                        Choices.CF_ACCEPTED);
            } else {
                service.addMetadata(context, target, metadata.getSchema(), metadata.getElement(),
                        metadata.getQualifier(), metadata.getLanguage(), metadata.getValue());
            }
        }
    }

    private <T extends DSpaceObject> boolean isMetadataToSkip(DSpaceObjectService<T> service, T target,
            MetadataValue metadata) {
        String metadataName = metadata.getSchema() + "." + metadata.getElement();
        if (StringUtils.isNotBlank(metadata.getQualifier())) {
            metadataName += "." + metadata.getQualifier();
        }

        if (Arrays.stream(matadataToSkipIfAlreadyPresent).anyMatch(metadataName::equals)) {
            List<MetadataValue> metadataList = service.getMetadataByMetadataString(target, metadataName);

            return metadataList.size() > 0;
        }

        return Arrays.stream(matadataToSkip).anyMatch(metadataName::equals);
    }

    private UUID extractItemUuid(String value) {
        UUID itemUuid = null;
        if (StringUtils.isNotBlank(value)) {
            Pattern pattern = Pattern.compile("^((?:project_|funding_))(.*)(_.*)$");
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                itemUuid = UUID.fromString(matcher.group(2));
            } else {
                throw new RuntimeException("Metadata value of synsicris.relation.entity_item : " + value
                        + " is bad formed!  It should have the following format : project_<UUID>_<.*>");
            }
        }
        return itemUuid;
    }

    private Community setCommunityName(Context context, Community community, String name)
        throws SQLException, AuthorizeException {
        List<MetadataValue> metadata = getMetadata(community, "dc", "title", null, Item.ANY);
        if (CollectionUtils.isEmpty(metadata)) {
            addMetadata(context, community, "dc", "title", null, null, name);
        } else {
            MetadataValue dcTitle = metadata.get(0);
            dcTitle.setValue(name);
            update(context, community);
        }
        return community;
    }

    private void cloneCommunityGroups(Context context, Community clone, Community communityToClone,
        Map<UUID, Group> scopedRoles) throws SQLException, AuthorizeException {

        Group administrators = communityToClone.getAdministrators();
        if (administrators != null) {
            Group newAdministrators = createAdministrators(context, clone);
            addInstitutionalScopedRoleMembers(context, administrators, newAdministrators, scopedRoles);
        }

        clonePolicies(context, clone, communityToClone, scopedRoles);

    }

    private void cloneCollectionGroups(Context context, Collection newCollection, Collection collection,
        Map<UUID, Group> scopedRoles) throws SQLException, AuthorizeException {

        Group administrators = collection.getAdministrators();
        if (administrators != null) {
            Group newAdministrators = collectionService.createAdministrators(context, newCollection);
            addInstitutionalScopedRoleMembers(context, administrators, newAdministrators, scopedRoles);
        }

        Group submitter = collection.getSubmitters();
        if (submitter != null) {
            Group newSubmitter = collectionService.createSubmitters(context, newCollection);
            addInstitutionalScopedRoleMembers(context, submitter, newSubmitter, scopedRoles);
        }

        cloneWorkflowGroup(context, collection.getWorkflowStep1(context), newCollection, 1, scopedRoles);
        cloneWorkflowGroup(context, collection.getWorkflowStep2(context), newCollection, 2, scopedRoles);
        cloneWorkflowGroup(context, collection.getWorkflowStep3(context), newCollection, 3, scopedRoles);

        clonePolicies(context, newCollection, collection, scopedRoles);

    }

    private void addInstitutionalScopedRoleMembers(Context context, Group group, Group newGroup,
        Map<UUID, Group> scopedRoles) throws SQLException, AuthorizeException {

        for (Group subGroup : group.getMemberGroups()) {
            if (scopedRoles.containsKey(subGroup.getID())) {
                groupService.addMember(context, newGroup, scopedRoles.get(subGroup.getID()));
            }
        }

    }

    private void clonePolicies(Context context, DSpaceObject clone, DSpaceObject objectToClone,
        Map<UUID, Group> scopedRoles) throws SQLException, AuthorizeException {
        for (ResourcePolicy policy : objectToClone.getResourcePolicies()) {
            Group group = policy.getGroup();
            if (group != null && scopedRoles.containsKey(group.getID())) {
                authorizeService.removeGroupPolicies(context, clone,
                        groupService.findByName(context, Group.ANONYMOUS));
                authorizeService.addPolicy(context, clone, policy.getAction(), scopedRoles.get(group.getID()));
            }
        }
    }

    private void cloneWorkflowGroup(Context context, Group workflowGroup, Collection newCollection, int step,
        Map<UUID, Group> scopedRoles) throws SQLException, AuthorizeException {

        if (workflowGroup == null) {
            return;
        }

        Group newWorkflowGroup = collectionService.createWorkflowGroup(context, newCollection, step);
        addInstitutionalScopedRoleMembers(context, workflowGroup, newWorkflowGroup, scopedRoles);

    }

    /**
     * Create a new Institutional Scoped Role for each existing Institutional Role.
     * The community will keep a reference to the institutional scoped role via
     * perucris.community.institutional-scoped-role metadata.
     *
     * @return a map between the institutional roles and the related scopes for the
     *         institution community
     */
    private Map<UUID, Group> createScopedRoles(Context context, Community project, Community parent,
            String projectRootCommunityID, boolean isProject) throws SQLException, AuthorizeException {
        Map<UUID, Group> groupsMap;
        Map<UUID, Group> scopedRolesMap;
        String[] projectTemplates = configurationService.getArrayProperty("project.template.groups-name");
        if (isProject || Objects.isNull(parent)) {
            String[] projectUserGroups = configurationService.getArrayProperty("project.template.add-user-groups");
            groupsMap = new HashMap<>(projectTemplates.length + projectUserGroups.length);
            groupsMap.putAll(
                createTemplateGroups(
                    context,
                    project,
                    projectUserGroups
                )
            );
            Map<UUID, Group> projectGroupsMap =
                createTemplateGroups(
                    context,
                    project,
                    projectTemplates
                );
            if (!isProject) {
                groupsMap.putAll(projectGroupsMap);
            }
            scopedRolesMap =
                Stream.concat(groupsMap.entrySet().stream(), projectGroupsMap.entrySet().stream())
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b));
        } else {
            String[] fundingTemplates = configurationService.getArrayProperty("project.funding-template.groups-name");
            groupsMap = new HashMap<>(fundingTemplates.length + projectTemplates.length);
            groupsMap.putAll(
                createTemplateGroups(
                    context,
                    project,
                    fundingTemplates
                )
            );
            Community projectComm =
                Optional.ofNullable(parent.getParentCommunities())
                    .filter(l -> !l.isEmpty())
                    .map(l -> l.get(0))
                    .orElse(null);
            Community parentProjComm =
                Optional.ofNullable(projectComm)
                    .map(Community::getParentCommunities)
                    .filter(l -> !l.isEmpty())
                    .map(l -> l.get(0))
                    .orElse(null);
            Map<UUID, Group> projectGroupsMap = Map.of();
            if (
                    projectComm != null &&
                    parentProjComm != null &&
                    parentProjComm.getID().toString().equals(projectRootCommunityID)
            ) {

                projectGroupsMap = new HashMap<UUID, Group>(projectTemplates.length);

                addCommunityGroupsFromTemplate(
                    context,
                    projectComm,
                    projectGroupsMap,
                    projectTemplates
                );
            }
            scopedRolesMap =
                Stream.concat(groupsMap.entrySet().stream(), projectGroupsMap.entrySet().stream())
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b));
        }

        groupsMap.forEach((uuid, group) -> groupService.addMember(context, group, context.getCurrentUser()));

        return scopedRolesMap;
    }

    private Map<UUID, Group> createTemplateGroups(
        Context context,
        Community project,
        String[] templateGroupsName
    ) throws SQLException, AuthorizeException {
        if (templateGroupsName == null || templateGroupsName.length == 0) {
            return Map.of();
        }
        Map<UUID, Group> groupsMap = new HashMap<>(templateGroupsName.length);
        for (int i = 0; i < templateGroupsName.length; i++) {
            String currentTemplateGroup = templateGroupsName[i];
            Group templateGroup = groupService.findByName(context, currentTemplateGroup);
            if (isValidGroup(templateGroup)) {
                String projectId = project.getID().toString();
                String[] name_parts = getValidGroupTemplateParts(templateGroup);
                Group scopedRole = groupService.findByName(context, formatGroup(projectId, name_parts));
                if (scopedRole == null) {
                    scopedRole = createGroupFromTemplate(context, projectId, name_parts);
                }
                groupsMap.put(templateGroup.getID(), scopedRole);
            } else {
                log.warn("Cannot find a valid group for properties group template {}", currentTemplateGroup);
            }
        }
        return groupsMap;
    }

    private void addCommunityGroupsFromTemplate(
        Context context,
        Community project,
        Map<UUID, Group> groupsMap,
        String[] templateGroupsName
    ) throws SQLException, AuthorizeException {
        if (templateGroupsName == null || templateGroupsName.length == 0) {
            return;
        }
        for (int i = 0; i < templateGroupsName.length; i++) {
            String currentTemplateGroup = templateGroupsName[i];
            Group templateGroup = groupService.findByName(context, currentTemplateGroup);
            if (isValidGroup(templateGroup)) {
                String[] name_parts = getValidGroupTemplateParts(templateGroup);
                Group scopedRole = getValidProjectGroup(context, project.getID().toString(), name_parts);
                groupsMap.put(templateGroup.getID(), scopedRole);
            } else {
                log.warn("Cannot find a valid group for properties group template {}", currentTemplateGroup);
            }
        }
    }

    private Group getValidProjectGroup(Context context, String projectId, String[] name_parts) throws SQLException {
        Group scopedRole = groupService.findByName(context, formatGroup(projectId, name_parts));
        if (!isValidGroup(scopedRole)) {
            throw new RuntimeException(
                "Cannot find project community for funding tamplate " +
                projectId
            );
        }
        return scopedRole;
    }

    private String[] getValidGroupTemplateParts(Group templateGroup) {
        String[] name_parts = extractName(templateGroup.getName());
        if (!isValidTemplate(name_parts)) {
            throw new RuntimeException(
                "The group name : " +
                templateGroup.getName() +
                " is bad formed! It should have the following format : project_<UUID>_<NAME>_group"
            );
        }
        return name_parts;
    }


    private Group createGroupFromTemplate(
        Context context,
        String projectId,
        String[] name_parts
    ) throws SQLException, AuthorizeException {
        Group scopedRole = groupService.create(context);
        groupService.setName(scopedRole, formatGroup(projectId, name_parts));
        return scopedRole;
    }

    private String formatGroup(String projectId, String[] name_parts) {
        return new StringBuilder(name_parts[0])
                    .append(projectId)
                    .append(name_parts[1])
                    .append("_group")
                    .toString();
    }

    private boolean isValidTemplate(String[] name_parts) {
        return name_parts != null && name_parts.length == 2;
    }

    private boolean isValidGroup(Group templateGroup) {
        return templateGroup != null && StringUtils.isNotBlank(templateGroup.getName());
    }

    private String[] extractName(String groupName) {
        if (groupName == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("^((?:project_|funding_)).*(_.*)(_group)$");
        Matcher matcher = pattern.matcher(groupName);

        if (matcher.matches()) {
            return new String[] {matcher.group(1),matcher.group(2)};
        } else {
            return null;
        }
    }
}
