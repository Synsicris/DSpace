/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.project.util.ProjectConstants;

/**
 * Implementation of {@link Consumer} this consumer has the role
 * of deleting hung groups of deleted communities.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class DeleteGroupsConsumer implements Consumer {

    private Set<UUID> communitiesAlreadyProcessed = new HashSet<UUID>();

    private GroupService groupService;

    /**
     * Initalise the consumer
     *
     * @throws Exception if error
     */
    @Override
    public void initialize() throws Exception {
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        if (event.getEventType() == Event.DELETE) {
            UUID uuid = event.getSubjectID();
            if (event.getSubjectType() == Constants.COMMUNITY && Objects.nonNull(uuid)) {
                if (communitiesAlreadyProcessed.contains(uuid)) {
                    return;
                }

                Group membersGroup = searchGroup(context, uuid, ProjectConstants.MEMBERS_ROLE, false);
                Group coordinatorGroup = searchGroup(context, uuid, ProjectConstants.COORDINATORS_ROLE, false);
                Group fundersGroup = searchGroup(context, uuid, ProjectConstants.FUNDERS_ROLE, false);
                Group readersGroup = searchGroup(context, uuid, ProjectConstants.READERS_ROLE, false);

                Group fundingMembersGroup = searchGroup(context, uuid, ProjectConstants.MEMBERS_ROLE, true);
                Group fundingCoordinatorGroup = searchGroup(context, uuid, ProjectConstants.COORDINATORS_ROLE, true);

                deleteGroup(context, membersGroup);
                deleteGroup(context, coordinatorGroup);
                deleteGroup(context, fundersGroup);
                deleteGroup(context, readersGroup);

                deleteGroup(context, fundingMembersGroup);
                deleteGroup(context, fundingCoordinatorGroup);
                communitiesAlreadyProcessed.add(uuid);
            }
        }
    }

    /**
     * Handle the end of the event
     *
     * @param context The relevant DSpace Context.
     * @throws Exception if error
     */
    @Override
    public void end(Context context) throws Exception {
        communitiesAlreadyProcessed.clear();
    }

    /**
     * Finish the event
     *
     * @param context The relevant DSpace Context.
     */
    @Override
    public void finish(Context context) throws Exception {}

    private Group searchGroup(Context context, UUID communityUuid, String type, boolean isFunding) throws SQLException {
        StringBuilder groupName = isFunding ? new StringBuilder( "funding_") : new StringBuilder( "project_");
        groupName.append(communityUuid.toString()).append("_").append(type).append("_group");
        return groupService.findByName(context, groupName.toString());
    }

    private void deleteGroup(Context context, Group membersGroup) throws SQLException, AuthorizeException, IOException {
        if (Objects.nonNull(membersGroup)) {
            groupService.delete(context, membersGroup);
        }
    }

}