/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.integration.crosswalks.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.crosswalk.StreamDisseminationCrosswalk;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.integration.crosswalks.FileNameDisseminator;
import org.dspace.content.integration.crosswalks.StreamDisseminationCrosswalkMapper;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.util.UUIDUtils;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link DSpaceRunnable} to export items in the given format.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public class ItemExport extends DSpaceRunnable<ItemExportScriptConfiguration<ItemExport>> {

    private ItemService itemService;

    protected Item item;

    private UUID itemUuid;

    private String fileName;

    private String exportFormat;

    private Context context;

    @Override
    public void setup() throws ParseException {

        this.itemService = ContentServiceFactory.getInstance().getItemService();

        this.itemUuid = UUIDUtils.fromString(commandLine.getOptionValue('i'));
        this.exportFormat = commandLine.getOptionValue('f');
        this.fileName = commandLine.getOptionValue('n');
    }

    @Override
    @SuppressWarnings("unchecked")
    public ItemExportScriptConfiguration<ItemExport> getScriptConfiguration() {
        return new DSpace().getServiceManager().getServiceByName("item-export", ItemExportScriptConfiguration.class);
    }

    @Override
    public void internalRun() throws Exception {

        initContext();

        validate();

        loadItem(context);

        run(context);

    }

    protected void run(Context context) throws Exception {
        StreamDisseminationCrosswalk streamDisseminationCrosswalk = getCrosswalkByType(exportFormat);
        if (streamDisseminationCrosswalk == null) {
            throw new IllegalArgumentException("No dissemination configured for format " + exportFormat);
        }

        boolean canDisseminate = streamDisseminationCrosswalk.canDisseminate(context, item);
        if (!canDisseminate) {
            throw new IllegalArgumentException("The item cannot be disseminated by the dissemination " + exportFormat);
        }

        try {
            performExport(item, streamDisseminationCrosswalk);
            context.complete();
        } catch (Exception e) {
            handler.handleException(e);
            context.abort();
        }
    }

    protected void loadItem(Context context) throws SQLException {
        item = itemService.find(context, itemUuid);
        if (item == null) {
            throw new IllegalArgumentException("No item found by id " + itemUuid);
        }
    }

    protected void validate() {
        if (exportFormat == null) {
            throw new IllegalArgumentException("The export format must be provided");
        }

        if (itemUuid == null) {
            throw new IllegalArgumentException("A valid item uuid should be provided");
        }
    }

    protected void initContext() throws SQLException {
        context = new Context();
        assignCurrentUserInContext();
        assignHandlerLocaleInContext();
        assignSpecialGroupsInContext();
    }

    private void performExport(Item item, StreamDisseminationCrosswalk streamDisseminationCrosswalk) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        streamDisseminationCrosswalk.disseminate(context, item, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        String name = getFileName(streamDisseminationCrosswalk);
        context.setMode(Context.Mode.READ_WRITE);
        handler.writeFilestream(context, name, in, streamDisseminationCrosswalk.getMIMEType(),
                streamDisseminationCrosswalk.isPubliclyReadable());
        handler.logInfo("Item exported successfully into file named " + name);
    }

    private String getFileName(StreamDisseminationCrosswalk streamDisseminationCrosswalk) {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName;
        }

        if (streamDisseminationCrosswalk instanceof FileNameDisseminator) {
            return ((FileNameDisseminator) streamDisseminationCrosswalk).getFileName();
        } else {
            return "export-result";
        }

    }

    private void assignCurrentUserInContext() throws SQLException {
        UUID uuid = getEpersonIdentifier();
        if (uuid != null) {
            EPerson ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
            context.setCurrentUser(ePerson);
        }
    }

    private void assignSpecialGroupsInContext() throws SQLException {
        for (UUID uuid : handler.getSpecialGroups()) {
            context.setSpecialGroup(uuid);
        }
    }

    private void assignHandlerLocaleInContext() {
        if (Objects.nonNull(this.handler) &&
            Objects.nonNull(this.context) &&
            Objects.nonNull(this.handler.getLocale()) &&
            !this.handler.getLocale().equals(this.context.getCurrentLocale())
        ) {
            this.context.setCurrentLocale(this.handler.getLocale());
        }
    }

    private StreamDisseminationCrosswalk getCrosswalkByType(String type) {
        return new DSpace().getSingletonService(StreamDisseminationCrosswalkMapper.class).getByType(type);
    }

}
