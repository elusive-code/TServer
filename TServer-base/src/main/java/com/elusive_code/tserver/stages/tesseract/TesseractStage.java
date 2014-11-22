package com.elusive_code.tserver.stages.tesseract;

import com.elusive_code.tserver.base.Context;
import com.elusive_code.tserver.base.CtxParam;
import com.elusive_code.tserver.stages.AbstractPipeStage;
import com.elusive_code.tserver.util.ImageHelper;
import com.elusive_code.tserver.util.StreamRedirector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vladislav Dolgikh
 */
public class TesseractStage extends AbstractPipeStage {

    private String              pathToTesseract  = "{{env.TESSDATA_PREFIX}}/tesseract";
    private String              tessDataPath     = null;
    private String              userWordsFile    = null;
    private String              userPatternsFile = null;
    private List<String>        languages        = new ArrayList();
    private Analysis            analysis         = Analysis.APS;
    private Map<String, String> config           = new HashMap();
    private String              configFile       = "";
    private String              imageFormat      = "png";

    @Override
    public Object run(Object input) throws Throwable {

        //preparing parameters
        String tesseract = formatString(pathToTesseract);

        String tessData = formatString(tessDataPath);
        if (!StringUtils.isBlank(tessData)) {
            tessData = "--tessdata-dir " + tessData;
        } else {
            tessData = "";
        }

        String userWords = formatString(userWordsFile);
        if (!StringUtils.isBlank(userWords)) {
            userWords = "--user-words " + userWords;
        } else {
            userWords = "";
        }

        String userPatterns = formatString(userPatternsFile);
        if (userPatterns != null) {
            userPatterns = "--user-patterns "+userPatterns;
        } else {
            userPatterns = "";
        }

        String configvars = config.entrySet()
                                  .parallelStream()
                                  .map(e->"-c "+formatString(e.getKey())+"="+formatString(e.getValue()))
                                  .collect(Collectors.joining(" "));

        String langs = languages.parallelStream()
            .map(s -> formatString(s))
            .collect(Collectors.joining("+"));
        if (!StringUtils.isBlank(langs)){
            langs = "-l "+langs;
        }

        String psm = analysis==null?"":"-psm "+analysis.ordinal();

        String config = StringUtils.isBlank(configFile)?"":configFile;


        String execId = UUID.randomUUID().toString();
        File imageFile = createInputFile(execId,input, imageFormat);
        File outputFolder = createOutputFolder(execId);
        String output = outputFolder.getAbsolutePath() + File.separator + "result";

        try {
            //launching
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(tesseract,
                       imageFile.getAbsolutePath(),
                       output,
                       tessData,
                       userWords,
                       userPatterns,
                       configvars,
                       langs,
                       psm,
                       config);
            pb.inheritIO();
            Process p = pb.start();

            p.waitFor();

            return Arrays.asList(outputFolder.listFiles());
        } finally {
            //imageFile.delete();
        }
    }

    private File createInputFile(String execId, Object source, String format) throws IOException {
        String tmpFolder = (String)Context.current().get(CtxParam.TEMP_FOLDER);
        File tesseractFolder = new File(tmpFolder+File.separator+getClass().getName());
        if (!tesseractFolder.exists()){
            tesseractFolder.mkdir();
            tesseractFolder.deleteOnExit();
        }
        File currentRunFolder = new File(tesseractFolder.getAbsolutePath()+File.separator+execId);
        if (!currentRunFolder.exists()){
            currentRunFolder.mkdir();
            currentRunFolder.deleteOnExit();
        }

        File inputFile = File.createTempFile("input","."+format,currentRunFolder);
        inputFile.deleteOnExit();
        BufferedImage image = ImageHelper.loadImage(source);
        boolean written = ImageIO.write(image,format,inputFile);
        return inputFile;
    }

    private File createOutputFolder(String execId){
        String tmpFolder = (String)Context.current().get(CtxParam.TEMP_FOLDER);
        File tesseractFolder = new File(tmpFolder+File.separator+getClass().getName());
        if (!tesseractFolder.exists()){
            tesseractFolder.mkdir();
            tesseractFolder.deleteOnExit();
        }
        File currentRunFolder = new File(tesseractFolder.getAbsoluteFile()+File.separator+execId);
        if (!currentRunFolder.exists()){
            currentRunFolder.mkdir();
            currentRunFolder.deleteOnExit();
        }
        File outputFolder = new File(currentRunFolder.getAbsoluteFile()+File.separator+"output");
        if (!outputFolder.exists()){
            outputFolder.mkdir();
            outputFolder.deleteOnExit();
        }
        return outputFolder;
    }

    public String getPathToTesseract() {
        return pathToTesseract;
    }

    public void setPathToTesseract(String pathToTesseract) {
        this.pathToTesseract = pathToTesseract;
    }

    public String getTessDataPath() {
        return tessDataPath;
    }

    public void setTessDataPath(String tessDataPath) {
        this.tessDataPath = tessDataPath;
    }

    public String getUserWordsFile() {
        return userWordsFile;
    }

    public void setUserWordsFile(String userWordsFile) {
        this.userWordsFile = userWordsFile;
    }

    public String getUserPatternsFile() {
        return userPatternsFile;
    }

    public void setUserPatternsFile(String userPatternsFile) {
        this.userPatternsFile = userPatternsFile;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

}
