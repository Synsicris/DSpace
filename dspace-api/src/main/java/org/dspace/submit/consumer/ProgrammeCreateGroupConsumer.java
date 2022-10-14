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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.dspace.authorize.AuthorizeException;
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
        DSpaceObject dso = event.getSubject(context);
        if (dso != null && !isItem(dso) || event.getSubjectType() != Constants.ITEM) {
            return;
        }

        UUID uuid = event.getSubjectID();
        int eventType = event.getEventType();
        String groupName = String.format(PROGRAMME_GROUP_TEMPLATE, uuid.toString());

        boolean processed = hasBeenProcessed(uuid, eventType);
        if (eventType == Event.DELETE && !processed) {
            deleteGroupByName(context, groupName);
            putProcessed(uuid, eventType);
        } else if (eventType == Event.INSTALL && !processed) {
            Item item = (Item) dso;
            if (isEntityTypeEqualTo(item, PROGRAMME)) {
                createProgrammeGroup(context, groupName);
                putProcessed(uuid, eventType);
            }
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


    private void deleteGroupByName(Context context, String groupName)
        throws SQLException, AuthorizeException, IOException {
        Group group = groupService.findByName(context, groupName);
        if (group != null) {
            groupService.delete(context, group);
        }
    }

    private boolean isEntityTypeEqualTo(Item item, String entityType) {
        return itemService.getEntityType(item).equals(entityType);
    }

    private boolean isItem(DSpaceObject dso) {
        return dso instanceof Item;
    }

    private void createProgrammeGroup(Context context, String groupName) throws SQLException, AuthorizeException {
        Group group = groupService.create(context);
        groupService.setName(group, groupName);
        groupService.update(context, group);
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
