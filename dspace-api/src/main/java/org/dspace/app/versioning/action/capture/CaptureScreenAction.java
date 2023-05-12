/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.saveservice.CapturedStreamSaveService;
import org.dspace.app.capture.service.CaptureWebsiteService;
import org.dspace.app.capture.service.CapturedScreenConsumer;
import org.dspace.app.capture.service.factory.CaptureWebsiteServiceFactory;
import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.app.versioning.action.clear.bundle.ClearBundleAction;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VersioningAction used to capture a screen
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public class CaptureScreenAction<T extends CapturableScreen> extends VersioningAction<T> {

    private static final Logger logger = LoggerFactory.getLogger(CaptureScreenAction.class);

    private final CaptureWebsiteService captureWebsiteService =
        CaptureWebsiteServiceFactory.getInstance().getCaptureWebsiteService();
    private final CapturedStreamSaveService capturedStreamSaveService =
        CaptureWebsiteServiceFactory.getInstance().getCapturedStreamSaveService();
    private final Optional<ClearBundleAction> clearBundleAction;

    private final Item item;
    private final String bundleName;
    private final MetadataValueDTO metadataValue;
    private InputStream capturedScreen;

    public CaptureScreenAction(T operation, Item item, String bundleName) {
        this(operation, item, bundleName, false);
    }

    public CaptureScreenAction(T operation, Item item, String bundleName, boolean cleanBundle) {
        this(operation, item, bundleName, cleanBundle, null);
    }

    public CaptureScreenAction(
        T operation, Item item, String bundleName, boolean cleanBundle, MetadataValueDTO metadataValue
    ) {
        super(operation);
        this.item = item;
        this.bundleName = bundleName;
        if (cleanBundle) {
            this.clearBundleAction =
                Optional.of(new ClearBundleAction(bundleName, item));
        } else {
            this.clearBundleAction =
                Optional.empty();
        }
        this.metadataValue = metadataValue;
    }

    @Override
    public void consumeAsync(Context context) {
        try {
            CapturedScreenConsumer capturedScreenConsumer =
                new CapturedScreenConsumer(
                    context,
                    this,
                    capturableScreenProvider(),
                    initializeProcess()
                );
            this.capturedScreen = capturedScreenConsumer.get();
        } catch (Exception e) {
            this.capturedScreen = null;
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retryAsync(Context context) {
        increaseRetry();
        this.consumeAsync(context);
    }

    protected void increaseRetry() {
        this.operation.getConfiguration().setTimeout(
            Integer.toString(
                Integer.valueOf(this.operation.getConfiguration().getTimeout()) * 2
            )
        );
    }

    @Override
    public void store(Context context) {
        this.clearBundleAction.ifPresent(action -> action.consume(context));
        try {
            this.capturedStreamSaveService
                    .saveScreenIntoItem(context, this.capturedScreen, this);
        } catch (IOException | SQLException | AuthorizeException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            this.capturedScreen = null;
        }
    }

    @Override
    public void consume(Context context) {
        try {
            this.consumeAsync(context);
            this.store(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retry(Context context) {
        increaseRetry();
        this.consume(context);
    }

    protected Supplier<CapturableScreen> capturableScreenProvider() {
        return () -> getCapturableScreen();
    }

    protected BiFunction<Context, CapturableScreen, Process> initializeProcess() {
        return (c, screen) -> {
            try {
                return this.captureWebsiteService.getScreenshotProcess(c, screen);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        };
    }

    private T getCapturableScreen() {
        return this.operation;
    }

    public Item getItem() {
        return item;
    }

    public String getBundleName() {
        return bundleName;
    }

    public boolean isCleanBundle() {
        return clearBundleAction.isPresent();
    }

    public MetadataValueDTO getMetadataValue() {
        return metadataValue;
    }

}
