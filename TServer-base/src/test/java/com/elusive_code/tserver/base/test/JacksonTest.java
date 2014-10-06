package com.elusive_code.tserver.base.test;

import com.elusive_code.tserver.base.Main;
import com.elusive_code.tserver.base.PipeManager;
import com.elusive_code.tserver.base.Pipeline;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * @author Vladislav Dolgikh
 */
@RunWith(JUnit4.class)
public class JacksonTest {

    @Test
    public void test() throws IOException {
        Pipeline pipe = PipelineTest.preparePipeline();
        ObjectMapper om = Main.objectMapper();
        String json = om.writeValueAsString(pipe);
        System.out.println(json);

        pipe = om.readValue(json,Pipeline.class);
        System.out.println(pipe);
    }
}
