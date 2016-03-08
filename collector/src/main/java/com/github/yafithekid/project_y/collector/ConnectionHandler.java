package com.github.yafithekid.project_y.collector;


import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;
import com.github.yafithekid.project_y.commons.dbs.models.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler extends Thread{
    private Socket mSocket;
    private List<ProfilingWriter> mProfilingWriters;

    public ConnectionHandler(Socket socket, List<ProfilingWriter> profilingWriters){
        this.mSocket = socket;
        this.mProfilingWriters = profilingWriters;
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
            String data = buffer.toString();
            String code = buffer.substring(0,6);
            if (code.equalsIgnoreCase(ProfilingPrefix.SYSTEM_CPU)){
                SystemCPUUsage systemCPUUsage = SystemCPUUsage.newInstance(data);
                for(ProfilingWriter profilingWriter: mProfilingWriters){
                    profilingWriter.systemCPUUsage(systemCPUUsage);
                }
            } else if (code.equalsIgnoreCase(ProfilingPrefix.SYSTEM_MEMORY)){
                SystemMemoryUsage systemMemoryUsage = SystemMemoryUsage.newInstance(data);
                for(ProfilingWriter profilingWriter: mProfilingWriters) {
                    profilingWriter.systemMemoryUsage(systemMemoryUsage);
                }
            } else if (code.equalsIgnoreCase(ProfilingPrefix.METHOD_INVOCATION)){
                MethodCall methodCall = MethodCall.newInstance(data);
                for(ProfilingWriter profilingWriter: mProfilingWriters){
                    profilingWriter.methodCall(methodCall);
                }
            } else if (code.equalsIgnoreCase(ProfilingPrefix.APP_CPU)){
                AppCPUUsage appCPUUsage = AppCPUUsage.newInstance(data);
                for(ProfilingWriter profilingWriter: mProfilingWriters){
                    profilingWriter.appCPUUsage(appCPUUsage);
                }
            } else if (code.equalsIgnoreCase(ProfilingPrefix.APP_MEMORY)){
                AppMemoryUsage appMemoryUsage = AppMemoryUsage.newInstance(data);
                for(ProfilingWriter profilingWriter: mProfilingWriters){
                    profilingWriter.appMemoryUsage(appMemoryUsage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
