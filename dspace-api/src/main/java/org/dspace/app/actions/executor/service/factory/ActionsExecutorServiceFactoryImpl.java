/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.actions.executor.service.factory;

import org.dspace.app.actions.executor.service.ActionsExecutorService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Factory used to retrieve all the {@link ActionsExecutorService} services
 * and their utility services
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ActionsExecutorServiceFactoryImpl extends ActionsExecutorServiceFactory {

    @Autowired(required = true)
    private ActionsExecutorService actionsExecutorService;

    @Override
    public ActionsExecutorService getActionsExecutorService() {
        return this.actionsExecutorService;
    }

}
