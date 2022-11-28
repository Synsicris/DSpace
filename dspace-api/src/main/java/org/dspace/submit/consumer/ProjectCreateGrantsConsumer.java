/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.project.util.ProjectConstants;
import org.dspace.services.ConfigurationService;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * The purpose of this consumer is to check if the user
 * who created the workspaceitem belongs to a funding group,
 * if yes in the metadata 'cris.project.shared' it is copied by using the
 * grant value present in the belonging funding.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectCreateGrantsConsumer implements Consumer {

    private ConfigurationService configurationService;
    private ItemService itemService;
    private WorkspaceItemService workspaceItemService;
    private ProjectConsumerService projectConsumerService;

    private Set<Item> itemsAlreadyProcessed = new HashSet<Item>();

    @Override
    public void initialize() throws Exception {
        configurationService = new DSpace().getConfigurationService();
        itemService = ContentServiceFactory.getInstance().getItemService();
        workspaceItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
        projectConsumerService = new DSpace().getServiceManager().getServiceByName(
                                              ProjectConsumerServiceImpl.class.getName(),
                                              ProjectConsumerServiceImpl.class);
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        if (event.getEventType() == Event.CREATE) {
            EPerson currentUser = context.getCurrentUser();
            if (Objects.isNull(currentUser)) {
                return;
            }
            Object dso = event.getSubject(context);
            if (dso instanceof Item) {
                Item item = (Item) dso;
                String[] entitiesToSkip = configurationService.getArrayProperty("project.grants.entity-name.to-skip",
                        new String[] {});
                String[] entitiesToInclude = configurationService.getArrayProperty(
                        "project.grants.entity-name.to-include", new String[] {});
                String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
                if (Objects.isNull(entityType) || Arrays.stream(entitiesToSkip).anyMatch(entityType::equals)) {
                    return;
                }

                EPerson submitter = item.getSubmitter();
                if (Objects.isNull(submitter)) {
                    return;
                }
                if (itemsAlreadyProcessed.contains(item)) {
                    return;
                }

                String sharedValue = itemService.getMetadataFirstValue(item, "cris", "project", "shared", Item.ANY);
                if (StringUtils.equals(sharedValue, ProjectConstants.SHARED) ||
                    StringUtils.equals(sharedValue, ProjectConstants.FUNDER) ||
                    StringUtils.equals(sharedValue, ProjectConstants.FUNDER_PROGRAMME)) {
                    return;
                }
                if (StringUtils.isNotBlank(sharedValue) &&
                    (Objects.nonNull(workspaceItemService.findByItem(context, item)) ||
                     Arrays.stream(entitiesToInclude).anyMatch(entityType::equals))) {
                    projectConsumerService.checkGrants(context, submitter, item);
                }
                itemsAlreadyProcessed.add(item);
            }
        }
    }

    @Override
    public void end(Context context) throws Exception {
        itemsAlreadyProcessed.clear();
    }

    @Override
    public void finish(Context context) throws Exception {}

}