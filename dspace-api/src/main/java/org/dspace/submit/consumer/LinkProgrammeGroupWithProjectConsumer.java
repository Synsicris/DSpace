/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_CALLTOPROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * The purpose of this consumer is to link a programme reader group
 * to the project reader group.
 * 
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 */
public class LinkProgrammeGroupWithProjectConsumer implements Consumer {

    private ItemService itemService;
    private GroupService groupService;
    private ProjectConsumerService projectConsumerService;

    @Override
    public void initialize() throws Exception {
        itemService = ContentServiceFactory.getInstance().getItemService();
        groupService = EPersonServiceFactory.getInstance().getGroupService();
        projectConsumerService = new DSpace().getServiceManager()
                                             .getServiceByName(ProjectConsumerServiceImpl.class.getName(),
                                                 ProjectConsumerServiceImpl.class);
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        DSpaceObject dso = event.getSubject(context);

        if (!(dso instanceof Item)) {
            return;
        }

        Item item = (Item) dso;
        if (isEntityTypeEqualTo(item, "Project") &&
            isEventTypeEqualTo(event, Event.MODIFY_METADATA) &&
            isEventMetadataEqualTo(event, getRelationMetadata())) {
            consumeItem(context, item);
        }
    }

    private boolean isEntityTypeEqualTo(Item item, String entityType) {
        return itemService.getEntityType(item).equals(entityType);
    }

    private boolean isEventTypeEqualTo(Event event, int eventType) {
        return event.getEventType() == eventType;
    }

    private boolean isEventMetadataEqualTo(Event event, String metadata) {
        return event.getDetail() != null && event.getDetail().contains(metadata);
    }

    private String getRelationMetadata() {
        return MD_RELATION_CALLTOPROGRAMME.toString().replaceAll("\\.", "_");
    }

    private void consumeItem(Context context, Item item) throws SQLException, AuthorizeException {

        Item programmeItem = getRelatedProgrammeItem(context, item);
        if (Objects.isNull(programmeItem)) {
            return;
        }

        Group programmeGroup = getProgrammeGroup(context, programmeItem);
        if (Objects.isNull(programmeGroup)) {
            return;
        }

        Group parentGroup = getProjectCommunityGroupByRole(context, item, READERS_ROLE);
        if (!Objects.isNull(parentGroup)) {
            removeAllChildGroups(context, parentGroup);
            addChildGroup(context, parentGroup, programmeGroup);
        }
    }

    private Item getRelatedProgrammeItem(Context context, Item item) throws SQLException {
        Item relatedItem;
        MetadataValue metadataValue = getRelationMetadataValue(item);
        if (!Objects.isNull(metadataValue) && hasAuthority(metadataValue)) {
            relatedItem = itemService.find(context, UUID.fromString(metadataValue.getAuthority()));
            if (!Objects.isNull(relatedItem) && isEntityTypeEqualTo(relatedItem, "programme")) {
                return relatedItem;
            }
        }
        return null;
    }
    private MetadataValue getRelationMetadataValue(Item item) {
        List<MetadataValue> metadataValues =
            itemService.getMetadata(item, "oairecerif", "fundingParent", null, null);
        return !Objects.isNull(metadataValues) ? getFirstMetadataValue(metadataValues) : null;
    }

    private MetadataValue getFirstMetadataValue(List<MetadataValue> metadataValues) {
        return metadataValues.stream().findFirst().get();
    }

    private boolean hasAuthority(MetadataValue metadataValue) {
        return StringUtils.isNotEmpty(metadataValue.getAuthority());
    }

    private Group getProgrammeGroup(Context context, Item programmeItem) throws SQLException {
        return groupService.findByName(context, String.format(PROGRAMME_GROUP_TEMPLATE, programmeItem.getID()));
    }

    private Group getProjectCommunityGroupByRole(Context context, Item projectItem, String role) throws SQLException {
        Community community = projectConsumerService.getProjectCommunity(context, projectItem);
        return projectConsumerService.getProjectCommunityGroupByRole(context, community, role);
    }

    private void removeAllChildGroups(Context context, Group parentGroup) throws SQLException, AuthorizeException {
        List<Group> childGroups = getCopyOfMemberGroups(parentGroup.getMemberGroups());
        for (Group childGroup : childGroups) {
            groupService.removeMember(context, parentGroup, childGroup);
        }
        groupService.update(context, parentGroup);
    }

    private List<Group> getCopyOfMemberGroups(List<Group> memberGroups) {
        List<Group> copyOfMemberGroups = new ArrayList<>();
        memberGroups.stream()
                    .forEach(group -> copyOfMemberGroups.add(group));
        return copyOfMemberGroups;
    }

    private void addChildGroup(Context context, Group parentGroup, Group childGroup)
        throws SQLException, AuthorizeException {
        groupService.addMember(context, parentGroup, childGroup);
        groupService.update(context, parentGroup);

    }

    @Override
    public void end(Context context) throws Exception {
    }

    @Override
    public void finish(Context context) throws Exception {}

}