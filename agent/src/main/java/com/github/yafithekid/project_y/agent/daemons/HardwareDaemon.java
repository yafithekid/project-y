package com.github.yafithekid.project_y.agent.daemons;

import com.github.yafithekid.project_y.commons.JsonConstruct;
import com.github.yafithekid.project_y.commons.config.ResourceMonitor;
import com.github.yafithekid.project_y.commons.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.ProfilingPrefix;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for monitoring CPU and memory of current JVM
 */
public class HardwareDaemon extends Thread {
    final String mCollectorHost;
    final int mCollectorPort;
    final long mResourceRateCollect;
    JsonConstruct jsonConstruct = new JsonConstruct();
    List<HardwareDaemonWriter> mHardwareWriters;

    public HardwareDaemon(Config config,List<HardwareDaemonWriter> hardwareDaemonWriters){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
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
            List<MemoryPoolMXBean> memoryMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (HardwareDaemonWriter writer : mHardwareWriters) {
                for (MemoryPoolMXBean mxBean : memoryMXBeans) {
                    MemoryUsage usage = mxBean.getUsage();
                    if (usage != null){
                        writer.write(jsonConstruct.constructMemoryPool(currTime,mxBean.getName(),
                                usage.getUsed(),usage.getCommitted(),usage.getMax()));
                    }
                }
                MemoryUsage sysMemU = memoryMXBean.getHeapMemoryUsage();
                writer.write(jsonConstruct.constructAppMemoryUsage(currTime,
                        sysMemU.getUsed(), sysMemU.getCommitted(), sysMemU.getMax()));
                writer.write(jsonConstruct.constructAppCpuUsage(currTime,
                        operatingSystemMXBean.getProcessCpuLoad()));

                operatingSystemMXBean.getCommittedVirtualMemorySize();
                long used = operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize();
                writer.write(jsonConstruct.constructSystemMemoryUsage(currTime,used,operatingSystemMXBean.getTotalPhysicalMemorySize()));
                writer.write(jsonConstruct.constructSystemCpuUsage(currTime,
                        operatingSystemMXBean.getSystemCpuLoad()));
            }

            try {
                Thread.sleep(mResourceRateCollect);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
