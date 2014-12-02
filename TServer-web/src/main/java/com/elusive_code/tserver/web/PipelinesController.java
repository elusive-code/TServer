package com.elusive_code.tserver.web;

import com.elusive_code.tserver.base.Pipeline;
import com.elusive_code.tserver.base.PipelineManager;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.ArrayList;

/**
 * @author Vladislav Dolgikh
 */
@RestController
public class PipelinesController {

    @Resource(name = "pipelineManagerImpl")
    private PipelineManager pipelineManager;

    @RequestMapping(value = "/pipelines",method = RequestMethod.GET)
    public Iterable<Pipeline> index(){
        return new ArrayList(pipelineManager);
    }

    @RequestMapping(value = "/pipelines",method = RequestMethod.POST)
    public void createPipeline(Pipeline pipeline){
        pipelineManager.addPipeline(pipeline);
    }

    @RequestMapping(value = "/pipelines/{name}",method = RequestMethod.GET)
    public Pipeline readPipeline(@PathVariable("name")String name){
        Pipeline pipe = pipelineManager.getPipeline(name);
        return pipe;
    }

    @RequestMapping(value = "/pipelines/{name}",method = {RequestMethod.POST,RequestMethod.PUT})
    public void updatePipeline(@PathVariable("name")String name,
                               Pipeline pipeline) throws Exception {
        Pipeline pipe = pipelineManager.removePipeline(name);
        BeanUtils.copyProperties(pipe,pipeline);
        pipelineManager.addPipeline(pipe);
    }

    @RequestMapping(value = "/pipelines",method = RequestMethod.PUT)
    public void updatePipeline(Pipeline pipeline) throws Exception {
       updatePipeline(pipeline.getName(),pipeline);
    }

    @RequestMapping(value = "/pipelines/{name}",method = {RequestMethod.DELETE})
    public void removePipeline(@PathVariable("name")String name){
        pipelineManager.removePipeline(name);
    }
}
