package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriter;

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
            Thread t = new DatagramConnectionHandler(new String(receivePacket.getData()),mProfilingWriters);
            t.start();
        }
    }
}
