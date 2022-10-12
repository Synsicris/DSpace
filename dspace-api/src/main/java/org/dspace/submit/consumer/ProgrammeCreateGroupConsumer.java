package org.dspace.submit.consumer;

import java.io.IOException;
import java.sql.SQLException;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
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

    private final String PROGRAMME_GROUP_NAME = "programme_%s_group";

    private ItemService itemService;
    private GroupService groupService;

    @Override
    public void initialize() throws Exception {
        this.itemService = ContentServiceFactory.getInstance().getItemService();
        this.groupService = EPersonServiceFactory.getInstance().getGroupService();
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        DSpaceObject dso = event.getSubject(context);
        int eventType = event.getEventType();
        String groupName = String.format(PROGRAMME_GROUP_NAME, event.getSubjectID());

        if (eventType == Event.DELETE) {
            deleteGroupByName(context, groupName);
        } else if (eventType == Event.INSTALL) {
            if (isEntityTypeEqualTo(dso, "programme")) {
                createProgrammeGroup(context, groupName);
            }
        }

    }

    private void deleteGroupByName(Context context, String groupName)
        throws SQLException, AuthorizeException, IOException {
        Group group = groupService.findByName(context, groupName);
        if (group != null) {
            groupService.delete(context, group);
        }
    }

    private boolean isEntityTypeEqualTo(DSpaceObject dso, String entityType) {
        if (!isItem(dso)) {
            return false;
        }
        Item item = (Item) dso;
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

    @Override
    public void end(Context ctx) throws Exception {

    }

    @Override
    public void finish(Context ctx) throws Exception {

    }
}
