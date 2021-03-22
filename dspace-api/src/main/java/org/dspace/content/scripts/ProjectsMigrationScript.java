/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.scripts;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
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
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectsMigrationScript extends
             DSpaceRunnable<ProjectsMigrationScriptConfiguration<ProjectsMigrationScript>> {

    private static final Logger log = LogManager.getLogger(UpdateCrisMetricsWithExternalSource.class);

    private ConfigurationService configurationService;

    private CommunityService communityService;

    private GroupService groupService;

    private Context context;

    @Override
    public void setup() throws ParseException {
        this.configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        this.communityService = ContentServiceFactory.getInstance().getCommunityService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
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
        Community projectCommunity = getProjectCommunity();
        if (Objects.isNull(projectCommunity)) {
            throw new RuntimeException("The ProjectCommunity has not been found,"
                                     + " check the propery : project.parent-community-id");
        }
        for (Community community : projectCommunity.getSubcommunities()) {
            checkGroups(community);
        }
    }

    private void checkGroups(Community community) throws SQLException, AuthorizeException {
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
                                                  .append("_admin_group");

        Group newMemberGroup = groupService.create(context);
        groupService.setName(group, memberGroupName.toString());
        groupService.update(context, newMemberGroup);

        for (EPerson member : group.getMembers()) {
            groupService.addMember(context, newMemberGroup, member);
        }
    }

    private Community getProjectCommunity() throws SQLException {
        String  projectUuid = configurationService.getProperty("project.parent-community-id");
        if (StringUtils.isBlank(projectUuid)) {
            return communityService.find(context, UUID.fromString(projectUuid));
        }
        return null;
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