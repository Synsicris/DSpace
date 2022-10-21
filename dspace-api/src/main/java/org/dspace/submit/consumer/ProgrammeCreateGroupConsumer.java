/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;

import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_GROUP_TEMPLATE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

        String groupName = String.format(PROGRAMME_GROUP_TEMPLATE, uuid.toString());
        if (eventType == Event.DELETE) {
            deleteGroupByName(context, groupName);
            putProcessed(uuid, eventType);
        } else if (eventType == Event.INSTALL) {
            createProgrammeGroup(context, groupName);
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

    private void createProgrammeGroup(Context context, String groupName) {
        try {
            context.turnOffAuthorisationSystem();
            Group group = groupService.create(context);
            groupService.setName(group, groupName);
            groupService.update(context, group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            context.restoreAuthSystemState();
        }
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
