/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.saveservice;

import static org.dspace.app.capture.service.CaptureWebsiteService.DEFAULT_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.versioning.CaptureScreenAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.BitstreamFormat;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class CapturedStreamSaveServiceImpl implements CapturedStreamSaveService {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private ItemService itemService;
    @Autowired
    private BundleService bundelService;
    @Autowired
    private BitstreamService bitstreamService;
    @Autowired
    private CaptureWebsiteService captureWebsiteService;
    @Autowired
    private BitstreamFormatService bitstreamFormatService;

    @Override
    public void deleteAllBitstreamFromTargetBundle(Context context, Item targetItem, String bundleName)
            throws SQLException, AuthorizeException, IOException {

        for (Bundle bundle : targetItem.getBundles(bundleName)) {
            bundelService.delete(context, bundle);
        }
        Bundle newBundle = bundelService.create(context, targetItem, bundleName);
        itemService.addBundle(context, targetItem, newBundle);
    }

    @Override
    public void saveScreenIntoItem(Context context, InputStream is, CaptureScreenAction<?> captureScreenAction)
            throws IOException, SQLException, AuthorizeException {

        Bundle bundle = getBundle(context, captureScreenAction);
        Bitstream bitstream = bitstreamService.create(context, bundle, is);
        BitstreamFormat bitstreamFormat = getBitstreamFormat(context, captureScreenAction);
        bitstream.setFormat(context, bitstreamFormat);
        bitstreamService.update(context, bitstream);
    }

    private Bundle getBundle(Context context, CaptureScreenAction<?> captureScreenAction)
             throws SQLException, AuthorizeException, IOException {

        Bundle targetBundle = null;
        Item targetItem = captureScreenAction.getItem();
        String bundleName = captureScreenAction.getBundleName();
        List<Bundle> bundles = targetItem.getBundles(bundleName);
        if (captureScreenAction.cleanBundleBeforeAddNewBistream()) {
            for (Bundle bundle : bundles) {
                bundelService.delete(context, bundle);
            }
            targetBundle = bundelService.create(context, targetItem, bundleName);
            itemService.addBundle(context, targetItem, targetBundle);
        } else {
            targetBundle =  bundles.get(0);
        }
        return targetBundle;
    }

    private BitstreamFormat getBitstreamFormat(Context context, CaptureScreenAction<?> captureScreenAction)
             throws SQLException {
        BitstreamFormat bitstreamFormat = null;
        String extension = captureScreenAction.getOperation().getConfiguration().getType();
        Map<String, String> extensionsToMimeType = captureWebsiteService.getExtensionsToMimeType();
        if (extensionsToMimeType.containsKey(extension)) {
            bitstreamFormat = bitstreamFormatService.findByMIMEType(context, extensionsToMimeType.get(extension));
        }
        if (Objects.isNull(bitstreamFormat)) {
            log.warn("For the screenshots of project with uuid: " + captureScreenAction.getItem().getID() +
                    ". The default format (" + DEFAULT_TYPE + ") was used because the configured format ("
                    + extension + ") is not supported.");
            bitstreamFormat = bitstreamFormatService.findByMIMEType(context, extensionsToMimeType.get(DEFAULT_TYPE));
        }
        return bitstreamFormat;
    }

}
