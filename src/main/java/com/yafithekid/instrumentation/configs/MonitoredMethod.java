package com.yafithekid.instrumentation.configs;

import java.util.ArrayList;
import java.util.List;

public class MonitoredMethod {
    public static final String MONITOR_TIME = "time";
    public static final String MONITOR_MEMORY = "memory";

    private String name;
    private List<String> monitor;

    public MonitoredMethod(String name){
        this.name = name;
        monitor = new ArrayList<>();
    }

    public MonitoredMethod time(){
        monitor.add(MONITOR_TIME);
        return this;
    }

    public MonitoredMethod memory(){
        monitor.add(MONITOR_MEMORY);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMonitor() {
        return monitor;
    }

    public void setMonitor(List<String> monitor) {
        this.monitor = monitor;
    }
}
