/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.actions.executor.model;

import java.util.List;

import org.dspace.app.versioning.action.VersioningAction;

/**
 *
 * Actions that we are going to execute
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ExecutableActions {

    private List<VersioningAction<?>> actions;
    private boolean isParallel;

    public ExecutableActions(List<VersioningAction<?>> actions) {
        this(actions, false);
    }

    public ExecutableActions(List<VersioningAction<?>> actions, boolean isParallel) {
        this.actions = actions;
        this.isParallel = isParallel;
    }

    public List<VersioningAction<?>> getActions() {
        return actions;
    }

    public void setActions(List<VersioningAction<?>> actions) {
        this.actions = actions;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean isParallel) {
        this.isParallel = isParallel;
    }

}
