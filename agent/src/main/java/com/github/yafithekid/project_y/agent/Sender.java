package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.JsonConstruct;
import com.github.yafithekid.project_y.commons.config.Config;

import java.io.*;
import java.net.Socket;

public class Sender implements SendToCollector {
    public static String classLoaderName = Sender.class.getClassLoader().getClass().getName();
    public static Sender instance;

    public final String mCollectorHost;
    public final int mCollectorPort;
    boolean isFlushOutput;
    JsonConstruct jsonConstruct;

    private static ThreadLocal<BufferedWriter> mDataOutputStream;

    private Sender(Config config) throws IOException {
        this.mCollectorHost = config.getCollector().getHost();
        this.mCollectorPort = config.getCollector().getPort();
        jsonConstruct = new JsonConstruct();
        Socket socket = new Socket(mCollectorHost,mCollectorPort);
        OutputStream outputStream = socket.getOutputStream();
        mDataOutputStream = new ThreadLocal<BufferedWriter>(){
            @Override
            protected BufferedWriter initialValue() {
                return new BufferedWriter(new OutputStreamWriter(outputStream));
            };
        };
        isFlushOutput = config.isFlushOutput();
    }

    public static Sender getInstance() throws IOException {
        if (instance == null) {
            Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
            instance = new Sender(config);
        }
        return instance;
    }

    @Override
    public void methodCall(String className, String methodName,String startTime, String endTime, String startMem,String endMem,String threadId) {
        String data = jsonConstruct.constructMethodCall(className,methodName,Long.parseLong(startTime),Long.parseLong(endTime),Long.parseLong(startMem),Long.parseLong(endMem),threadId);
//        String data = String.format("%s %s %s %d %d %d %d %s",
//                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId);
        send(data);
    }

    @Override
    public void reqHandlerMethodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId, String httpVerb, String url) {
        String data = jsonConstruct.constructReqHandlerMethodCall(className,methodName,startTime,endTime,startMem,endMem,threadId,httpVerb,url);
//        String data = String.format("%s %s %s %d %d %d %d %s %s %s",
//                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId,
//                httpVerb,url);
        send(data);
    }

    public void send(String data){
        try {
            mDataOutputStream.get().write(data);
            mDataOutputStream.get().newLine();
            if (isFlushOutput){
                mDataOutputStream.get().flush();
            }
        } catch (IOException e) {
            //try to reconnect. if failed then just throw exception
            try {
                Socket socket = new Socket(mCollectorHost,mCollectorPort);
                OutputStream outputStream = socket.getOutputStream();
                mDataOutputStream = new ThreadLocal<BufferedWriter>(){
                    @Override
                    protected BufferedWriter initialValue() {
                        return new BufferedWriter(new OutputStreamWriter(outputStream));
                    };
                };
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


}
