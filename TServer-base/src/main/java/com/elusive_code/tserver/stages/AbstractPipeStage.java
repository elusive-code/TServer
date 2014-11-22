package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.PipelineStage;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public String formatString(String template) {
        if (template == null) return template;
        Handlebars handlebars = new Handlebars();
        try {
            Template t = handlebars.compileInline(template);
            Map<String,Object> params = new HashMap();
            params.put("context", Context.current());
            params.put("env", System.getenv());
            params.put("system", System.getProperties());
            params.put("stage", this);
            return t.apply(params);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Failed to fill template: "+template,ex);
            return template;
        }
    }

    public abstract O run(I input) throws Throwable;
}
