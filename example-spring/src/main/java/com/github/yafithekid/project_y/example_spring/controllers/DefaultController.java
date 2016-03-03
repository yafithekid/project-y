package com.github.yafithekid.project_y.example_spring.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    //for ensuring the controller works
    @RequestMapping("/home")
    public String testHome(){
        return "hello world!";
    }

    //this request will be displayed in about 5 seconds
    @RequestMapping("/long")

    public String testLong(
        @RequestParam(name = "time",value = "",defaultValue = "5000") int time
    ){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "you wait for "+time+" millisecs";
    }

}
