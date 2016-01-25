package com.yafithekid.instrumentation.agent.configs;


import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private List<MonitoredClass> classes;

    public Config(){
        this.classes = new ArrayList<MonitoredClass>();
    }
    public void addMonitoredClass(MonitoredClass mc){
        classes.add(mc);
    }

    public List<MonitoredClass> getClasses() {
        return classes;
    }

    public void setClasses(List<MonitoredClass> classes) {
        this.classes = classes;
    }

    public static void main(String []args) throws IOException {
        ObjectMapper om = new ObjectMapper();
        System.out.println(om.writeValueAsString(createDummy()));
    }

    public static Config createDummy(){
        Config config = new Config();

        MonitoredClass monitoredClass = new MonitoredClass("com.yafithekid.instrumentation.agent.Sleeping");
        MonitoredMethod method = new MonitoredMethod("randomSleep").memory().time();
        monitoredClass.addMonitoredMethod(method);
        monitoredClass.addMonitoredMethod(new MonitoredMethod("bigList"));
        config.addMonitoredClass(monitoredClass);
        return config;
    }
}
