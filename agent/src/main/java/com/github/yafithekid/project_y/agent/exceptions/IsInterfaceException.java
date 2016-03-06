package com.github.yafithekid.project_y.agent.exceptions;

public class IsInterfaceException extends Exception{
    public IsInterfaceException(String classname){
        super(classname+" is an interface");
    }
}
