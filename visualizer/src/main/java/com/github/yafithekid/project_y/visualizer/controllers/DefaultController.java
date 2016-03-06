package com.github.yafithekid.project_y.visualizer.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DefaultController {
    @RequestMapping("")
    public ModelAndView home(){
        return new ModelAndView("home");
    }
}
