package com.yafithekid.instrumentation.config;

import java.util.ArrayList;
import java.util.List;

public class MonitoredMethod {
    private String name;
    private boolean trace;

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
}
