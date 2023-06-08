/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer;
import static org.dspace.project.util.ProjectConstants.MD_POLICY_SHARED;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.exception.SQLRuntimeException;
import org.dspace.eperson.EPerson;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link Consumer}
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectEditGrantsConsumer implements Consumer {

    private ProjectConsumerService projectConsumerService;
    private ItemService itemService;

    private Set<UUID> itemsAlreadyProcessed = new HashSet<UUID>();

    @Override
    public void initialize() throws Exception {
        projectConsumerService = new DSpace().getServiceManager().getServiceByName(
                                     ProjectConsumerServiceImpl.class.getName(),
                                     ProjectConsumerServiceImpl.class);
        itemService = ContentServiceFactory.getInstance().getItemService();
    }

    @Override
    public void consume(Context context, Event event) throws Exception {

        String policyMetadata = ProjectConstants.MD_POLICY_SHARED.toString().replaceAll("\\.", "_");

        if (event.getEventType() == Event.MODIFY_METADATA && event.getDetail() != null &&
            event.getDetail().contains(policyMetadata)) {
            EPerson currentUser = context.getCurrentUser();
            if (Objects.isNull(currentUser)) {
                return;
            }
            Object dso = event.getSubject(context);
            if (dso instanceof Item) {
                Item item = (Item) dso;
                if (itemsAlreadyProcessed.contains(item.getID())) {
                    return;
                }
                EPerson submitter = item.getSubmitter();
                if (Objects.isNull(submitter)) {
                    return;
                }
                this.projectConsumerService.processItem(context, submitter, item);
                itemsAlreadyProcessed.add(item.getID());
                processRelatedEntities(context, submitter, item);
            }
        }
    }

    protected void processRelatedEntities(Context context, EPerson submitter, Item item) {
        if (!this.projectConsumerService.isFundingItem(item)) {
            return;
        }
        String fundingPolicy = this.projectConsumerService.getSharedPolicyValue(item);
        Community fundingCommunity;
        try {
            fundingCommunity = this.projectConsumerService.getFirstOwningCommunity(context, item);
        } catch (SQLException e) {
            throw new SQLRuntimeException(
                "Cannot find the funding community of funding: " + item.getID(),
                e
            );
        }
        Iterator<Item> relatedItems =
            this.projectConsumerService.findRelatedFundingItems(context, fundingCommunity, item);
        Item toProcess = null;
        String policyValue =
            this.projectConsumerService.getSharedPolicyValue(item);
        while (
                relatedItems.hasNext() &&
                (toProcess = relatedItems.next()) != null &&
                !itemsAlreadyProcessed.contains(toProcess.getID())
        ) {
            try {
                processRelatedItem(context, submitter, toProcess, fundingPolicy, policyValue);
            } catch (SQLException | AuthorizeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void processRelatedItem(
        Context context, EPerson submitter, Item relatedItem, String fundingPolicy, String policyValue
    ) throws SQLException, AuthorizeException {
        this.projectConsumerService.setPolicy(context, submitter, relatedItem, fundingPolicy);
        itemService.replaceMetadata(
            context, relatedItem,
            MD_POLICY_SHARED.schema,
            MD_POLICY_SHARED.element,
            MD_POLICY_SHARED.qualifier,
            null, policyValue, null,
            Choices.CF_UNSET,
            0
        );
        this.itemService.update(context, relatedItem);
        itemsAlreadyProcessed.add(relatedItem.getID());
    }

    @Override
    public void end(Context ctx) throws Exception {
        itemsAlreadyProcessed.clear();
    }

    @Override
    public void finish(Context ctx) throws Exception {}

}