/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link Consumer}
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectConsumer implements Consumer {

    private ProjectConsumerService projectConsumerService;

    private Set<Item> itemsAlreadyProcessed = new HashSet<Item>();

    @Override
    public void initialize() throws Exception {
        projectConsumerService = new DSpace().getServiceManager().getServiceByName(
                                     ProjectConsumerServiceImpl.class.getName(),
                                     ProjectConsumerServiceImpl.class);
    }

    @Override
    public void consume(Context context, Event event) throws Exception {
        EPerson currentUser = context.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return;
        }
        Object dso = event.getSubject(context);
        if ((dso instanceof Item)) {
            Item item = (Item) dso;
            if (itemsAlreadyProcessed.contains(item)) {
                return;
            }
            this.projectConsumerService.processItem(context, currentUser, item);
            itemsAlreadyProcessed.add(item);
        }
    }

    @Override
    public void end(Context ctx) throws Exception {
        itemsAlreadyProcessed.clear();
    }

    @Override
    public void finish(Context ctx) throws Exception {}

}