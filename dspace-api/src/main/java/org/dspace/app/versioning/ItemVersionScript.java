/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import static org.dspace.app.versioning.exception.ItemVersionScriptException.MISSING_ITEM_UUID;
import static org.dspace.app.versioning.exception.ItemVersionScriptException.WOFKFLOW_FOUND;
import static org.dspace.app.versioning.exception.ItemVersionScriptException.WORKSPACE_FOUND;

import java.sql.SQLException;
import java.util.Objects;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.versioning.exception.ItemVersionScriptException;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.util.UUIDUtils;
import org.dspace.utils.DSpace;
import org.dspace.versioning.Version;
import org.dspace.versioning.VersionHistory;
import org.dspace.versioning.factory.VersionServiceFactory;
import org.dspace.versioning.service.VersionHistoryService;
import org.dspace.versioning.service.VersioningService;
import org.dspace.workflow.WorkflowItem;
import org.dspace.workflow.WorkflowItemService;
import org.dspace.workflow.factory.WorkflowServiceFactory;

public class ItemVersionScript extends DSpaceRunnable<ItemVersionScriptConfiguration> {

    private static final VersioningService versioningService = VersionServiceFactory.getInstance().getVersionService();
    private static final VersionHistoryService versionHistoryService =
        VersionServiceFactory.getInstance().getVersionHistoryService();
    private static final WorkspaceItemService workspaceItemService =
        ContentServiceFactory.getInstance().getWorkspaceItemService();
    private static final WorkflowItemService workflowItemService =
        WorkflowServiceFactory.getInstance().getWorkflowItemService();
    private static final ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    protected static final EPersonService epersonService = EPersonServiceFactory.getInstance().getEPersonService();

    protected String itemId;
    protected Item item;
    protected String summary;
    private boolean isHelp;

    @Override
    public ItemVersionScriptConfiguration<?> getScriptConfiguration() {
        return new DSpace().getServiceManager()
            .getServiceByName("version", ItemVersionScriptConfiguration.class);
    }

    @Override
    public void setup() throws ParseException {
        isHelp = commandLine.hasOption("h");
        itemId = commandLine.getOptionValue("i");
        summary = commandLine.getOptionValue("s");
    }

    @Override
    public void internalRun() throws Exception {

        if (isHelp) {
            printHelp();
            return;
        }

        validate();

        Context context = new Context();
        context.turnOffAuthorisationSystem();

        try {
            process(context);
        } catch (Exception e) {
            context.restoreAuthSystemState();
            context.abort();
            throw e;
        } finally {
            context.restoreAuthSystemState();
            context.complete();
        }

    }

    protected void process(Context context) throws SQLException, ItemVersionScriptException {

        setEPerson(context);

        item = itemService.find(context, UUIDUtils.fromString(itemId));

        WorkflowItem workflowItem = null;
        WorkspaceItem workspaceItem = null;
        VersionHistory versionHistory = versionHistoryService.findByItem(context, item);
        if (Objects.nonNull(versionHistory)) {
            Version lastVersion = versionHistoryService.getLatestVersion(context, versionHistory);
            if (Objects.nonNull(lastVersion)) {
                workflowItem = workflowItemService.findByItem(context, lastVersion.getItem());
                workspaceItem = workspaceItemService.findByItem(context, lastVersion.getItem());
            }
        } else {
            workflowItem = workflowItemService.findByItem(context, item);
            workspaceItem = workspaceItemService.findByItem(context, item);
        }

        int errors = 0;
        if (Objects.nonNull(workflowItem)) {
            errors |= WOFKFLOW_FOUND;
        }
        if (Objects.nonNull(workspaceItem)) {
            errors |= WORKSPACE_FOUND;
        }

        if (errors > 0) {
            handler.logError("It is not possible to create a new version if the latest one in submisssion!");
            throw new ItemVersionScriptException(
                errors,
                "It is not possible to create a new version if the latest one in submisssion!"
            );
        }

        getScriptConfiguration().getActions()
            .stream()
            .flatMap(conf ->
                conf.createAction(context, item)
            )
            .forEach(action -> action.consume(context));

        Version version = StringUtils.isNotBlank(summary) ?
                          versioningService.createNewVersion(context, item, summary) :
                          versioningService.createNewVersion(context, item);
    }

    private void validate() throws ItemVersionScriptException {
        if (StringUtils.isEmpty(itemId) || StringUtils.isBlank(itemId)) {
            handler.logError("Item UUID not found or invalid!");
            throw new ItemVersionScriptException(
                MISSING_ITEM_UUID,
                "Item UUID not found or invalid!"
            );
        }
    }

    private void setEPerson(Context context) throws SQLException {
        EPerson myEPerson = epersonService.find(context, this.getEpersonIdentifier());

        // check eperson
        if (myEPerson == null) {
            handler.logError("EPerson cannot be found: " + this.getEpersonIdentifier());
            throw new UnsupportedOperationException("EPerson cannot be found: " + this.getEpersonIdentifier());
        }

        context.setCurrentUser(myEPerson);
    }

}
