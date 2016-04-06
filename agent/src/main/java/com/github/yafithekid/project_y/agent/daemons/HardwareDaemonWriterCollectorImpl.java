package com.github.yafithekid.project_y.agent.daemons;

import com.github.yafithekid.project_y.commons.config.Config;

import java.io.*;
import java.net.Socket;

public class HardwareDaemonWriterCollectorImpl implements HardwareDaemonWriter {
    static final char SEPARATOR = '#';
    String mCollectorHost;
    boolean flushOutput;
    int mCollectorPort;
//    private ThreadLocal<Socket> mSocket;
    private ThreadLocal<BufferedWriter> mDataOutputStream;

    public HardwareDaemonWriterCollectorImpl(Config config) throws IOException{
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        flushOutput = config.isFlushOutput();
//        mSocket = new ThreadLocal<Socket>(){
//            @Override
//            protected Socket initialValue() {
//                try {
//                    return new Socket(mCollectorHost,mCollectorPort);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        };
        Socket socket = new Socket(mCollectorHost,mCollectorPort);
        OutputStream out = socket.getOutputStream();
        mDataOutputStream = new ThreadLocal<BufferedWriter>(){
            @Override
            protected BufferedWriter initialValue() {
                return new BufferedWriter(new OutputStreamWriter(out));
            }
        };
    }

    @Override
    public void write(String data){
        OutputStream outToServer;
        try {
//            outToServer = mSocket.get().getOutputStream();
//            if (!(data).endsWith("\n")) { data += "\n"; }
//            DataOutputStream out = new java.io.DataOutputStream(outToServer);
//            System.out.println(data);
            mDataOutputStream.get().write(data+SEPARATOR);
            mDataOutputStream.get().newLine();
            if (flushOutput){
                mDataOutputStream.get().flush();
            }
//            out.writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
