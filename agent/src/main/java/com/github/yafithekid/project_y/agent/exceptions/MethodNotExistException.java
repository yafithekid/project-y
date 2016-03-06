package com.github.yafithekid.project_y.agent.exceptions;

public class MethodNotExistException extends Exception{
    public MethodNotExistException(String classname,String methodname){
        super(classname+"#"+methodname+" does not exist");
    }
}
