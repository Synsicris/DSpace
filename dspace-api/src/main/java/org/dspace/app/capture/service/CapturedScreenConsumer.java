/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.versioning.action.capture.CaptureScreenAction;
import org.dspace.core.Context;
import org.dspace.storage.bitstore.DeleteOnCloseFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapturedScreenConsumer implements Supplier<InputStream> {

    private static final Logger logger = LoggerFactory.getLogger(CapturedScreenConsumer.class);

    public final Context context;
    public final CaptureScreenAction<?> action;
    public final Supplier<CapturableScreen> screenSupplier;
    public final BiFunction<Context, CapturableScreen, Process> processInitializer;

    public CapturedScreenConsumer(
        Context context,
        CaptureScreenAction<?> action,
        Supplier<CapturableScreen> screenSupplier,
        BiFunction<Context, CapturableScreen, Process> processInitializer
    ) {
        super();
        this.context = context;
        this.action = action;
        this.screenSupplier = screenSupplier;
        this.processInitializer = processInitializer;
    }

    @Override
    public InputStream get() {
        Process process =
            this.processInitializer.apply(context, screenSupplier.get());
        try {
            File tempFile = getScreenshotTempFile(process);
            ByteArrayOutputStream result = getErrorOutput(process);
            process.waitFor();
            if (process.exitValue() == 0) {
                return new DeleteOnCloseFileInputStream(tempFile);
            } else {
                throw new RuntimeException(
                    "Unable to take the screenshot! \n Exit Code: " + process.exitValue() +
                    "\n Error message: " + result.toString(StandardCharsets.UTF_8.name())
                );
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Error while retrieving the screenshot.", e);
            throw new RuntimeException("Error while retrieving the screenshot.", e);
        }
    }


    public File getScreenshotTempFile(Process process) throws IOException {
        File tempFile = File.createTempFile("screen-capture", "temp");
        tempFile.deleteOnExit();
        try (FileOutputStream output = new FileOutputStream(tempFile)) {
            InputStream stdin = process.getInputStream();
            IOUtils.copy(stdin, output);
        } catch (Exception e) {
            tempFile.delete();
            logger.error("Error while retrieving the screenshot.", e);
            throw new RuntimeException("Error while retrieving the screenshot.", e);
        }
        return tempFile;
    }


    public ByteArrayOutputStream getErrorOutput(Process process) throws IOException {
        InputStream sterr = process.getErrorStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = sterr.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result;
    }

}
