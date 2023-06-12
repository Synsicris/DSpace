/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.actions.executor.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.dspace.app.actions.executor.model.ExecutableActions;
import org.dspace.app.versioning.action.VersioningAction;
import org.dspace.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Default implementation of the actions' executor {@link ActionsExecutorService}
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ActionsExecutorServiceImpl implements ActionsExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(ActionsExecutorServiceImpl.class);

    @Override
    public void execute(Context context, ExecutableActions executable) {
        if (executable.isParallel()) {
            ExecutorService executorService = getExecutorService(executable);
            try {
                executeParallelActions(context, executorService, executable);
            } finally {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }
        } else {
            executeActions(context, executable);
        }
    }

    protected void executeParallelActions(
        Context context, ExecutorService executorService, ExecutableActions executable
    ) {
        List<CompletableFuture<VersioningAction<?>>> scheduledActions =
            getActionStream(executable)
                .map(action -> asyncActionMapper(context, action, executorService))
                .collect(Collectors.toList());

        scheduledActions
            .stream()
            .map(CompletableFuture::join)
            .forEach(action -> action.store(context));
    }

    @SuppressWarnings("CheckReturnValue")
    protected void executeActions(Context context, ExecutableActions executable) {
        getActionStream(executable)
            .forEach(action ->
                Optional.of(consumeActionWithRetry(context, action, action.getMaxRetries()))
                    .filter(Boolean::booleanValue)
                    .orElseThrow(
                        () -> new RuntimeException("Cannot take the screenshot of the action: " + action)
                    )
            );
    }

    private Boolean consumeActionWithRetry(Context context, VersioningAction<?> action, int maxRetries) {
        return IntStream.range(0, maxRetries)
            .mapToObj(i -> action)
            .reduce(
                (Boolean) null,
                (r, a) -> consumeOrRetry(context, r, a),
                (r1, r2) -> r1 || r2
            );
    }

    private Boolean consumeOrRetry(Context context, Boolean r, VersioningAction<?> a) {
        if (r == null) {
            return handleException(() -> a.consume(context), errorMessageSupplier(a));
        } else if (!r) {
            return handleException(() -> a.retry(context), errorMessageSupplier(a));
        }
        return true;
    }

    private CompletableFuture<VersioningAction<?>> asyncActionMapper(
        Context context,
        VersioningAction<?> action,
        ExecutorService executorService
    ) {
        return CompletableFuture
            .runAsync(() -> action.consumeAsync(context), executorService)
            .thenApply(CompletableFuture::completedFuture)
            .exceptionally(t -> retry(context, t, 0, action, executorService))
            .thenCompose(Function.identity())
            .thenApply((v) -> action);
    }

    private CompletableFuture<Void> retry(
        Context context,
        Throwable first, int retry,
        VersioningAction<?> action,
        ExecutorService executorService
    ) {
        if (retry >= action.getMaxRetries()) {
            return CompletableFuture.failedFuture(first);
        }
        return CompletableFuture
            .runAsync(() -> action.retryAsync(context), executorService)
            .thenApply(CompletableFuture::completedFuture)
            .exceptionally(t -> {
                first.addSuppressed(t);
                return retry(context, first, retry + 1, action, executorService);
            })
            .thenCompose(Function.identity());
    }

    private boolean handleException(Runnable runnable, Supplier<String> errorSupplier) {
        try {
            runnable.run();
        } catch (Throwable e) {
            logger.error(errorSupplier.get(), e);
            return false;
        }
        return true;
    }

    private Supplier<String> errorMessageSupplier(VersioningAction<?> a) {
        return () -> new StringBuilder("Cannot take the screenshot, ").append(a.toString()).toString();
    }

    private Stream<VersioningAction<?>> getActionStream(ExecutableActions actionsExecutor) {
        return actionsExecutor.getActions()
            .stream();
    }

    private ExecutorService getExecutorService(ExecutableActions actionsExecutor) {
        return Executors.newFixedThreadPool(actionsExecutor.getActions().size());
    }

}
