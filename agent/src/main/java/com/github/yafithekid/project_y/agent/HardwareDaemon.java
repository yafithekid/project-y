package com.github.yafithekid.project_y.agent;

import com.sun.management.OperatingSystemMXBean;
import com.github.yafithekid.project_y.config.Config;
import com.github.yafithekid.project_y.config.ProfilingPrefix;

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

    public HardwareDaemon(Config config){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        mSystemId = config.getSystemId();
        mAppId = config.getAppId();
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
                sendToCollector(String.format("%s %s %s %d %d %d %d",
                        ProfilingPrefix.APP_MEMORY,mAppId,mSystemId,currTime,
                        sysMemU.getUsed(),sysMemU.getCommitted(),sysMemU.getMax()));
                sendToCollector(String.format("%s %s %s %d %f",
                        ProfilingPrefix.APP_CPU,mAppId,mSystemId,currTime,
                        operatingSystemMXBean.getProcessCpuLoad()));
                sendToCollector(String.format("%s %s %d %f",
                        ProfilingPrefix.SYSTEM_CPU,mSystemId,currTime,
                        operatingSystemMXBean.getSystemCpuLoad()));
                long used = operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize();
                sendToCollector(String.format("%s %s %d %d %d",
                        ProfilingPrefix.SYSTEM_MEMORY,mSystemId,currTime,
                        used,operatingSystemMXBean.getTotalPhysicalMemorySize()));
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
        Sender.get().send(data);
    }


}
