/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;

import static org.dspace.content.authority.Choices.CF_ACCEPTED;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.util.AuthorizeUtil;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.event.Consumer;
import org.dspace.event.Event;

/**
 * The consumer to create new programme group when create new programme item.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ProgrammeCreateGroupConsumer implements Consumer {

    private ItemService itemService;
    private GroupService groupService;
    private Map<UUID, Set<Integer>> alreadyProcessed;

    @Override
    public void initialize() throws Exception {
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        if (event.getSubjectType() != Constants.ITEM) {
            return;
        }

        DSpaceObject dso = event.getSubject(context);
        if (dso != null && !isEntityTypeEqualTo((Item) dso, PROGRAMME)) {
            return;
        }

        if (!AuthorizeUtil.canAddOrRemoveProgramme(context, dso)) {
            return;
        }

        UUID uuid = event.getSubjectID();
        int eventType = event.getEventType();

        if (hasBeenProcessed(uuid, eventType)) {
            return;
        }

        String memberGroupName = String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, uuid.toString());
        String managerGroupName = String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, uuid.toString());
        String projectFunderGroupName = String.format(PROGRAMME_PROJECT_FUNDERS_GROUP_TEMPLATE, uuid.toString());

        if (eventType == Event.DELETE) {
            deleteGroupsByName(context, memberGroupName, managerGroupName, projectFunderGroupName);
            putProcessed(uuid, eventType);
        } else if (eventType == Event.INSTALL) {
            createProgrammeGroupsAndMetadata(
                context, (Item) dso, memberGroupName, managerGroupName, projectFunderGroupName
            );
            putProcessed(uuid, eventType);
        }

    }

    @Override
    public void end(Context ctx) throws Exception {
        this.alreadyProcessed = null;
    }

    @Override
    public void finish(Context ctx) throws Exception {

    }

    private Map<UUID, Set<Integer>> getAlreadyProcessed() {
        if (this.alreadyProcessed == null) {
            this.alreadyProcessed = new HashMap<>();
        }
        return this.alreadyProcessed;
    }

    private void deleteGroupsByName(Context context, String ...groupNames) {
        Stream.of(groupNames)
            .filter(StringUtils::isNotBlank)
            .forEach(group -> this.deleteGroupByName(context, group));
    }

    private void deleteGroupByName(Context context, String groupName) {
        try {
            context.turnOffAuthorisationSystem();
            Group group = groupService.findByName(context, groupName);
            if (group != null) {
                groupService.delete(context, group);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private boolean isEntityTypeEqualTo(Item item, String entityType) {
        return entityType.equals(itemService.getEntityType(item));
    }

    private void createProgrammeGroupsAndMetadata(Context context, Item dso, String memberGroupName,
            String managerGroupName, String projectFunderGroupName) {
        try {
            createProgrammeGroup(context, memberGroupName);
            createProgrammeGroup(context, projectFunderGroupName);
            addPolicyGroupMetadata(context, dso, createProgrammeGroup(context, managerGroupName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addPolicyGroupMetadata(Context context, Item dso, Group programmeManagerGroup) throws SQLException {
        this.itemService.addMetadata(
            context, dso, MD_POLICY_GROUP.schema, MD_POLICY_GROUP.element, MD_POLICY_GROUP.qualifier, null,
            programmeManagerGroup.getName(), programmeManagerGroup.getID().toString(), CF_ACCEPTED, 0
        );
    }

    private Group createProgrammeGroup(Context context, String groupName) {
        Group group = null;
        try {
            context.turnOffAuthorisationSystem();
            group = groupService.create(context);
            groupService.setName(group, groupName);
            groupService.update(context, group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
        return group;
    }

    private void putProcessed(UUID id, Integer event) {
        Set<Integer> processed = this.getProcessed(id);
        if (processed == null) {
            processed = new HashSet<>(2);
        }
        processed.add(event);
        this.getAlreadyProcessed().put(id, processed);
    }

    private Set<Integer> getProcessed(UUID id) {
        return this.getAlreadyProcessed().get(id);
    }

    private boolean hasBeenProcessed(UUID id, Integer event) {
        return Optional.ofNullable(this.getAlreadyProcessed().get(id))
                .map(set -> set.contains(event))
                .orElse(false);
    }
}
