package com.elusive_code.tserver.stages;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Vladislav Dolgikh
 */
public class FileWriteStage extends AbstractPipeStage {

    private String filename = null;

    @Override
    public Object run(Object input) throws Throwable {
        if (filename == null) return input;

        File file = new File(formatString(filename));

        try(FileOutputStream out = new FileOutputStream(file)){
            out.write((byte[])input);
        }

        return file;
    }
}
