package com.github.yafithekid.project_y.agent.daemons;

import com.github.yafithekid.project_y.commons.config.Config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HardwareDaemonWriterCollectorImpl implements HardwareDaemonWriter {
    String mCollectorHost;
    int mCollectorPort;

    public HardwareDaemonWriterCollectorImpl(Config config){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
    }

    @Override
    public void write(String data){
        try {
            Socket client = new Socket(mCollectorHost,mCollectorPort);
            OutputStream outToServer = client.getOutputStream();
            if (!(data).endsWith("\n")) { data += "\n"; }
            DataOutputStream out = new java.io.DataOutputStream(outToServer);
//            System.out.println(data);
            out.writeUTF(data);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
