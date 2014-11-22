package com.elusive_code.tserver.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vladislav Dolgikh
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class SimplePipeManager extends AbstractSet<Pipeline> implements PipelineManager {

    private static Logger log = Logger.getLogger(SimplePipeManager.class.getName());

    //to prevent memory leaks
    private boolean autoCleanContexts  = true;
    private boolean autoCleanTempFiles = true;

    private Context         rootContext;
    private ExecutorService executor;
    private List<Pipeline>        pipes   = new ArrayList<>();
    private Map<String, Pipeline> pipeMap = new HashMap<>();

    public SimplePipeManager() {
        this(null, null);
    }

    public SimplePipeManager(Context rootContext) {
        this(null, rootContext);
    }

    public SimplePipeManager(ExecutorService executor) {
        this(executor, null);
    }

    public SimplePipeManager(ExecutorService executor, Context rootContext) {
        this.executor = executor;
        if (this.executor == null) {
            this.executor = defaultExecutorService();
        }

        this.rootContext = rootContext;
        if (this.rootContext == null) {
            this.rootContext = new Context();
        }

        this.rootContext.putFinal(CtxParam.EXECUTOR.name(), this.executor);
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Context getRootContext() {
        return rootContext;
    }

    @Override
    public void setRootContext(Context rootContext) {
        this.rootContext = rootContext;
    }

    public boolean isAutoCleanContexts() {
        return autoCleanContexts;
    }

    public void setAutoCleanContexts(boolean autoCleanContexts) {
        this.autoCleanContexts = autoCleanContexts;
    }

    public boolean isAutoCleanTempFiles() {
        return autoCleanTempFiles;
    }

    public void setAutoCleanTempFiles(boolean autoCleanTempFiles) {
        this.autoCleanTempFiles = autoCleanTempFiles;
    }

    @Override
    public void addPipeline(Pipeline pipe) {
        add(pipe);
    }

    @Override
    public void removePipeline(Pipeline pipe) {
        remove(pipe);
    }

    @Override
    public Pipeline removePipeline(String name){
        Pipeline result = getPipeline(name);
        removePipeline(result);
        return result;
    }

    @Override
    public PipeExecutionState launchPipeline(String pipeName, Object input) {
        return launchPipeline(getPipeline(pipeName), input);
    }

    @Override
    public PipeExecutionState launchPipeline(Pipeline pipe, Object input) {
        Context pipeCtx = new Context(rootContext);
        PipeExecutionState s = pipe.execute(pipeCtx,input);
        s.whenCompleteAsync((BiConsumer<Object, Throwable>) (r, t) -> {
            //prevents memory leaks
            if (autoCleanContexts){
                rootContext.getChildren().remove(pipeCtx);
            }
        });
        return s;
    }

    public Pipeline getPipeline(String name){
        return pipeMap.get(name);
    }

    @Override
    public boolean add(Pipeline pipeline) {
        boolean result = pipes.add(pipeline);
        pipeMap.put(pipeline.getName(),pipeline);
        return result;
    }

    @Override
    public Iterator<Pipeline> iterator() {
        return new PipeIterator(pipes.iterator());
    }

    @Override
    public int size() {
        return pipes.size();
    }

    @Override
    public boolean contains(Object o) {
        return pipes.contains(o);
    }

    private class PipeIterator implements Iterator<Pipeline> {
        private Iterator<Pipeline> iter;
        private Pipeline last = null;

        private PipeIterator(Iterator<Pipeline> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Pipeline next() {
            last = iter.next();
            return last;
        }

        @Override
        public void remove() {
            iter.remove();
            pipeMap.remove(last.getName());
        }
    }

    public static ExecutorService defaultExecutorService(){
        return new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 2);
    }

}
