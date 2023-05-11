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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.core.Context;

/**
 * Composite and hierarchical versioning actions.
 * The `operation` field encapsulates the root mandatory action to execute, while the `actions` field
 * are just its sub-actions children that will be executed after its completion.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public class CompositeVersioningAction<T extends VersioningAction<?>>
    extends VersioningAction<T> {

    protected List<VersioningAction<?>> actions;
    protected boolean isParallel;

    public CompositeVersioningAction(T operation, List<VersioningAction<?>> actions, boolean isParallel) {
        super(operation);
        this.actions = actions;
        this.isParallel = isParallel;
    }

    public CompositeVersioningAction(T operation, List<VersioningAction<?>> actions) {
        this(operation, actions, false);
    }

    @Override
    public void consumeAsync(Context context) {
        this.operation.consumeAsync(context);

        if (actions.size() > 0) {
            if (isParallel) {
                int threadSize = actions.size();
                ExecutorService executor =
                    Executors.newFixedThreadPool(threadSize);

                try {
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
                        .forEach(CompletableFuture::join);

                } finally {
                    if (!executor.isShutdown()) {
                        executor.shutdown();
                    }
                }
            } else {
                this.actions
                    .stream()
                    .forEach(a -> a.consumeAsync(context));
            }
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
