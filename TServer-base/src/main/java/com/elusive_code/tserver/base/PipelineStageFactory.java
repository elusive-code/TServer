package com.elusive_code.tserver.base;

import java.util.Map;

/**
 * @author Vladislav Dolgikh
 */
public interface PipelineStageFactory<T extends PipelineStage> {

    T create() throws Exception;

    T create(Map<String,Object> params) throws Exception;
}


