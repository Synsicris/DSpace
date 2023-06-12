/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.script.synsicris;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.ParseException;
import org.dspace.app.actions.executor.model.ExecutableActions;
import org.dspace.app.actions.executor.service.ActionsExecutorService;
import org.dspace.app.actions.executor.service.factory.ActionsExecutorServiceFactory;
import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.app.versioning.action.VersioningActionConfiguration;
import org.dspace.content.integration.crosswalks.script.ItemExport;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;

public class ItemExportSynsicris extends ItemExport {

    protected static final ActionsExecutorService actionsExecutor =
        ActionsExecutorServiceFactory.getInstance().getActionsExecutorService();

    private boolean takeScreenshot = false;
    private String takeScreenshotString;

    @Override
    public ItemExportSynsicrisScriptConfiguration getScriptConfiguration() {
        return new DSpace().getServiceManager()
                .getServiceByName("item-export-synsicris", ItemExportSynsicrisScriptConfiguration.class);
    }

    @Override
    public void setup() throws ParseException {
        if (this.commandLine.hasOption("s")) {
            this.takeScreenshotString = this.commandLine.getOptionValue("s");
            this.takeScreenshot =
                "TRUE".equalsIgnoreCase(takeScreenshotString) ||
                "yes".equalsIgnoreCase(takeScreenshotString) ||
                "y".equalsIgnoreCase(takeScreenshotString);
        }
        super.setup();
    }

    @Override
    protected void run(Context context) throws Exception {

        if (this.takeScreenshot) {
            this.runActions(context);
        }

        super.run(context);
    }

    protected void runActions(Context context) {
        actionsExecutor.execute(
            context,
            new ExecutableActions(
                this.getConfiguredActions(context),
                getScriptConfiguration().isParallel()
            )
        );
    }

    protected List<VersioningAction<?>> getConfiguredActions(Context context) {
        List<VersioningActionConfiguration<?,?>> actions = getScriptConfiguration().getActions();
        return actions.stream()
            .flatMap(conf -> conf.createAction(context, item))
            .collect(Collectors.toList());
    }

}
