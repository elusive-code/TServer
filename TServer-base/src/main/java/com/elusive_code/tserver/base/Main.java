package com.elusive_code.tserver.base;

import com.elusive_code.tserver.util.ContextClassLoader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Dolgikh
 */
public class Main {

    public static Options defineOptions(){
        Options result = new Options();

        result.addOption("p", "pipeline", true, "File that contains pipeline configuration, default - \"pipeline.json\"");
        result.addOption("n", "name", true, "Name of the pipeline to launch, by default - first one");
        result.addOption("i", "input", true, "File with input data, default - standard input");
        result.addOption("o", "output", true, "File to put output data into, default - standard output");

        result.addOption("I", "input-context", true, "File that contains initial context data, default - \"context.json\"");
        result.addOption("O", "output-context", true, "File to store final context data, default - same as input-context");

        result.addOption("h", "help", false, "Show usage (this screen)");

        return result;
    }

    public static ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper(){

        };
        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "@class");
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    public static PipeManager loadPipeManager(ObjectMapper objectMapper, String pipelineFilename, String contextFile) throws IOException {
        Context root = null;
        if (!StringUtils.isBlank(contextFile)){
            File f = new File(contextFile);
            if (f.exists()){
                root = objectMapper.readValue(new File(contextFile),Context.class);
            }
        }

        PipeManager pipeManager = new PipeManager(root);
        pipeManager.setAutoCleanContexts(false);

        File pipelineFile = new File(pipelineFilename);
        try {
            List<Pipeline> pipeList = objectMapper.readValue(pipelineFile, ArrayList.class);
            pipeList.forEach(p -> pipeManager.add(p));
        } catch (JsonParseException | JsonMappingException ex) {
            Pipeline pipe = objectMapper.readValue(pipelineFile, Pipeline.class);
            pipeManager.add(pipe);
        }

        return pipeManager;
    }

    public static void main(String[] args) throws Exception {
        Options options = defineOptions();
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(" ",options,true);
            return;
        }

        //Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());

        //load pipeline(s)
        String pipelineFile = commandLine.getOptionValue("pipeline");
        if (StringUtils.isBlank(pipelineFile)) {
            pipelineFile = "pipeline.json";
        }

        String inputContext = commandLine.getOptionValue("input-context");
        if (StringUtils.isBlank(inputContext)){
            inputContext = "context.json";
        }

        ObjectMapper objectMapper = objectMapper();
        PipeManager pipeManager = loadPipeManager(objectMapper, pipelineFile, inputContext);

        //select pipeline
        String pipeName = commandLine.getOptionValue("name");
        if (StringUtils.isBlank(pipeName)){
            Pipeline pipe = pipeManager.iterator().next();
            pipeName = pipe.getName();
        }

        //read input data
        String inputFile = commandLine.getOptionValue("input");
        byte[] input = null;
        if (StringUtils.isBlank(inputFile)) {
            input = IOUtils.toByteArray(System.in);
        } else {
            try (FileInputStream fin = new FileInputStream(inputFile)) {
                input = IOUtils.toByteArray(fin);
            }
        }

        //execution
        PipeExecutionState exec = pipeManager.launch(pipeName,input);
        Object result = exec.get();
        if (!(result instanceof byte[])){
            result = objectMapper.writeValueAsBytes(result);
        }

        //output context
        String outputContext = commandLine.getOptionValue("output-context");
        if (StringUtils.isBlank(outputContext)){
            outputContext = inputContext;
        }

        if (!StringUtils.isBlank(outputContext)){
            objectMapper.writeValue(new File(outputContext),pipeManager.getRootContext());
        }

        //output result
        String outputFile = commandLine.getOptionValue("output");
        if (StringUtils.isBlank(outputFile)){
            System.out.write((byte[]) result);
        } else {
            try (FileOutputStream fout = new FileOutputStream(outputFile)) {
                IOUtils.write((byte[]) result, fout);
            }
        }
    }
}
