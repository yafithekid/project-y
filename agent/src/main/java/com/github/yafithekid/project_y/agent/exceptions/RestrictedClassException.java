package com.github.yafithekid.project_y.agent.exceptions;

public class RestrictedClassException extends Exception{
    public RestrictedClassException(String name){
        super(name+" is restricted class, cannot be profiled");

    }
}
