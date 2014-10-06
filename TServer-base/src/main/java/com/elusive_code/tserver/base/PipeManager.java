package com.elusive_code.tserver.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;

/**
 * @author Vladislav Dolgikh
 */
public class PipeManager extends AbstractSet<Pipeline> {

    //to prevent memory leaks
    private boolean autoCleanContexts = true;

    private Context         rootContext;
    private ExecutorService executor;
    private Set<Pipeline>         pipes   = new LinkedHashSet<>();
    private Map<String, Pipeline> pipeMap = new HashMap<>();

    public PipeManager() {
        this(null,null);
    }
    public PipeManager(Context rootContext) {
        this(null,rootContext);
    }
    public PipeManager(ExecutorService executor) {
        this(executor,null);
    }

    public PipeManager(ExecutorService executor, Context rootContext) {
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

    public PipeExecutionState launch(String pipeName, Object input) {
        Pipeline pipe = getPipeline(pipeName);
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

    public Context getRootContext(){
        return rootContext;
    }

    public boolean isAutoCleanContexts() {
        return autoCleanContexts;
    }

    public void setAutoCleanContexts(boolean autoCleanContexts) {
        this.autoCleanContexts = autoCleanContexts;
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
