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

import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
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
    private ProjectConsumerService projectConsumerService;

    private Set<Item> itemsAlreadyProcessed = new HashSet<Item>();

    @Override
    public void initialize() throws Exception {
        configurationService = new DSpace().getConfigurationService();
        itemService = ContentServiceFactory.getInstance().getItemService();
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
                String[] entitiesToInclude = configurationService.getArrayProperty(
                        "project.grants.entity-name.to-process", new String[] {});
                String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
                if (Objects.isNull(entityType) || Arrays.stream(entitiesToInclude).noneMatch(entityType::equals)) {
                    return;
                }

                projectConsumerService.setGrantsByFundingPolicy(context, item);

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