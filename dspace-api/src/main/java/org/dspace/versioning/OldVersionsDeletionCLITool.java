package org.dspace.versioning;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_ITEM_ENTITY;
import static org.dspace.project.util.ProjectConstants.MD_VERSION_OFFICIAL;

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

    private final ConfigurationService configurationService;
    private final CommunityService communityService;
    private final ItemService itemService;
    private final int threshold;
    private final VersioningService versioningService;
    private final VersionHistoryService versionHistoryService;

    public OldVersionsDeletionCLITool() {
        this.configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.communityService = ContentServiceFactory.getInstance().getCommunityService();
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.versioningService = VersionServiceFactory.getInstance().getVersionService();
        this.versionHistoryService = VersionServiceFactory.getInstance().getVersionHistoryService();
        this.threshold = getThreshold();
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
                throw new RuntimeException(
                    "Can't delete items: 'project.parent-community-id' property not configured!"
                );
            }

            for (Community community : projectCommunity.getSubcommunities()) {
                oldVersionsDeletionCLITool.deleteOldVersionsByCommunity(context, community);
            }

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            if (context != null) {
                context.restoreAuthSystemState();
                context.complete();
                if (context.isValid()) {
                    context.abort();
                }
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

        Item projectItem = getProjectItem(context, community);
        VersionHistory versionHistory = versionHistoryService.findByItem(context, projectItem);

        if (Objects.isNull(versionHistory)) {
            return;
        }

        int versions = versioningService.countVersionsByHistoryWithItem(context, versionHistory);

        if (versions <= threshold) {
            return;
        }

        List<Version> allVersions = versioningService.getVersionsByHistory(context, versionHistory);
        List<Version> notOfficialVersions = getNotOfficialVersions(projectItem, allVersions);

        for (int i = notOfficialVersions.size() - 1; i >= threshold; i--) {
            itemService.delete(
                context,
                itemService.find(context, notOfficialVersions.get(i).getItem().getID())
            );
        }
    }

    private int getThreshold() {
        String threshold = configurationService.getProperty("versioning.delete.threshold");
        if (StringUtils.isEmpty(threshold)) {
            throw new RuntimeException(
                "The threshold for the deletable versions is not configured, " +
                "please configure it: versioning.delete.threshold."
            );
        }
        return Integer.parseInt(threshold);
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
        return CollectionUtils.isEmpty(
            itemService.getMetadata(
                version.getItem(),
                MD_VERSION_OFFICIAL.schema,
                MD_VERSION_OFFICIAL.element,
                MD_VERSION_OFFICIAL.qualifier,
                Item.ANY
            )
        );
    }
}
