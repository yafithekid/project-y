package com.github.yafithekid.project_y.commons;

import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;
import com.github.yafithekid.project_y.commons.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class JsonConstruct {
    static Gson gson = new Gson();

    public String constructMethodCall(String clazz,String method,long startTime,long endTime,
                                      long memory,String invocationId,String returnClass) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("_prefix", ProfilingPrefix.METHOD_INVOCATION);
        map.put("clazz",clazz);
        map.put("method",method);
        map.put("start",""+startTime);
        map.put("end",""+endTime);
        map.put("memory",""+memory);
        map.put("invocationId",invocationId);
        map.put("retClass",returnClass);
        return gson.toJson(map);
    }

    public String constructReqHandlerMethodCall(String clazz,String method,long startTime,long endTime,
                                                long memory,String invocationId,String reqMethod,String url,String returnClass) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("_prefix",ProfilingPrefix.METHOD_INVOCATION);
        map.put("clazz",clazz);
        map.put("method",method);
        map.put("start",""+startTime);
        map.put("end",""+endTime);
        map.put("memory",""+memory);
        map.put("invocationId",invocationId);
        map.put("reqMethod",reqMethod);
        map.put("url",url);
        map.put("retClass",returnClass);
        return gson.toJson(map);
    }

    public String constructAppMemoryUsage(long timestamp,long used,long commited,long max,String type){
        Map<String,String> map = new HashMap<String, String>();
        map.put("_prefix",ProfilingPrefix.APP_MEMORY);
        map.put("timestamp",timestamp+"");
        map.put("used",used+"");
        map.put("commited",commited+"");
        map.put("max",max+"");
        map.put("type",type);
        return gson.toJson(map);
    }

    public String constructMemoryPool(long timestamp,String name,long used,long commited,long max,String type){
        Map<String,String> map = new HashMap<String,String>();
        map.put("_prefix",ProfilingPrefix.MEMORY_SPACE);
        map.put("timestamp",""+timestamp);
        map.put("name",name);
        map.put("used",""+used);
        map.put("commited",""+commited);
        map.put("max",""+max);
        map.put("type",type);
        return gson.toJson(map);
    }

    public String constructAppCpuUsage(long timestamp,double load){
        Map<String,String> map = new HashMap<String, String>();
        map.put("_prefix",ProfilingPrefix.APP_CPU);
        map.put("timestamp",timestamp+"");
        map.put("load",load+"");
        return gson.toJson(map);
    }

    public String constructSystemMemoryUsage(long timestamp,long used,long max){
        Map<String,String> map = new HashMap<String, String>();
        map.put("_prefix",ProfilingPrefix.SYSTEM_MEMORY);
        map.put("timestamp",""+timestamp);
        map.put("used",""+used);
        map.put("max",""+max);
        return gson.toJson(map);
    }

    public String constructSystemCpuUsage(long timestamp,double load){
        Map<String,String> map = new HashMap<String, String>();
        map.put("_prefix",ProfilingPrefix.SYSTEM_CPU);
        map.put("timestamp",""+timestamp);
        map.put("load",""+load);
        return gson.toJson(map);
    }
}

