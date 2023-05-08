/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.group;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.app.versioning.action.VersioningActionConfiguration;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Configuration that generates {@link CompositeVersioningAction}
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <C>
 * @param <T>
 */
public class CompositeVersioningActionConfiguration<
        C extends VersioningActionConfiguration<?, T>,
        T extends VersioningAction<?>
    > extends VersioningActionConfiguration<C, CompositeVersioningAction<T>> {

    private final List<VersioningActionConfiguration<?, ?>> configurations;
    private final boolean isParallel;

    public CompositeVersioningActionConfiguration(
        C configuration,
        List<VersioningActionConfiguration<?, ?>> configurations,
        boolean isParallel
    ) {
        super(configuration);
        this.configurations = configurations;
        this.isParallel = isParallel;
    }

    public CompositeVersioningActionConfiguration(
        C configuration,
        List<VersioningActionConfiguration<?, ?>> configurations
    ) {
        this(configuration, configurations, false);
    }

    @Override
    public Stream<CompositeVersioningAction<T>> createAction(Context c, Item i) {
        return Stream.of(
            new CompositeVersioningAction<>(
                configuration.createAction(c, i).findFirst().orElse(null),
                this.configurations
                    .stream()
                    .flatMap(conf -> conf.createAction(c, i))
                    .collect(Collectors.toList()),
                isParallel
            )
        );
    }

}
