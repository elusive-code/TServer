package com.elusive_code.tserver.service;

import com.elusive_code.tserver.base.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Vladislav Dolgikh
 */
@Service
public class PipelineManagerImpl implements PipelineManager {

    private SimplePipeManager pipeManager;

    private Map<UUID, PipeExecutionState> executions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        ExecutorService executorService = new ForkJoinPool(Runtime.getRuntime().availableProcessors()+2);
        pipeManager = new SimplePipeManager(executorService);
    }

    public Map<UUID, PipeExecutionState> getProcesses(){
        return Collections.unmodifiableMap(executions);
    }

    public void cleanProcesses(){
        synchronized (executions){
            Iterator<Map.Entry<UUID,PipeExecutionState>> i = executions.entrySet().iterator();
            while (i.hasNext()){
                Map.Entry<UUID,PipeExecutionState> e = i.next();
                if (e.getValue().isDone()){
                    i.remove();
                }
            }
        }
    }

    @Override
    public Context getRootContext() {
        return pipeManager.getRootContext();
    }

    @Override
    public void setRootContext(Context ctx) {
        pipeManager.setRootContext(ctx);
    }

    @Override
    public ExecutorService getExecutor() {
        return pipeManager.getExecutor();
    }

    @Override
    public void setExecutor(ExecutorService exec) {
        pipeManager.setExecutor(exec);
    }

    @Override
    public void addPipeline(Pipeline pipe) {
        pipeManager.addPipeline(pipe);
    }

    @Override
    public Pipeline getPipeline(String name) {
        return pipeManager.getPipeline(name);
    }

    @Override
    public void removePipeline(Pipeline pipe) {
        pipeManager.removePipeline(pipe);
    }

    @Override
    public Pipeline removePipeline(String name) {
        return pipeManager.removePipeline(name);
    }

    @Override
    public PipeExecutionState launchPipeline(String pipeName, Object input) {
        PipeExecutionState state = pipeManager.launchPipeline(pipeName, input);
        UUID uuid = UUID.randomUUID();
        executions.put(uuid,state);
        return state;
    }

    @Override
    public PipeExecutionState launchPipeline(Pipeline pipe, Object input) {
        return pipeManager.launchPipeline(pipe,input);
    }

    @Override
    public Iterator<Pipeline> iterator() {
        return pipeManager.iterator();
    }

    @Override
    public int size() {
        return pipeManager.size();
    }

    @Override
    public boolean isEmpty() {
        return pipeManager.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return pipeManager.contains(o);
    }

    @Override
    public Object[] toArray() {
        return pipeManager.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return pipeManager.toArray(a);
    }

    @Override
    public boolean add(Pipeline pipeline) {
        return pipeManager.add(pipeline);
    }

    @Override
    public boolean remove(Object o) {
        return pipeManager.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return pipeManager.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Pipeline> c) {
        return pipeManager.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return pipeManager.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return pipeManager.retainAll(c);
    }

    @Override
    public void clear() {
        pipeManager.clear();
    }
}
