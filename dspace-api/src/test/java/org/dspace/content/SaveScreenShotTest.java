/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.AbstractUnitTest;
import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenBuilder;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.app.capture.saveservice.CapturedStreamSaveService;
import org.dspace.app.versioning.action.capture.CaptureScreenAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class SaveScreenShotTest extends AbstractUnitTest {

    private static final Logger log = LogManager.getLogger();

    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    private BundleService bundleService = ContentServiceFactory.getInstance().getBundleService();
    private BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();
    private CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    private CollectionService collectionService = ContentServiceFactory.getInstance().getCollectionService();
    private InstallItemService installItemService = ContentServiceFactory.getInstance().getInstallItemService();
    private WorkspaceItemService workspaceItemService = ContentServiceFactory.getInstance().getWorkspaceItemService();
    private CapturedStreamSaveService capturedStreamSaveService = ContentServiceFactory.getInstance()
                                                                                       .getCapturedStreamSaveService();

    private String bundleName = "Bundle4Bitstream";
    private Item item;
    private Bundle bundle;
    private Community community;
    private Collection collection;

    @Before
    @Override
    public void init() {
        super.init();
        try {
            context.turnOffAuthorisationSystem();
            this.community = communityService.create(null, context);
            this.collection = collectionService.create(context, community);
            WorkspaceItem workspaceItem = workspaceItemService.create(context, collection, true);
            this.item = installItemService.installItem(context, workspaceItem);
            this.bundle = bundleService.create(context, item, bundleName);
            try (InputStream is = IOUtils.toInputStream("ThisIsSomeDummyText", Charset.defaultCharset())) {
                 bitstreamService.create(context, bundle, is);
            }
            context.restoreAuthSystemState();
        } catch (AuthorizeException ex) {
            log.error("Authorization Error in init", ex);
            fail("Authorization Error in init: " + ex.getMessage());
        } catch (SQLException ex) {
            log.error("SQL Error in init", ex);
            fail("SQL Error in init: " + ex.getMessage());
        } catch (IOException ex) {
            log.error("IO Error in init", ex);
            fail("IO Error in init: " + ex.getMessage());
        }
    }

    @After
    @Override
    public void destroy() {
        context.turnOffAuthorisationSystem();
        try {
            itemService.delete(context, item);
            collectionService.delete(context, collection);
            communityService.delete(context, community);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        context.restoreAuthSystemState();

        item = null;
        collection = null;
        community = null;
        try {
            super.destroy();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void deleteAllBitstreamBeforeAddNewOneTest() throws SQLException, AuthorizeException, IOException {
        assertEquals(1, item.getBundles(bundleName).size());
        assertEquals(1, item.getBundles(bundleName).get(0).getBitstreams().size());
        context.turnOffAuthorisationSystem();
        capturedStreamSaveService.deleteAllBitstreamFromTargetBundle(context, item, bundleName);
        context.restoreAuthSystemState();
        assertEquals(1, item.getBundles(bundleName).size());
        assertEquals(0, item.getBundles(bundleName).get(0).getBitstreams().size());
    }

    @Test
    @Ignore
    // once we have an Operation implementation, finish the test
    public void saveScreenIntoItemTest() throws SQLException, AuthorizeException, IOException {
        assertEquals(1, item.getBundles(bundleName).size());
        assertEquals(1, item.getBundles(bundleName).get(0).getBitstreams().size());
        context.turnOffAuthorisationSystem();
        CapturableScreenConfiguration configuration =
            new CapturableScreenConfiguration("test", null, null, "jpeg", "1", null);
        // here Operation implementation (T operation)
        CapturableScreen capturableScreen =
            CapturableScreenBuilder
                .createCapturableScreen(context, configuration)
                .build();
        CaptureScreenAction<CapturableScreen> action =
            new CaptureScreenAction<>(capturableScreen, item, bundleName, true);
        try (InputStream is = IOUtils.toInputStream("NewBitstreamText", Charset.defaultCharset())) {
            bitstreamService.create(context, bundle, is);
        capturedStreamSaveService.saveScreenIntoItem(context, is, null);
        }
        context.restoreAuthSystemState();
        assertEquals(1, item.getBundles(bundleName).size());
        assertEquals(0, item.getBundles(bundleName).get(0).getBitstreams().size());
    }

}