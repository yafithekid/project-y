package com.github.yafithekid.project_y.commons.config;

public class AgentConfigErrorAction {
    public final static String QUIT = "quit";
    public final static String CONTINUE = "continue";

    String isInterface;
    String isAbstractMethod;
    String methodNotExists;

    public String getIsInterface() {
        return isInterface;
    }

    public String getIsAbstractMethod() {
        return isAbstractMethod;
    }

    public String getMethodNotExists() {
        return methodNotExists;
    }

}
