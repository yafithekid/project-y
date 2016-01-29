package com.yafithekid.instrumentation.agent;

import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new BasicClassFileTransformer("127.0.0.1",9000));
        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println("Starting daemon...");
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                while(true){
                    System.out.println("memory bean = "+memoryMXBean.getHeapMemoryUsage());
                    System.out.println("memory usage = "+Runtime.getRuntime().freeMemory());
                    System.out.println("operating system = "+operatingSystemMXBean.getProcessCpuLoad());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }



}
