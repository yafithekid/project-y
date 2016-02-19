package com.yafithekid.instrumentation.config;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitoredClassSearchMap {
    private Map<String,MonitoredClass> mMap;

    public MonitoredClassSearchMap(List<MonitoredClass> classes){
        mMap = new HashMap<String, MonitoredClass>();
        for(MonitoredClass mc: classes){
            mMap.put(mc.getName(),mc);
        }
    }

    public MonitoredClass get(String name){
        return mMap.get(name);
    }

    public boolean exist(String name){
        return get(name) != null;
    }
}
