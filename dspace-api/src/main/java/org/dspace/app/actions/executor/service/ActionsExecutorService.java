/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.actions.executor.service;

import org.dspace.app.actions.executor.model.ExecutableActions;
import org.dspace.core.Context;

/**
 * Interface of an executor that executes given actions
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public interface ActionsExecutorService {

    /**
     * Executes some configured actions inside the {@link ExecutableActions}
     *
     * @param context
     * @param executable
     */
    void execute(Context context, ExecutableActions executable);

}