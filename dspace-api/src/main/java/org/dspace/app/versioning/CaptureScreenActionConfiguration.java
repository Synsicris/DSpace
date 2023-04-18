/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.util.List;
import java.util.stream.Collectors;

import org.dspace.app.capture.CapturableScreen;
import org.dspace.app.capture.CapturableScreenConfiguration;
import org.dspace.content.Item;
import org.dspace.core.Context;

public class CaptureScreenActionConfiguration
    extends VersioningActionConfiguration<CapturableScreenConfiguration, CaptureScreenAction<?>> {

    protected String bundleName;
    protected CapturableScreenRelationMapper mapper;
    protected ItemMapper itemMapper;

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public CapturableScreenMapper getMapper() {
        return mapper;
    }

    public void setMapper(CapturableScreenRelationMapper mapper) {
        this.mapper = mapper;
    }

    public ItemMapper getItemMapper() {
        return itemMapper;
    }

    public void setItemMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public List<CaptureScreenAction<?>> createAction(Context c, Item i) {
        // 1. item to funding / project -> 1 to n (item where to store bundle)
        // 2. item to relation of funding or project -> 1 to n (screen to acquire)
        // 3. item to capturable-screen (store screen into item)
        return this.itemMapper.map(c, i)
            .stream()
            .flatMap(item ->
                this.mapper.mapToCapturableScreen(c, configuration, item, null, null)
                    .map(screen -> new CaptureScreenAction<CapturableScreen>(screen, item, bundleName))
            )
            .collect(Collectors.toList());
    }

}
