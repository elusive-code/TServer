package com.elusive_code.tserver.base.test;

import com.elusive_code.tserver.base.*;
import com.elusive_code.tserver.stages.TestPipeStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CompletionStage;

/**
 * @author Vladislav Dolgikh
 */
@RunWith(JUnit4.class)
public class PipelineTest {

    public static Pipeline preparePipeline(){
        List<PipelineStage> stages = new ArrayList<>();
        stages.add(new TestPipeStage());
        stages.add(new TestPipeStage());
        Pipeline result = new Pipeline("testPipeline",stages);
        return result;
    }

    @Test
    public void test() throws Exception{
        SimplePipeManager pipeManager = new SimplePipeManager();
        pipeManager.add(preparePipeline());
        CompletionStage s = pipeManager.launchPipeline("testPipeline", "testInput");
        s.toCompletableFuture().get();
    }

    @Test
    public void testSerialization() throws Exception {
        Pipeline pipeline = preparePipeline();

        ObjectMapper mapper = Main.objectMapper();
        String s = mapper.writeValueAsString(pipeline);
        Pipeline desPipeline = mapper.readValue(s, Pipeline.class);

        Assert.assertEquals(pipeline,desPipeline);
    }

    @Test
    public void testStagesSerialization() throws Exception {
        ObjectMapper mapper = Main.objectMapper();
        for (PipelineStage stage: ServiceLoader.load(PipelineStage.class)){
            checkStageSerialization(mapper, stage);
        }

        for (PipelineStageFactory stageFactory: ServiceLoader.load(PipelineStageFactory.class)){
            PipelineStage stage = stageFactory.create();
            checkStageSerialization(mapper, stage);
        }
    }

    private void checkStageSerialization(ObjectMapper mapper, PipelineStage stage) throws Exception{
        Pipeline pipe = new Pipeline();
        pipe.getStages().add(stage);
        String s = mapper.writeValueAsString(pipe);
        Pipeline desPipe = mapper.readValue(s,Pipeline.class);
        Assert.assertEquals(stage.getClass()+" serialization test failed",pipe,desPipe);
    }
}
