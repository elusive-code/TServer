package com.elusive_code.tserver.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Vladislav Dolgikh
 */
@Controller
public class PagesController {

    @RequestMapping("/index")
    public String index(ModelMap model){
        return "index";
    }

    @RequestMapping("/test")
    public String test(ModelMap model){
        return "test";
    }

}
