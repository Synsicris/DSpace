/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.dspace.app.capture.model.CapturableScreen;
import org.dspace.app.capture.model.CapturableScreenBuilder;
import org.dspace.app.capture.model.CapturableScreenConfiguration;
import org.dspace.app.versioning.action.VersioningActionConfiguration;
import org.dspace.app.versioning.action.capture.mapper.ItemUrlMapper;
import org.dspace.app.versioning.action.capture.mapper.MetadataValueDTOSupplier;
import org.dspace.app.versioning.action.capture.mapper.MetadataValueDTOSuppliers;
import org.dspace.app.versioning.action.capture.mapper.ScreenActionItemMapper;
import org.dspace.app.versioning.model.ItemProvider;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Configuration of an action that takes a screenshot.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CaptureScreenActionConfiguration
    extends VersioningActionConfiguration<CapturableScreenConfiguration, CaptureScreenAction<?>> {

    private static final ScreenActionItemMapper ITEM_MAPPER = (c, item, providedItem) -> item;
    private static final Function<String, MetadataValueDTOSupplier> dcTypeSupplier =
        (dcType) -> MetadataValueDTOSuppliers.getDcTypeSupplier(dcType);
    private static final MetadataValueDTOSupplier emptySupplier = (c, i) -> null;

    protected final String bundleName;
    protected final ItemProvider itemProvider;
    protected final ScreenActionItemMapper itemMapper;
    protected final ItemUrlMapper itemUrlMapper;
    protected final boolean cleanBundle;
    protected final MetadataValueDTOSupplier metadataValueSupplier;

    public CaptureScreenActionConfiguration(
        CapturableScreenConfiguration configuration, String bundleName,
        ItemProvider itemProvider, ItemUrlMapper itemUrlMapper
    ) {
        this(configuration, bundleName, itemProvider, itemUrlMapper, ITEM_MAPPER);
    }

    public CaptureScreenActionConfiguration(
        CapturableScreenConfiguration configuration, String bundleName,
        ItemProvider itemProvider, ItemUrlMapper itemUrlMapper, String dcType
    ) {
        this(configuration, bundleName, itemProvider, itemUrlMapper, ITEM_MAPPER, false, dcType);
    }

    public CaptureScreenActionConfiguration(
        CapturableScreenConfiguration configuration,
        String bundleName, ItemProvider itemProvider,
        ItemUrlMapper itemUrlMapper, ScreenActionItemMapper itemMapper
    ) {
        this(configuration, bundleName, itemProvider, itemUrlMapper, itemMapper, false);
    }

    public CaptureScreenActionConfiguration(
        CapturableScreenConfiguration configuration,
        String bundleName, ItemProvider itemProvider,
        ItemUrlMapper itemUrlMapper, ScreenActionItemMapper itemMapper,
        boolean cleanBundle
    ) {
        this(configuration, bundleName, itemProvider, itemUrlMapper, itemMapper, cleanBundle, null);
    }

    public CaptureScreenActionConfiguration(
        CapturableScreenConfiguration configuration,
        String bundleName, ItemProvider itemProvider,
        ItemUrlMapper itemUrlMapper, ScreenActionItemMapper itemMapper,
        boolean cleanBundle, String dcType
    ) {
        super(configuration);
        this.bundleName = bundleName;
        this.itemProvider = itemProvider;
        this.itemMapper = itemMapper;
        this.itemUrlMapper = itemUrlMapper;
        this.cleanBundle = cleanBundle;
        if (dcType != null) {
            this.metadataValueSupplier = dcTypeSupplier.apply(dcType);
        } else {
            this.metadataValueSupplier = emptySupplier;
        }
    }

    @Override
    public Stream<CaptureScreenAction<?>> createAction(Context c, Item item) {
        // provide items (somehow related to the item)
        return this.itemProvider.retrieve(c, item)
            .filter(Objects::nonNull)
            .map(providedItem -> mapAction(c, item, providedItem));
    }

    public CaptureScreenAction<CapturableScreen> mapAction(Context c, Item item, Item providedItem) {
        return new CaptureScreenAction<>(
            getCapturableScreen(c, providedItem),
            itemMapper.mapScreenActionItem(c, item, providedItem),
            bundleName,
            cleanBundle,
            this.metadataValueSupplier.get(c, item)
        );
    }

    public CapturableScreen getCapturableScreen(Context c, Item providedItem) {
        return CapturableScreenBuilder.createCapturableScreen(c, configuration)
                .withUrl(this.itemUrlMapper.mapToUrl(c, providedItem))
                .computeHeaders()
                .build();
    }

}
