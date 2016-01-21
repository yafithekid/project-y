package com.yafithekid.instrumentation.agent.configs;

import java.util.ArrayList;
import java.util.List;

public class MonitoredClass {
    private String name;
    private List<MonitoredMethod> methods;

    public MonitoredClass(String name){
        this.name = name;
        methods = new ArrayList<>();
    }

    public void addMonitoredMethod(MonitoredMethod mm){
        methods.add(mm);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MonitoredMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<MonitoredMethod> methods) {
        this.methods = methods;
    }
}
