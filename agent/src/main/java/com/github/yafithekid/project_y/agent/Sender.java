package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.config.CollectorConfig;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Sender implements SendToCollector {
    public static Sender instance;

    public final String mCollectorHost;
    public final int mCollectorPort;

    private Sender(Config config){
        this.mCollectorHost = config.getCollector().getHost();
        this.mCollectorPort = config.getCollector().getPort();
    }

    public static Sender getInstance() throws FileNotFoundException {
        if (instance == null) {
            Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
            instance = new Sender(config);
        }
        return instance;
    }

    @Override
    public void methodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId) {
        String data = String.format("%s %s %s %d %d %d %d %s",
                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId);
        sendToCollector(data);
    }

    @Override
    public void reqHandlerMethodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId, String httpVerb, String url) {
        String data = String.format("%s %s %s %d %d %d %d %s %s %s",
                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId,
                httpVerb,url);
        sendToCollector(data);
    }

    private void sendToCollector(String data){
        try {
            Socket client = new Socket(mCollectorHost,mCollectorPort);
            OutputStream outToServer = client.getOutputStream();
            if (!(data).endsWith("\n")) { data += "\n"; }
            DataOutputStream out = new java.io.DataOutputStream(outToServer);
            System.out.println(data);
            out.writeUTF(data);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
