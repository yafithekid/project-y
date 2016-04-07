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
    boolean debug;
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
        isFlushOutput = config.getAgentConfig().isFlushOutput();
        debug = config.getAgentConfig().isDebug();
    }

    public static Sender getInstance() throws IOException {
        if (instance == null) {
            Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
            instance = new Sender(config);
        }
        return instance;
    }

    @Override
    public void methodCall(String className, String methodName,long startTime, long endTime, long startMem,long endMem,String threadId,Object retVal) {
        String retClass = "Void";
        if (retVal != null){
            retClass = retVal.getClass().getName();
        }
        String data = jsonConstruct.constructMethodCall(className,methodName,startTime,endTime,Agent.getObjectSize(retVal),threadId,retClass);
//        String data = String.format("%s %s %s %d %d %d %d %s",
//                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId);
        data += BasicClassFileTransformer.SEPARATOR;
        send(data);
    }

    @Override
    public void reqHandlerMethodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId, String httpVerb, String url,Object retVal) {
        String retClass = "Void";
        if (retVal != null){
            retClass = retVal.getClass().getName();
        }
        String data = jsonConstruct.constructReqHandlerMethodCall(className,methodName,startTime,endTime,Agent.getObjectSize(retVal),threadId,httpVerb,url,retClass);
//        String data = String.format("%s %s %s %d %d %d %d %s %s %s",
//                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId,
//                httpVerb,url);
        data += BasicClassFileTransformer.SEPARATOR;
        send(data);
    }

    private void send(String data){
        try {
            mDataOutputStream.get().write(data);
            mDataOutputStream.get().newLine();
            if (isFlushOutput){
                mDataOutputStream.get().flush();
            }
            if (debug){
                System.out.println(data);
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
                    }
                };
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


}
