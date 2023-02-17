/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;

import static org.dspace.content.Item.ANY;
import static org.dspace.content.authority.Choices.CF_ACCEPTED;
import static org.dspace.core.CrisConstants.MD_ENTITY_TYPE;
import static org.dspace.event.Event.MODIFY_METADATA;
import static org.dspace.project.util.ProjectConstants.COMMENT_ENTITY;
import static org.dspace.project.util.ProjectConstants.FUNDERS_ROLE;
import static org.dspace.project.util.ProjectConstants.MD_FUNDER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_COMMENT_PROJECT;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.kernel.ServiceManager;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link Consumer}
 * This consumer is used to fill "synsicris.funder-policy.group" metadata with the correct policy group.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class CommentPolicyConsumer implements Consumer {

    private Set<UUID> itemsAlreadyProcessed = new HashSet<UUID>();

    private ItemService itemService;
    private ProjectConsumerService projectConsumerService;

    @Override
    public void initialize() throws Exception {
        ServiceManager serviceManager = new DSpace().getServiceManager();
        itemService = ContentServiceFactory.getInstance().getItemService();
        projectConsumerService = serviceManager.getServiceByName(
                                                ProjectConsumerServiceImpl.class.getName(),
                                                ProjectConsumerServiceImpl.class);
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        if (
            MODIFY_METADATA == event.getEventType() &&
            StringUtils.contains(event.getDetail(), MD_RELATION_COMMENT_PROJECT.toString().replaceAll("\\.", "_")) &&
            context.getCurrentUser() != null && !itemsAlreadyProcessed.contains(event.getSubjectID())
        ) {
            Object dso = event.getSubject(context);
            if (dso instanceof Item) {
                Item commentItem = (Item) dso;
                String entityType = itemService.getMetadataFirstValue(commentItem, MD_ENTITY_TYPE, ANY);
                if (!StringUtils.equals(COMMENT_ENTITY, entityType)) {
                    return;
                }
                List<MetadataValue> metadataValues =
                    itemService.getMetadata(
                        commentItem,
                        MD_RELATION_COMMENT_PROJECT.schema,
                        MD_RELATION_COMMENT_PROJECT.element,
                        MD_RELATION_COMMENT_PROJECT.qualifier,
                        ANY
                    );
                MetadataValue mv = null;
                if (CollectionUtils.isNotEmpty(metadataValues) && (mv = metadataValues.get(0)) != null) {
                    String uuidOfProjectItem = mv.getAuthority();
                    if (StringUtils.isNotBlank(uuidOfProjectItem)) {
                        Group group = getPolicyGroup(context, uuidOfProjectItem);
                        if (group != null) {
                            setFunderPolicyGroupMetadata(context, commentItem, group);
                            itemsAlreadyProcessed.add(event.getSubjectID());
                        }
                    }
                }
            }
        }
    }

    private void setFunderPolicyGroupMetadata(Context context, Item item, Group group) throws SQLException {
        String value = group.getName();
        String authority = group.getID().toString();
        String funderPolicyGroup =
            itemService.getMetadataFirstValue(
                item,
                MD_FUNDER_POLICY_GROUP.schema,
                MD_FUNDER_POLICY_GROUP.element,
                MD_FUNDER_POLICY_GROUP.qualifier,
                ANY
            );
        if (StringUtils.isNotBlank(funderPolicyGroup)) {
            itemService.replaceMetadata(
                context, item,
                MD_FUNDER_POLICY_GROUP.schema,
                MD_FUNDER_POLICY_GROUP.element,
                MD_FUNDER_POLICY_GROUP.qualifier,
                null, value, authority, CF_ACCEPTED, 0
            );
        } else {
            itemService.addMetadata(
                context, item,
                MD_FUNDER_POLICY_GROUP.schema,
                MD_FUNDER_POLICY_GROUP.element,
                MD_FUNDER_POLICY_GROUP.qualifier,
                null, value, authority, CF_ACCEPTED, 0
            );
        }
    }

    private Group getPolicyGroup(Context context, String uuidOfProjectItem) throws SQLException {
        Item projectItem = itemService.find(context, UUID.fromString(uuidOfProjectItem));
        Community communityOfProject = projectConsumerService.getFirstOwningCommunity(context, projectItem);
        return projectConsumerService.getProjectCommunityGroupByRole(context, communityOfProject, FUNDERS_ROLE);
    }

    @Override
    public void end(Context context) throws Exception {
        itemsAlreadyProcessed.clear();
    }

    @Override
    public void finish(Context context) throws Exception {}

}
