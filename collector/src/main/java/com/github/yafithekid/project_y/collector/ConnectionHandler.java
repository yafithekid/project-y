package com.github.yafithekid.project_y.collector;


import com.github.yafithekid.project_y.collector.models.*;
import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.config.ProfilingPrefix;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private Socket mSocket;
    private ProfilingWriter mProfilingWriter;

    public ConnectionHandler(Socket socket, ProfilingWriter profilingWriter){
        this.mSocket = socket;
        this.mProfilingWriter = profilingWriter;
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
                mProfilingWriter.systemCPUUsage(systemCPUUsage);
            } else if (code.equalsIgnoreCase(ProfilingPrefix.SYSTEM_MEMORY)){
                SystemMemoryUsage systemMemoryUsage = SystemMemoryUsage.newInstance(data);
                mProfilingWriter.systemMemoryUsage(systemMemoryUsage);
            } else if (code.equalsIgnoreCase(ProfilingPrefix.METHOD_INVOCATION)){
                MethodCall methodCall = MethodCall.newInstance(data);
                mProfilingWriter.methodCall(methodCall);
            } else if (code.equalsIgnoreCase(ProfilingPrefix.APP_CPU)){
                AppCPUUsage appCPUUsage = AppCPUUsage.newInstance(data);
                mProfilingWriter.appCPUUsage(appCPUUsage);
            } else if (code.equalsIgnoreCase(ProfilingPrefix.APP_MEMORY)){
                AppMemoryUsage appMemoryUsage = AppMemoryUsage.newInstance(data);
                mProfilingWriter.appMemoryUsage(appMemoryUsage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
