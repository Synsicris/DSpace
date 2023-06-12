/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.actions.executor.service.factory;

import org.dspace.app.actions.executor.service.ActionsExecutorService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * Factory of {@link ActionsExecutorService}s
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public abstract class ActionsExecutorServiceFactory {

    public static ActionsExecutorServiceFactory getInstance() {
        return DSpaceServicesFactory.getInstance().getServiceManager()
                .getServiceByName("actionsExecutoFactory", ActionsExecutorServiceFactory.class);
    }

    public abstract ActionsExecutorService getActionsExecutorService();

}
