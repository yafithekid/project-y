package com.yafithekid.instrumentation.agent;

import com.sun.management.OperatingSystemMXBean;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.Socket;

/**
 * Class for monitoring CPU and memory of current JVM
 */
public class HardwareDaemon extends Thread {
    final String mCollectorHost;
    final int mCollectorPort;

    public HardwareDaemon(String collectorHost,int collectorPort){
        mCollectorHost = collectorHost;
        mCollectorPort = collectorPort;
    }

    @Override
    public void run() {
        System.out.println("Starting daemon...");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //noinspection InfiniteLoopStatement
        while(true){
            try {
                //TODO can change to getCommited, getMax etc
                sendToCollector(memoryMXBean.getHeapMemoryUsage().toString());
                sendToCollector(String.valueOf(operatingSystemMXBean.getProcessCpuLoad()));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void sendToCollector(String data) throws IOException {
        //TODO formatting
        Socket client = new Socket(mCollectorHost,mCollectorPort);
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new java.io.DataOutputStream(outToServer);
        if (!data.endsWith("\n")) data = data + "\n";
        out.writeUTF(data);
        client.close();
    }


}
