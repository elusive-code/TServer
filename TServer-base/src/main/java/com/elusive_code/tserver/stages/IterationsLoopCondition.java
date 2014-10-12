package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;

import java.util.function.BiPredicate;

/**
 * @author Vladislav Dolgikh
 */
public enum  IterationsLoopCondition implements BiPredicate<Context,Object> {

    INSTANCE;

    private IterationsLoopCondition() {
    }

    @Override
    public boolean test(Context context, Object o) {
        Integer iters = context.get(LoopPipeStage.ITERATIONS_CTX_PARAM, false);
        return iters > 0;
    }
}
