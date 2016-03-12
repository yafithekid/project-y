package com.github.yafithekid.project_y.agent.daemons;

import com.github.yafithekid.project_y.commons.config.ResourceMonitor;
import com.sun.management.OperatingSystemMXBean;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * Class for monitoring CPU and memory of current JVM
 */
public class HardwareDaemon extends Thread {
    final String mCollectorHost;
    final int mCollectorPort;
    final String mAppId;
    final String mSystemId;
    final long mResourceRateCollect;
    List<HardwareDaemonWriter> mHardwareWriters;

    public HardwareDaemon(Config config,List<HardwareDaemonWriter> hardwareDaemonWriters){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        mSystemId = config.getSystemId();
        mAppId = config.getAppId();
        ResourceMonitor rm = config.getResourceMonitor();
        mResourceRateCollect = rm.getCollectRateMillis();

        mHardwareWriters = hardwareDaemonWriters;
    }

    @Override
    public void run() {
        System.out.println("Starting daemon...");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //noinspection InfiniteLoopStatement
        while(true){
            long currTime = System.currentTimeMillis();
            MemoryUsage sysMemU = memoryMXBean.getHeapMemoryUsage();
            for (HardwareDaemonWriter writer : mHardwareWriters) {
                //TODO extract format
                writer.write((sysMemU.getMax() - sysMemU.getUsed())+"");
                writer.write(String.format("%s %s %s %d %d %d %d",
                        ProfilingPrefix.APP_MEMORY, mAppId, mSystemId, currTime,
                        sysMemU.getUsed(), sysMemU.getCommitted(), sysMemU.getMax()));
                writer.write(String.format("%s %s %s %d %f",
                        ProfilingPrefix.APP_CPU, mAppId, mSystemId, currTime,
                        operatingSystemMXBean.getProcessCpuLoad()));
                writer.write(String.format("%s %s %d %f",
                        ProfilingPrefix.SYSTEM_CPU, mSystemId, currTime,
                        operatingSystemMXBean.getSystemCpuLoad()));
                long used = operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize();
                writer.write(String.format("%s %s %d %d %d",
                        ProfilingPrefix.SYSTEM_MEMORY, mSystemId, currTime,
                        used, operatingSystemMXBean.getTotalPhysicalMemorySize()));
            }

            try {
                Thread.sleep(mResourceRateCollect);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
