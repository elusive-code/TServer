package com.elusive_code.tserver.base;

import java.io.Serializable;
import java.util.concurrent.CompletionStage;

/**
 * Pipeline stage, should have default constructor and generally be easily serializable.
 *
 * @author Vladislav Dolgikh
 */
public interface PipelineStage<I,O> extends Serializable {

    CompletionStage<O> execute(Context ctx, I input);

}
