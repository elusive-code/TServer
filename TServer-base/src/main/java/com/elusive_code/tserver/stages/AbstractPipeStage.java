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

    public <T> T getParam(String name){
        return (T)Context.current().get(getClass().getName() + "." + name);
    }

    public void setParam(String name,Object value){
        Context.current().put(getClass().getName() + "." + name, value);
    }

    @Override
    public CompletionStage<O> execute(Context ctx, I input) {
        ExecutorService executorService = ctx.getExecutor();
        CompletableFuture<O> future = new CompletableFuture();

        executorService.submit(()->{
            try {
                Context.current(ctx);
                O output = run(input);
                future.complete(output);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            } finally {
                Context.current(null);
            }
        });

        return future;
    }

    public abstract O run(I input) throws Throwable;
}
