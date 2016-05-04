package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.db.models.SystemCPUUsage;

import java.io.IOException;
import java.net.*;
import java.util.List;
public class DatagramCollector implements Collector{
    DatagramSocket mServerSocket;
    List<ProfilingWriter> mProfilingWriters;
    public static final int DEFAULT_PORT = 9000;

    public DatagramCollector(int port,List<ProfilingWriter> profilingWriters) throws IOException {
        mServerSocket = new DatagramSocket(port);
        mProfilingWriters = profilingWriters;
    }

    @Override
    public void run(){
        while (true){
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
            try {
                mServerSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = new String(receivePacket.getData());
            if (s.isEmpty()) continue;
            s = s.substring(0,s.indexOf("#"));
            Thread t = new DatagramConnectionHandler(s,mProfilingWriters);
            t.start();
        }
    }
}
