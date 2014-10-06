package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.Pipeline;
import com.elusive_code.tserver.base.PipelineStage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author Vladislav Dolgikh
 */
public class LoopPipeStage implements PipelineStage {

    public static final String ITERATIONS_CTX_PARAM = "iterations";

    private Pipeline                     content       = null;
    private boolean                      fixedInput    = false;
    private Integer                      iterations    = null;
    private BiPredicate<Context, Object> loopCondition = null;

    public LoopPipeStage(Pipeline content, int iterations) {
        this.content = content;
        this.iterations = iterations;
        this.loopCondition = IterationsLoopCondition.INSTANCE;
    }

    public LoopPipeStage(Pipeline content, BiPredicate<Context, Object> loopCondition) {
        this.content = content;
        this.loopCondition = loopCondition;
    }

    @Override
    public CompletionStage execute(Context ctx, Object input) {
        CompletableFuture future = new CompletableFuture();

        Context loopCtx = new Context(ctx);
        if (this.iterations != null) {
            loopCtx.put(ITERATIONS_CTX_PARAM, this.iterations);
        }

        if (!loopCondition.test(loopCtx,input)){
            future.complete(input);
            return future;
        }

        //first iteration start
        Context firstIterationContext = new Context(loopCtx);
        CompletionStage c = content.execute(firstIterationContext, input);

        //looping
        c = c.thenCompose((Function<Object,CompletionStage>)r->{
            if (loopCondition.test(loopCtx,r)){
                Context iterationContext = new Context(loopCtx);
                if (fixedInput){
                    return content.execute(iterationContext,input);
                } else {
                    return content.execute(iterationContext,r);
                }

            } else {
                CompletableFuture f = new CompletableFuture();
                f.complete(r);
                return f;
            }
        });

        //completion
        c.handleAsync((BiFunction<Object,Throwable,Object>)(r, ex) -> {
            if (ex != null) {
                future.completeExceptionally(ex);
            } else {
                future.complete(r);
            }
            return r;
        });

        return future;
    }

    public boolean isFixedInput() {
        return fixedInput;
    }

    public void setFixedInput(boolean fixedInput) {
        this.fixedInput = fixedInput;
    }
}
