package com.yafithekid.instrumentation.agent.configs;


import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents data in the config file
 */
public class Config {
    private List<MonitoredClass> classes;

    public Config(){
        this.classes = new ArrayList<MonitoredClass>();
    }

    /**
     * Add a class to be monitored. The monitored class to be configured will be given from MonitoredClass.
     * @param mc
     */
    public void addMonitoredClass(MonitoredClass mc){
        classes.add(mc);
    }

    public List<MonitoredClass> getClasses() {
        return classes;
    }

    public void setClasses(List<MonitoredClass> classes) {
        this.classes = classes;
    }

    //TODO erase
    public static void main(String []args) throws IOException {
        ObjectMapper om = new ObjectMapper();
        System.out.println(om.writeValueAsString(createDummy()));
    }

    //TODO erase
    //intention to test monitored method
    public static Config createDummy(){
        Config config = new Config();

        //monitor randomsleep and biglist, but not the hello world.
        MonitoredClass monitoredClass = new MonitoredClass("com.yafithekid.instrumentation.example.Sleeping");
        MonitoredMethod method = new MonitoredMethod("randomSleep").memory().time();
        monitoredClass.addMonitoredMethod(method);
        monitoredClass.addMonitoredMethod((new MonitoredMethod("bigList")).memory());
        config.addMonitoredClass(monitoredClass);
        return config;
    }
}
