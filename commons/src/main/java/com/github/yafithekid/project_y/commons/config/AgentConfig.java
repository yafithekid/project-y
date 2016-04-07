package com.github.yafithekid.project_y.commons.config;

public class AgentConfig {
    private boolean debug;
    private boolean flushOutput;
    private AgentConfigErrorAction errorAction;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isFlushOutput() {
        return flushOutput;
    }

    @SuppressWarnings("unused")
    public void setFlushOutput(boolean flushOutput) {
        this.flushOutput = flushOutput;
    }

    public AgentConfigErrorAction getErrorAction() {
        return errorAction;
    }

    public void setErrorAction(AgentConfigErrorAction errorAction) {
        this.errorAction = errorAction;
    }
}
