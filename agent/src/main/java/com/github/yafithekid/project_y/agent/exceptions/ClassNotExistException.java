package com.github.yafithekid.project_y.agent.exceptions;

public class ClassNotExistException extends Exception {
    public ClassNotExistException(String classname){
        super("Class "+classname+" does not exist");
    }
}
