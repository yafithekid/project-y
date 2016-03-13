package com.github.yafithekid.project_y.collector;


import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;
import com.github.yafithekid.project_y.commons.gson.Gson;
import com.github.yafithekid.project_y.commons.gson.reflect.TypeToken;
import com.github.yafithekid.project_y.db.models.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ConnectionHandler extends Thread{
    private Socket mSocket;
    private List<ProfilingWriter> mProfilingWriters;
    private Gson mGson;

    public ConnectionHandler(Socket socket, List<ProfilingWriter> profilingWriters){
        this.mSocket = socket;
        this.mProfilingWriters = profilingWriters;
        mGson = new Gson();
    }

    @Override
    public void run(){
        try {
            DataInputStream is = new DataInputStream(mSocket.getInputStream());
            StringBuffer buffer = new StringBuffer();
            String sockInput;
            boolean stop = false;
            while (!stop){
                sockInput = is.readUTF();
                buffer.append(sockInput);
                if (sockInput.endsWith("\n")){
                    stop = true;
                }
            }
            try {
                String data = buffer.toString();
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
            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
