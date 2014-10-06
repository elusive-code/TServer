package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.stages.AbstractPipeStage;


/**
 * @author Vladislav Dolgikh
 */
public class TestPipeStage extends AbstractPipeStage<Object,Integer> {

    @Override
    public Integer run(Context ctx, Object input) throws Throwable {
        Integer c = (Integer)ctx.get("testCounter");
        if (c == null) {
            c = 0;
        } else {
            c++;
        }
        ctx.put("testCounter",c);
        ctx.put("input_"+c,input);
        return c;
    }
}
