package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.Pipeline;
import com.elusive_code.tserver.base.PipelineStage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private BiPredicate<Context, Object> loopCondition = null;

    public LoopPipeStage(){
        this(new Pipeline(),0);
    }

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

    public Pipeline getContent() {
        return content;
    }

    public void setContent(Pipeline content) {
        this.content = content;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public BiPredicate<Context, Object> getLoopCondition() {
        return loopCondition;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public void setLoopCondition(BiPredicate<Context, Object> loopCondition) {
        this.loopCondition = loopCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoopPipeStage that = (LoopPipeStage) o;

        if (fixedInput != that.fixedInput) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (iterations != null ? !iterations.equals(that.iterations) : that.iterations != null) return false;
        if (loopCondition != null ? !loopCondition.equals(that.loopCondition) : that.loopCondition != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (fixedInput ? 1 : 0);
        result = 31 * result + (iterations != null ? iterations.hashCode() : 0);
        result = 31 * result + (loopCondition != null ? loopCondition.hashCode() : 0);
        return result;
    }
}
