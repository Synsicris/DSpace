package org.dspace.versioning;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.util.UUIDUtils;
import org.dspace.versioning.factory.VersionServiceFactory;
import org.dspace.versioning.service.VersionHistoryService;
import org.dspace.versioning.service.VersioningService;

/**
 * This script is used to delete the old not-official versions.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 *
 */
public class OldVersionsDeletionCLITool {
    private final static Logger log = LogManager.getLogger();

    private ConfigurationService configurationService;
    private CommunityService communityService;
    private ItemService itemService;
    private static VersioningService versioningService;
    private static VersionHistoryService versionHistoryService;

    public OldVersionsDeletionCLITool() {
        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        communityService = ContentServiceFactory.getInstance().getCommunityService();
        itemService = ContentServiceFactory.getInstance().getItemService();
        versioningService = VersionServiceFactory.getInstance().getVersionService();
        versionHistoryService = VersionServiceFactory.getInstance().getVersionHistoryService();
    }

    public static void main(String[] argv) throws SQLException {
        OldVersionsDeletionCLITool oldVersionsDeletionCLITool = new OldVersionsDeletionCLITool();

        Context context = null;

        try {
            context = new Context(Context.Mode.BATCH_EDIT);
            context.turnOffAuthorisationSystem();

            Community projectCommunity =
                oldVersionsDeletionCLITool.getCommunityByProperty(context, "project.parent-community-id");

            if (Objects.isNull(projectCommunity)) {
                return;
            }

            for (Community community : projectCommunity.getSubcommunities()) {
                oldVersionsDeletionCLITool.deleteOldVersionsByCommunity(context, community);
            }

            context.restoreAuthSystemState();
            context.complete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        } finally {
            if (context != null && context.isValid()) {
                context.abort();
            }
        }

    }

    private Community getCommunityByProperty(Context context, String property) throws SQLException {
        String communityUuid = configurationService.getProperty(property);
        if (StringUtils.isNotBlank(communityUuid)) {
            return communityService.find(context, UUID.fromString(communityUuid));
        }
        return null;
    }

    private void deleteOldVersionsByCommunity(Context context, Community community)
        throws SQLException, AuthorizeException, IOException {

        int threshold = getThreshold();
        Item projectItem = getProjectItem(context, community);
        VersionHistory versionHistory = versionHistoryService.findByItem(context, projectItem);
        List<Version> allVersions = versioningService.getVersionsByHistory(context, versionHistory);

        if (allVersions.size() <= threshold) {
            return;
        }

        List<Version> notOfficialVersions =getNotOfficialVersions(projectItem, allVersions);
        int notDeletableCount =
            getNotDeletableCount(allVersions.size(), notOfficialVersions.size(), threshold);

        for (int i = notOfficialVersions.size() - 1  ; i >= notDeletableCount ; i--) {
            itemService.delete(context,
                itemService.find(context, notOfficialVersions.get(i).getItem().getID()));
        }
    }

    private int getThreshold() {
        String threshold = configurationService.getProperty("versioning.delete.threshold");
        if (StringUtils.isNotEmpty(threshold)) {
            return Integer.parseInt(threshold);
        }
        return 9999;
    }

    private Item getProjectItem(Context context, Community community) throws SQLException {
        List<MetadataValue> values =
            communityService.getMetadata(community, MD_RELATION_ITEM_ENTITY.schema,
                MD_RELATION_ITEM_ENTITY.element, MD_RELATION_ITEM_ENTITY.qualifier, null);
        return itemService.find(context, UUIDUtils.fromString(values.get(0).getAuthority()));
    }

    private List<Version> getNotOfficialVersions(Item projectItem, List<Version> versions) {
        return versions.stream()
                       .filter(version -> isNotOfficial(version))
                       .filter(version -> version.getItem().getID() != projectItem.getID())
                       .collect(Collectors.toList());
    }

    private boolean isNotOfficial(Version version) {
        List<MetadataValue> values =
            itemService.getMetadata(version.getItem(), "synsicris", "version", "official", Item.ANY);
        return !CollectionUtils.isNotEmpty(values);
    }

    private int getNotDeletableCount(int allVersions, int notOfficialVersions, int threshold) {
        int officialVersionsCount = allVersions - notOfficialVersions;
        int notDeletableCount = threshold - officialVersionsCount;
        return notDeletableCount > 0 ? notDeletableCount : 0;
    }

}
