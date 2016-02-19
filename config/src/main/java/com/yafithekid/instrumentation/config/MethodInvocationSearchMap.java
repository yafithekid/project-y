package com.yafithekid.instrumentation.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Search map for quick access of method invocation configuration
 */
public class MethodInvocationSearchMap {
    private Map<String,MonitoredMethod> map;

    public MethodInvocationSearchMap(Config config){
        List<MonitoredClass> classes = config.getClasses();
        map = new HashMap<String, MonitoredMethod>();
    }

    public void add(String clazz,String method,MonitoredMethod monitoredMethod){
        map.put(createKey(clazz,method),monitoredMethod);
    }

    public MonitoredMethod get(String clazz,String method){
        return map.get(createKey(clazz,method));
    }

    public boolean exist(String clazz,String method){
        return get(clazz,method) != null;
    }

    public String createKey(String clazz,String method){
        return clazz+"#"+method;
    }


}
