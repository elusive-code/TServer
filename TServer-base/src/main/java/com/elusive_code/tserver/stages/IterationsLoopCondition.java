package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;

import java.util.function.BiPredicate;

/**
 * @author Vladislav Dolgikh
 */
public class IterationsLoopCondition implements BiPredicate<Context,Object> {

    public static IterationsLoopCondition INSTANCE = new IterationsLoopCondition();

    private IterationsLoopCondition() {
    }

    @Override
    public boolean test(Context context, Object o) {
        Integer iters = context.get(LoopPipeStage.ITERATIONS_CTX_PARAM, false);
        return iters > 0;
    }

}
