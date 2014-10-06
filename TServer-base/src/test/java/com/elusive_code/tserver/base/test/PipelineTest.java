package com.elusive_code.tserver.base.test;

import com.elusive_code.tserver.base.PipeManager;
import com.elusive_code.tserver.base.Pipeline;
import com.elusive_code.tserver.base.PipelineStage;
import com.elusive_code.tserver.stages.TestPipeStage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
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
        PipeManager pipeManager = new PipeManager();
        pipeManager.add(preparePipeline());
        CompletionStage s = pipeManager.launch("testPipeline", "testInput");
        s.toCompletableFuture().get();

    }
}
