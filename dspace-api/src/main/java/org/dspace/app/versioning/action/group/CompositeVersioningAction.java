/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.group;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.core.Context;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public class CompositeVersioningAction<T extends VersioningAction<?>>
    extends VersioningAction<T> {

    List<VersioningAction<?>> actions;

    public CompositeVersioningAction(T operation, List<VersioningAction<?>> actions) {
        super(operation);
        this.actions = actions;
    }

    @Override
    public void consumeAsync(Context context) {
        this.operation.consumeAsync(context);

        if (actions.size() > 0) {
            Executor executor =
                Executors.newFixedThreadPool(this.actions.size());

            List<CompletableFuture<?>> scheduledActions =
                this.actions
                    .stream()
                    .map(a ->
                        CompletableFuture
                            .runAsync(() -> a.consumeAsync(context), executor)
                    )
                    .collect(Collectors.toList());

            scheduledActions
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        }

    }

    @Override
    public void store(Context context) {
        this.operation.store(context);
        this.actions.forEach(action -> action.store(context));
    }

    @Override
    public void consume(Context c) {
        this.consumeAsync(c);
        this.store(c);
    }

}
