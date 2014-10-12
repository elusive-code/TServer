package com.elusive_code.tserver.base;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.concurrent.CompletionStage;

/**
 * Pipeline stage, should have default constructor and generally be easily serializable.
 *
 * @author Vladislav Dolgikh
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface PipelineStage<I,O> extends Serializable {

    CompletionStage<O> execute(Context ctx, I input);

}
