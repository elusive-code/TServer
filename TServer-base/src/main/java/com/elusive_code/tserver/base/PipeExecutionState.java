package com.elusive_code.tserver.base;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author Vladislav Dolgikh
 */
public class PipeExecutionState extends CompletableFuture {

    private Pipeline pipeline;
    private Context  context;

    public PipeExecutionState(Pipeline pipeline, Context context) {
        this.pipeline = pipeline;
        this.context = context;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public Context getContext() {
        return context;
    }

    public PipelineStage getCurrentStage(){
        return (PipelineStage)context.get(CtxParam.CURRENT_STAGE);
    }

    public Object getInput(){
        return context.get(CtxParam.INPUT);
    }

    public Throwable getError(){
        return (Throwable)context.get(CtxParam.ERROR);
    }

    public PipelineStage getErrorStage(){
        return (PipelineStage)context.get(CtxParam.ERROR_STAGE);
    }

    public Date getStartDate(){
        return (Date)context.get(CtxParam.START_DATE);
    }

    public Date getFinishDate(){
        return (Date)context.get(CtxParam.FINISH_DATE);
    }

}
