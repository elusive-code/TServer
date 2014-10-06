package com.elusive_code.tserver.util;

import org.apache.commons.lang3.Validate;

/**
 * @author Vladislav Dolgikh
 */
public class ContextClassLoader implements AutoCloseable {

    private ClassLoader oldClassLoader = null;

    public ContextClassLoader(ClassLoader cl){
        Validate.notNull(cl);
        this.oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
    }

    @Override
    public void close() throws Exception {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
}
