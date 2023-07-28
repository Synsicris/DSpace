/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.script.synsicris;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.dspace.app.versioning.action.VersioningActionConfiguration;
import org.dspace.content.integration.crosswalks.script.ItemExportScriptConfiguration;

public class ItemExportSynsicrisScriptConfiguration<T extends ItemExportSynsicris>
    extends ItemExportScriptConfiguration<T> {

    // list of actions to accomplish
    private List<VersioningActionConfiguration<?,?>> actions;
    private boolean isParallel = false;

    @Override
    public Options getOptions() {
        Options options = super.getOptions();

        options.addOption(
            Option.builder("s").longOpt("screenshot")
                .desc("export with updated screenshots of configured entities")
                .hasArg()
                .build()
        );

        super.options = options;

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
