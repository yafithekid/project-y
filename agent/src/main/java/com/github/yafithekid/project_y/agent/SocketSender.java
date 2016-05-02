package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.JsonConstruct;
import com.github.yafithekid.project_y.commons.config.Config;

import java.io.*;
import java.net.Socket;

public class SocketSender extends SenderTrait implements SendToCollector {
    public final String mCollectorHost;
    public final int mCollectorPort;
    boolean isFlushOutput;
    boolean debug;

    private static ThreadLocal<BufferedWriter> mDataOutputStream;

    public SocketSender(Config config) throws IOException {
        super(new JsonConstruct());
        this.mCollectorHost = config.getCollector().getHost();
        this.mCollectorPort = config.getCollector().getPort();
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

    @Override
    void send(String data){
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
