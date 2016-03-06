package com.github.yafithekid.project_y.agent.exceptions;

public class IsAbstractMethodException extends Exception{
    public IsAbstractMethodException(String classname,String methodname){
        super(classname+"#"+methodname+" is abstract method");
    }
}
