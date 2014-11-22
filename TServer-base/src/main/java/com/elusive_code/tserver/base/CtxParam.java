package com.elusive_code.tserver.base;

/**
 * @author Vladislav Dolgikh
 */
public enum CtxParam {

    EXECUTOR,
    PIPELINE,

    INPUT,
    START_DATE,

    PROCESS_ID,
    TEMP_FOLDER,
    CURRENT_STAGE,
    STAGE_RESULTS,

    FINISH_DATE,
    ERROR,
    ERROR_STAGE,
    OUTPUT
    ;


    private String key;

    CtxParam() {
        this.key = name();
    }

    CtxParam(String key) {
        this.key = key;
    }

    public String key(){
        return key;
    }
}
