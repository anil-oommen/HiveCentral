package com.oom.hive.central.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping("/ng")
    public String forwardDefault(){
        System.err.println("--------------------Executing Forward");
        return "forward:/ng/index.html";
    }
}
