package com.elusive_code.tserver.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vladislav Dolgikh
 */
public class Pipeline implements PipelineStage, Serializable {

    private static Logger log = Logger.getLogger(Pipeline.class.getName());
    
    private List<PipelineStage> stages;
    private String name;
    private boolean autoCleanTempFiles = true;

    public Pipeline() {
        this(null, Collections.emptyList());
    }

    public Pipeline(String name) {
        this(name, Collections.emptyList());
    }

    @JsonCreator
    public Pipeline(@JsonProperty("name") String name, @JsonProperty("stages") List<PipelineStage> stages) {
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

        UUID processId;
        File tmpFolder;
        do {
            processId = UUID.randomUUID();
            tmpFolder = new File(System.getProperty("java.io.tmpdir") + File.separator + "tserver"
                                 + File.separator + processId.toString());
        } while (tmpFolder.exists());
        tmpFolder.mkdir();
        tmpFolder.deleteOnExit();
        ctx.putFinal(CtxParam.PROCESS_ID.key(), processId);
        ctx.putFinal(CtxParam.TEMP_FOLDER.key(), tmpFolder.getAbsolutePath());

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
            if (autoCleanTempFiles){
                String tmpFolderName = (String)ctx.get(CtxParam.TEMP_FOLDER);
                File folder = new File(tmpFolderName);
                if (folder.exists()){
                    try {
                        FileUtils.deleteDirectory(folder);
                    }catch (Exception ex1){
                        log.log(Level.SEVERE,"Failed to remove directory "+tmpFolderName,ex1);
                    }
                }
            }

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

    public synchronized List<PipelineStage> getStages() {
        return stages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pipeline pipeline = (Pipeline) o;

        if (name != null ? !name.equals(pipeline.name) : pipeline.name != null) return false;
        if (stages != null ? !stages.equals(pipeline.stages) : pipeline.stages != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
