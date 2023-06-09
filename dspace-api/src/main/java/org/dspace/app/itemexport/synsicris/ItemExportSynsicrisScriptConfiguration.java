/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.itemexport.synsicris;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.dspace.app.itemexport.ItemExportScriptConfiguration;
import org.dspace.app.versioning.action.VersioningActionConfiguration;

public class ItemExportSynsicrisScriptConfiguration<T extends ItemExportSynsicris>
    extends ItemExportScriptConfiguration<T> {

    // list of actions to accomplish
    private List<VersioningActionConfiguration<?,?>> actions;
    private boolean isParallel = false;

    @Override
    public Options getOptions() {
        Options options = super.getOptions();

        options.addOption(Option.builder("s").longOpt("screenshot")
                .desc("take the screenshot of configured entities")
                .hasArg().build());

        return options;
    }

    public List<VersioningActionConfiguration<?,?>> getActions() {
        return actions;
    }

    public void setActions(List<VersioningActionConfiguration<?, ?>> actions) {
        this.actions = actions;
    }

    public boolean isParallel() {
        return isParallel;
    }

    public void setParallel(boolean isParallel) {
        this.isParallel = isParallel;
    }

}
