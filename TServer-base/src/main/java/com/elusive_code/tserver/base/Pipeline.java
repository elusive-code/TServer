package com.elusive_code.tserver.base;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Vladislav Dolgikh
 */
public class Pipeline implements PipelineStage, Serializable {
    
    private List<PipelineStage> stages;
    private String name;

    public Pipeline() {
        this(null, new ArrayList<>());
    }

    public Pipeline(String name) {
        this(name, new ArrayList<>());
    }

    public Pipeline(String name, List<PipelineStage> stages) {
        this.stages = new ArrayList<>(stages);
        this.name = name;
    }

    @Override
    public PipeExecutionState execute(Context ctx, Object input) {

        PipeExecutionState result = new PipeExecutionState(this,ctx);

        //context init
        ctx.putFinal(CtxParam.PIPELINE.key(), this);
        ctx.putFinal(CtxParam.INPUT.key(), input);
        ctx.putFinal(CtxParam.START_DATE.key(), new Date());

        CompletionStage currentStage = null;
        List stageResults;

        //synchronization for #stages
        synchronized (this) {
            List<PipelineStage> stageList = getStages();
            stageResults = new ArrayList<>(stageList.size());
            synchronized (ctx) {
                ctx.putFinal(CtxParam.STAGE_RESULTS.key(), Collections.unmodifiableList(stageResults));
            }

            //creating asynchronous execution
            for (PipelineStage stage : stageList) {
                if (currentStage == null) {
                    //first stage
                    ctx.put(CtxParam.CURRENT_STAGE.key(), stage);
                    currentStage = stage.execute(ctx, input);
                } else {
                    //piping
                    Function<Object, CompletionStage> next = r -> {
                        CompletionStage nextStage = stage.execute(ctx, r);
                        ctx.put(CtxParam.CURRENT_STAGE.key(), stage);
                        synchronized (ctx){
                            stageResults.add(r);
                        }
                        return nextStage;
                    };
                    currentStage = currentStage.thenComposeAsync(next);
                }
            }
        }

        //finish
        currentStage.handleAsync((BiFunction<Object,Throwable,Object>)(r,ex) -> {
            if (ex != null) {
                ctx.putFinal(CtxParam.ERROR.key(), ex);
                Object stage = ctx.get(CtxParam.CURRENT_STAGE.key());
                ctx.putFinal(CtxParam.ERROR_STAGE.key(), stage);
                result.completeExceptionally(ex);
            } else {
                ctx.putFinal(CtxParam.OUTPUT.key(), r);
                synchronized (ctx) {
                    stageResults.add(r);
                }
                result.complete(r);
            }
            ctx.putFinal(CtxParam.FINISH_DATE.key(), new Date());
            ctx.remove(CtxParam.CURRENT_STAGE.key());
            return r;
        });

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public synchronized List<PipelineStage> getStages() {
        return stages;
    }
}
