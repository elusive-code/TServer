package com.elusive_code.tserver.stages;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Vladislav Dolgikh
 */
public class FileReadStage extends AbstractPipeStage {

    private String filename = null;
    private File file = null;

    @Override
    public Object run(Object input) throws Throwable {
        File file = calcFile();
        if (file == null) return null;
        byte[] data;
        try(InputStream in = new FileInputStream(file)){
            data = IOUtils.toByteArray(in);
        }
        return data;
    }

    private File calcFile(){
        if (file != null) return file;
        if (filename == null) return null;
        return new File(formatString(filename));
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
