package com.elusive_code.tserver.base;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * @author Vladislav Dolgikh
 */
public interface PipelineManager extends Collection<Pipeline> {

    Context getRootContext();

    void setRootContext(Context ctx);

    ExecutorService getExecutor();

    void setExecutor(ExecutorService exec);

    void addPipeline(Pipeline pipe);

    Pipeline getPipeline(String name);

    void removePipeline(Pipeline pipe);

    Pipeline removePipeline(String name);

    PipeExecutionState launchPipeline(String pipeName, Object input);

    PipeExecutionState launchPipeline(Pipeline pipe, Object input);

}
