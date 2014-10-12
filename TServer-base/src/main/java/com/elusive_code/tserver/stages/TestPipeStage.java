package com.elusive_code.tserver.stages;

import com.elusive_code.tserver.base.Context;


/**
 * @author Vladislav Dolgikh
 */
public class TestPipeStage extends AbstractPipeStage<Object,Integer> {

    @Override
    public Integer run(Object input) throws Throwable {
        Integer c = getParam("testCounter");
        if (c == null) {
            c = 0;
        } else {
            c++;
        }
        setParam("testCounter",c);
        setParam("input_"+c,input);
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
