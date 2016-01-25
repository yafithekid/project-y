package com.yafithekid.instrumentation.agent.configs;

import java.util.ArrayList;
import java.util.List;

public class MonitoredMethod {
    public static final String MONITOR_TIME = "time";
    public static final String MONITOR_MEMORY = "memory";

    private String name;
    private List<String> monitor;

    public MonitoredMethod(String name){
        this.name = name;
        monitor = new ArrayList<String>();
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

    public boolean monitorRuntime(){
        return monitor.contains(MONITOR_TIME);
    }

    public boolean monitorMemory(){
        return monitor.contains(MONITOR_MEMORY);
    }
}
