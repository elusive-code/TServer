package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.PipelineStage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

/**
 * @author Vladislav Dolgikh
 */
public abstract class AbstractPipeStage<I,O> implements PipelineStage<I,O> {

    @Override
    public CompletionStage<O> execute(Context ctx, I input) {
        ExecutorService executorService = ctx.getExecutor();
        CompletableFuture<O> future = new CompletableFuture();

        executorService.submit(()->{
            try {
                O output = run(ctx, input);
                future.complete(output);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    public abstract O run(Context ctx, I input) throws Throwable;
}
