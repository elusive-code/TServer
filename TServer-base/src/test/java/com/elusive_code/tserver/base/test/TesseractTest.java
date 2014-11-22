package com.elusive_code.tserver.base.test;

import com.elusive_code.tserver.base.Main;
import com.elusive_code.tserver.base.PipeExecutionState;
import com.elusive_code.tserver.base.Pipeline;
import com.elusive_code.tserver.base.SimplePipeManager;
import com.elusive_code.tserver.stages.FileReadStage;
import com.elusive_code.tserver.stages.tesseract.TesseractStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;

/**
 * @author Vladislav Dolgikh
 */
@RunWith(JUnit4.class)
public class TesseractTest {

    public Pipeline preparePipeline(String resultFile) throws Exception {
        if (resultFile == null) {
            resultFile = System.getProperty("java.io.tmpdir")+File.separator+"output";
        }
        Pipeline pipe = new Pipeline("tesseractPipeline");

        FileReadStage readStage = new FileReadStage();
        URL inputUrl = getClass().getClassLoader().getResource("test-data/tesseract/text_with_links.png");
        File inputFile = new File(inputUrl.toURI());
        readStage.setFile(inputFile);
        pipe.getStages().add(readStage);

        TesseractStage tessStage = new TesseractStage();
        tessStage.setConfigFile("hocr");
        pipe.getStages().add(tessStage);

        return pipe;
    }

    @Test
    public void tesseract() throws Exception {
        SimplePipeManager pipeManager = new SimplePipeManager();
        pipeManager.add(preparePipeline(null));
        PipeExecutionState state = pipeManager.launchPipeline("tesseractPipeline", "");
        List<File> result = (List<File>)state.get();
        System.out.println(result);
    }

}
