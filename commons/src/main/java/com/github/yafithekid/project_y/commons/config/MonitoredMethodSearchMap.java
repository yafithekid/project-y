package com.github.yafithekid.project_y.commons.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Search map for quick access of method invocation configuration
 */
public class MonitoredMethodSearchMap {
    private Map<String,MonitoredMethod> map;

    public MonitoredMethodSearchMap(List<MonitoredClass> classes){
        map = new HashMap<String, MonitoredMethod>();
        for(MonitoredClass mc:classes){
            for(MonitoredMethod mm:mc.getMethods()){
                add(mc.getName(),mm.getName(),mm);
            }
        }
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
