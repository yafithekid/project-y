package com.yafithekid.instrumentation.agent;

import com.sun.management.OperatingSystemMXBean;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.Socket;

/**
 * Class for monitoring CPU and memory of current JVM
 */
public class HardwareDaemon extends Thread {
    final String mCollectorHost;
    final int mCollectorPort;
    final String mAppId;
    final String mSystemId;

    public HardwareDaemon(String collectorHost, int collectorPort, String appId, String systemId){
        mCollectorHost = collectorHost;
        mCollectorPort = collectorPort;
        mSystemId = systemId;
        mAppId = appId;
    }

    @Override
    public void run() {
        System.out.println("Starting daemon...");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //noinspection InfiniteLoopStatement
        while(true){
            try {
                long currTime = System.currentTimeMillis();
                MemoryUsage sysMemU = memoryMXBean.getHeapMemoryUsage();
                //TODO extract format
                sendToCollector(String.format("%s %s %d %d %d %d",
                        ProfilingPrefix.SYSTEM_MEMORY,mSystemId,currTime,
                        sysMemU.getUsed(),sysMemU.getCommitted(),sysMemU.getMax()));
                sendToCollector(String.format("%s %s %s %d %f",
                        ProfilingPrefix.APP_CPU,mAppId,mSystemId,currTime,
                        operatingSystemMXBean.getProcessCpuLoad()));
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
