package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;


/**
 * @author Vladislav Dolgikh
 */
public class TestPipeStage extends AbstractPipeStage<Object,Integer> {

    private int initialValue = 0;

    public TestPipeStage() {
    }

    public TestPipeStage(int initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Integer run(Context ctx, Object input) throws Throwable {
        Integer c = (Integer)ctx.get("testCounter");
        if (c == null) {
            c = initialValue;
        } else {
            c++;
        }
        ctx.put("testCounter",c);
        ctx.put("input_"+c,input);
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestPipeStage that = (TestPipeStage) o;

        if (initialValue != that.initialValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return initialValue;
    }
}
