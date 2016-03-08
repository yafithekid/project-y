package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.config.CollectorConfig;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Sender {
    public static Sender instance;

    public final String mCollectorHost;
    public final int mCollectorPort;

    private Sender(String host,int port){
        this.mCollectorHost = host;
        this.mCollectorPort = port;
    }

    public static void initialize(CollectorConfig config){
        instance = new Sender(config.getHost(),config.getPort());
    }

    public static Sender get(){ return instance; }

    public void send(String data){
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
