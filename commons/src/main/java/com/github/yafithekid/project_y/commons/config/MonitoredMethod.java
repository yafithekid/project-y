package com.github.yafithekid.project_y.commons.config;

import java.util.ArrayList;
import java.util.List;

public class MonitoredMethod {
    private String name;
    private boolean trace;
    private boolean requestHandler;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public boolean isRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(boolean requestHandler) {
        this.requestHandler = requestHandler;
    }
}
