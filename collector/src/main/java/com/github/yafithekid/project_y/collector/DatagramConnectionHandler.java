package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;
import com.github.yafithekid.project_y.commons.gson.Gson;
import com.github.yafithekid.project_y.commons.gson.reflect.TypeToken;
import com.github.yafithekid.project_y.db.models.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DatagramConnectionHandler extends Thread {
    private List<ProfilingWriter> mProfilingWriters;
    private String data;
    private Gson mGson;

    public DatagramConnectionHandler(String data,List<ProfilingWriter> profilingWriters){
        this.data = data;
        this.mProfilingWriters = profilingWriters;
    }

    @Override
    public void run() {
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Map<String,String> map = mGson.fromJson(data,type);
        String prefix = map.get("_prefix");
        if (prefix == null) prefix = "";
        if (prefix.equalsIgnoreCase(ProfilingPrefix.MEMORY_SPACE)){
            MemoryPool memoryPool = MemoryPool.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters){
                profilingWriter.memoryPool(memoryPool);
            }
        } else if (prefix.equalsIgnoreCase(ProfilingPrefix.SYSTEM_CPU)){
            SystemCPUUsage systemCPUUsage = SystemCPUUsage.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters){
                profilingWriter.systemCPUUsage(systemCPUUsage);
            }
        } else if (prefix.equalsIgnoreCase(ProfilingPrefix.SYSTEM_MEMORY)){
            SystemMemoryUsage systemMemoryUsage = SystemMemoryUsage.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters) {
                profilingWriter.systemMemoryUsage(systemMemoryUsage);
            }
        } else if (prefix.equalsIgnoreCase(ProfilingPrefix.METHOD_INVOCATION)){
            MethodCall methodCall = MethodCall.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters){
                profilingWriter.methodCall(methodCall);
            }
        } else if (prefix.equalsIgnoreCase(ProfilingPrefix.APP_CPU)){
            AppCPUUsage appCPUUsage = AppCPUUsage.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters){
                profilingWriter.appCPUUsage(appCPUUsage);
            }
        } else if (prefix.equalsIgnoreCase(ProfilingPrefix.APP_MEMORY)){
            AppMemoryUsage appMemoryUsage = AppMemoryUsage.newInstance(map);
            for(ProfilingWriter profilingWriter: mProfilingWriters){
                profilingWriter.appMemoryUsage(appMemoryUsage);
            }
        } else {
            System.out.println("unknown: "+data);
        }
    }
}
