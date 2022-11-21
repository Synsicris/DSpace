/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;

import static org.dspace.project.util.ProjectConstants.MD_RELATION_CALLTOPROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MANAGERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROGRAMME_MEMBERS_GROUP_TEMPLATE;
import static org.dspace.project.util.ProjectConstants.PROJECT_ENTITY;
import static org.dspace.project.util.ProjectConstants.READERS_ROLE;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
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
 * The purpose of this consumer is to link a programme reader group to the
 * project reader group.
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
        projectConsumerService =
            new DSpace().getServiceManager()
                .getServiceByName(
                    ProjectConsumerServiceImpl.class.getName(),
                    ProjectConsumerServiceImpl.class
                );
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        if (
                event.getSubjectType() != Constants.ITEM ||
                event.getEventType() != Event.MODIFY_METADATA ||
                event.getDetail() == null ||
                !event.getDetail().contains(MD_RELATION_CALLTOPROGRAMME.toString().replaceAll("\\.", "_"))
        ) {
            return;
        }

        DSpaceObject dso = event.getSubject(context);

        if (dso == null) {
            return;
        }

        Item item = (Item) dso;
        if (isEntityTypeEqualTo(item, PROJECT_ENTITY)) {
            consumeItem(context, item);
        }
    }

    private void consumeItem(Context context, Item item) throws SQLException, AuthorizeException {

        Item programmeItem = getRelatedProgrammeItem(context, item);
        if (Objects.isNull(programmeItem)) {
            return;
        }

        Group programmeMemberGroup = getProgrammeMembersGroup(context, programmeItem);
        Group programmeManagerGroup = getProgrammeManagerGroup(context, programmeItem);
        if (Objects.isNull(programmeMemberGroup) && Objects.isNull(programmeManagerGroup)) {
            return;
        }

        Group parentGroup = getProjectCommunityGroupByRole(context, item, READERS_ROLE);
        if (!Objects.isNull(parentGroup)) {
            removeAllChildGroups(context, parentGroup);
            addChildGroups(context, parentGroup, programmeMemberGroup, programmeManagerGroup);
        }
    }

    private Item getRelatedProgrammeItem(Context context, Item item) throws SQLException {
        Item relatedItem = null;
        MetadataValue metadataValue = getRelationMetadataValue(item);
        if (!Objects.isNull(metadataValue) && hasAuthority(metadataValue)) {
            relatedItem =
                Optional.ofNullable(itemService.find(context, UUID.fromString(metadataValue.getAuthority())))
                    .filter(found -> isEntityTypeEqualTo(found, PROGRAMME))
                    .orElse(null);
        }
        return relatedItem;
    }

    private boolean isEntityTypeEqualTo(Item item, String entityType) {
        return itemService.getEntityType(item).equals(entityType);
    }

    private MetadataValue getRelationMetadataValue(Item item) {
        return Optional.ofNullable(
                itemService.getMetadata(
                    item, MD_RELATION_CALLTOPROGRAMME.schema, MD_RELATION_CALLTOPROGRAMME.element,
                    MD_RELATION_CALLTOPROGRAMME.qualifier, null
                )
            )
            .filter(metadatas -> !metadatas.isEmpty())
            .map(metadatas -> metadatas.get(0))
            .orElse(null);
    }

    private boolean hasAuthority(MetadataValue metadataValue) {
        return StringUtils.isNotEmpty(metadataValue.getAuthority());
    }

    private Group getProgrammeMembersGroup(Context context, Item programmeItem) throws SQLException {
        return groupService.findByName(context, String.format(PROGRAMME_MEMBERS_GROUP_TEMPLATE, programmeItem.getID()));
    }

    private Group getProgrammeManagerGroup(Context context, Item programmeItem) throws SQLException {
        return groupService
            .findByName(context, String.format(PROGRAMME_MANAGERS_GROUP_TEMPLATE, programmeItem.getID()));
    }

    private Group getProjectCommunityGroupByRole(Context context, Item projectItem, String role) throws SQLException {
        Community community = projectConsumerService.getProjectCommunity(context, projectItem);
        return projectConsumerService.getProjectCommunityGroupByRole(context, community, role);
    }

    private void removeAllChildGroups(Context context, Group parentGroup) throws SQLException, AuthorizeException {
        List<Group> childGroups = List.copyOf(parentGroup.getMemberGroups());
        for (Group childGroup : childGroups) {
            groupService.removeMember(context, parentGroup, childGroup);
        }
        groupService.update(context, parentGroup);
    }

    private void addChildGroups(Context context, Group parentGroup, Group ...childGroups) {
        Stream.of(childGroups)
            .filter(Objects::nonNull)
            .forEach(childGroup -> this.addChildGroup(context, parentGroup, childGroup));
    }

    private void addChildGroup(Context context, Group parentGroup, Group childGroup) {
        try {
            groupService.addMember(context, parentGroup, childGroup);
            groupService.update(context, parentGroup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void end(Context context) throws Exception {
    }

    @Override
    public void finish(Context context) throws Exception {
    }

}