package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.PipelineStageFactory;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * @author Vladislav Dolgikh
 */
public class LoopPipeStageFactory implements PipelineStageFactory<LoopPipeStage> {

    @Override
    public LoopPipeStage create() {
        return new LoopPipeStage();
    }

    @Override
    public LoopPipeStage create(Map<String, Object> params) throws Exception {
        LoopPipeStage result = new LoopPipeStage();
        BeanUtils.populate(result,params);
        return result;
    }
}
