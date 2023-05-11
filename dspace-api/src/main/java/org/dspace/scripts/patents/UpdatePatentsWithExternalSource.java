/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultIterator;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.external.provider.impl.LiveImportDataProvider;
import org.dspace.kernel.ServiceManager;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.scripts.patents.service.UpdatePatentService;
import org.dspace.scripts.patents.service.UpdatePatentServiceImpl;
import org.dspace.utils.DSpace;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class UpdatePatentsWithExternalSource
        extends DSpaceRunnable<UpdatePatentsWithExternalSourceScriptConfiguration<UpdatePatentsWithExternalSource>> {

    private static final Logger log = LogManager.getLogger(UpdatePatentsWithExternalSource.class);

    private Context context;

    private UpdatePatentService updatePatentService;
    private LiveImportDataProvider liveImportDataProvider;

    @Override
    public void setup() throws ParseException {
        ServiceManager serviceManager = new DSpace().getServiceManager();
        liveImportDataProvider = serviceManager.getServiceByName("epoLiveImportDataProvider",
                LiveImportDataProvider.class);
        updatePatentService = serviceManager.getServiceByName(UpdatePatentServiceImpl.class.getName(),
                                                              UpdatePatentServiceImpl.class);
    }

    @Override
    public void internalRun() throws Exception {
        assignCurrentUserInContext();
        try {
            context.turnOffAuthorisationSystem();
            Iterator<Item> itemIterator = findPatents();
            handler.logInfo("Update start");

            int count = 0;
            int countFoundItems = 0;
            int countUpdatedItems = 0;

            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                countFoundItems++;
                count ++;
                updatePatentService.updatePatent(context, item, liveImportDataProvider);
            }
            context.complete();

            handler.logInfo("Found " + countFoundItems + " items");
            handler.logInfo("Updated " + countUpdatedItems + " Patents");
            handler.logInfo("Update end");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            handler.handleException(e);
            context.abort();
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private Iterator<Item> findPatents() {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.setDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addFilterQueries("search.entitytype:Patent");
        discoverQuery.addFilterQueries("-(relation.isVersionOf:*)");
        discoverQuery.setMaxResults(20);
        return new DiscoverResultIterator<Item, UUID>(context, discoverQuery);

    }

    private void assignCurrentUserInContext() throws SQLException {
        context = new Context();
        UUID uuid = getEpersonIdentifier();
        if (Objects.nonNull(uuid)) {
            EPerson ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
            context.setCurrentUser(ePerson);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public UpdatePatentsWithExternalSourceScriptConfiguration<UpdatePatentsWithExternalSource>
           getScriptConfiguration() {
        return new DSpace().getServiceManager().getServiceByName("update-patents",
                                                UpdatePatentsWithExternalSourceScriptConfiguration.class);
    }

}