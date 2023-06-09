/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.itemexport.synsicris;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.ParseException;
import org.dspace.app.actions.executor.model.ExecutableActions;
import org.dspace.app.actions.executor.service.ActionsExecutorService;
import org.dspace.app.actions.executor.service.factory.ActionsExecutorServiceFactory;
import org.dspace.app.itemexport.ItemExport;
import org.dspace.app.versioning.action.VersioningAction;
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
                .getServiceByName("export-synsicris", ItemExportSynsicrisScriptConfiguration.class);
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
    public void internalRun() throws Exception {

        if (this.takeScreenshot) {

        }

        super.internalRun();
    }

    protected void handleVersioningActions(Context context) {
        actionsExecutor.execute(
            context,
            new ExecutableActions(
                this.getConfiguredActions(context),
                getScriptConfiguration().isParallel()
            )
        );
    }

    protected List<VersioningAction<?>> getConfiguredActions(Context context) {
        return getScriptConfiguration()
            .getActions()
            .stream()
            .flatMap(conf -> conf.createAction(context, item))
            .collect(Collectors.toList());
    }

}
