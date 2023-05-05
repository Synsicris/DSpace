/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.saveservice;


import static org.dspace.app.capture.CaptureWebsiteProperties.DEFAULT_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.versioning.action.capture.CaptureScreenAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.BitstreamFormat;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class CapturedStreamSaveServiceImpl implements CapturedStreamSaveService {

    private static final Logger log = LogManager.getLogger(CapturedStreamSaveServiceImpl.class);
    private static final char[][] CHARS = new char[][] {{'a', 'z'},{'0','9'}};
    private static final RandomStringGenerator GENERATOR =
        new RandomStringGenerator.Builder().withinRange(CHARS).build();

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
            for (Bitstream bitstream : bundle.getBitstreams()) {
                bitstreamService.delete(context, bitstream);
            }
        }
    }

    @Override
    public void saveScreenIntoItem(Context context, InputStream is, CaptureScreenAction<?> captureScreenAction)
            throws IOException, SQLException, AuthorizeException {
        Bundle bundle = getBundle(context, captureScreenAction);
        Bitstream bitstream = bitstreamService.create(context, bundle, is);
        BitstreamFormat bitstreamFormat = getBitstreamFormat(context, captureScreenAction);
        bitstream.setFormat(context, bitstreamFormat);
        bitstream.setName(context,computeBitstreamName(captureScreenAction));
        MetadataValueDTO metadataValue = captureScreenAction.getMetadataValue();
        if (metadataValue != null) {
            this.bitstreamService.addMetadata(
                context, bitstream, metadataValue.getSchema(),
                metadataValue.getElement(), metadataValue.getQualifier(), metadataValue.getLanguage(),
                metadataValue.getValue(), metadataValue.getAuthority(), metadataValue.getConfidence()
            );
        }
        bitstreamService.update(context, bitstream);
    }

    public String computeBitstreamName(CaptureScreenAction<?> captureScreenAction) {
        return new StringBuilder(
                captureScreenAction.getOperation().getUrl()
            )
            .append("_")
            .append(
                LocalDate.now()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            )
            .append("_")
            .append(GENERATOR.generate(5))
            .toString();
    }

    private Bundle getBundle(Context context, CaptureScreenAction<?> captureScreenAction)
             throws SQLException, AuthorizeException, IOException {

        Item targetItem = captureScreenAction.getItem();
        String bundleName = captureScreenAction.getBundleName();
        List<Bundle> bundles = targetItem.getBundles(bundleName);
        if (captureScreenAction.isCleanBundle() && CollectionUtils.isNotEmpty(bundles)) {
            deleteAllBitstreamFromTargetBundle(context, targetItem, bundleName);
        } else if (CollectionUtils.isEmpty(bundles)) {
            return bundelService.create(context, targetItem, bundleName);
        }
        return bundles.get(0);
    }

    private BitstreamFormat getBitstreamFormat(Context context, CaptureScreenAction<?> captureScreenAction)
             throws SQLException {
        BitstreamFormat bitstreamFormat = null;
        String extension = captureScreenAction.getOperation().getConfiguration().getType();
        Map<String, String> extensionsToMimeType = captureWebsiteService.getExtensionsToMimeType();
        if (extensionsToMimeType.containsKey(extension)) {
            bitstreamFormat = bitstreamFormatService.findByMIMEType(context, extensionsToMimeType.get(extension));
        }
        if (bitstreamFormat == null) {
            log.warn(
                "Format" + extension + ") is not supported." +
                "The screenshots of url " + captureScreenAction.getOperation().getUrl() +
                " has been stored in: " + captureScreenAction.getBundleName() + " of item with uuid: " +
                captureScreenAction.getItem().getID() + " with the default format (" + DEFAULT_TYPE + ")"
            );
            bitstreamFormat = bitstreamFormatService.findByMIMEType(context, extensionsToMimeType.get(DEFAULT_TYPE));
        }
        return bitstreamFormat;
    }

}
