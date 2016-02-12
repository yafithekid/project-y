package com.yafithekid.instrumentation.collector;


import com.yafithekid.instrumentation.collector.models.*;
import com.yafithekid.instrumentation.collector.services.ProfilingWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

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
            if (code.equalsIgnoreCase("syscpu")){
                SystemCPUUsage systemCPUUsage = SystemCPUUsage.newInstance(data);
                mProfilingWriter.systemCPUUsage(systemCPUUsage);
            } else if (code.equalsIgnoreCase("sysmem")){
                SystemMemoryUsage systemMemoryUsage = SystemMemoryUsage.newInstance(data);
                mProfilingWriter.systemMemoryUsage(systemMemoryUsage);
            } else if (code.equalsIgnoreCase("metinv")){
                MethodCall methodCall = MethodCall.newInstance(data);
                mProfilingWriter.methodCall(methodCall);
            } else if (code.equalsIgnoreCase("appcpu")){
                AppCPUUsage appCPUUsage = AppCPUUsage.newInstance(data);
                mProfilingWriter.appCPUUsage(appCPUUsage);
            } else if (code.equalsIgnoreCase("appmem")){
                AppMemoryUsage appMemoryUsage = AppMemoryUsage.newInstance(data);
                mProfilingWriter.appMemoryUsage(appMemoryUsage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
