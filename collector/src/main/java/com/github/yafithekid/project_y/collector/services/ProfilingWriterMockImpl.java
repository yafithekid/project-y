package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.db.models.*;

import java.util.Date;

public class ProfilingWriterMockImpl implements ProfilingWriter {
    @Override
    public void methodCall(MethodCall methodCall) {
//        System.out.println(String.format("%s: %s %s %s %s %s %s %s","[method call]",methodCall.getClazz(),
//                methodCall.getMethod(),methodCall.getInvocationId(),new Date(methodCall.getStart()).toString(),
//                new Date(methodCall.getEnd()).toString(),methodCall.getFreeMemStart(),methodCall.getFreeMemEnd()));
        System.out.println(methodCall.toString());
    }

    @Override
    public void memoryPool(MemoryPool memoryPool) {
        System.out.println(String.format("[%s]: %d %s %d %d %d","mem pool",memoryPool.getTimestamp(),memoryPool.getName(),
                memoryPool.getUsed(),memoryPool.getCommited(),memoryPool.getMax()));
    }

    @Override
    public void appCPUUsage(AppCPUUsage appCPUUsage) {
        System.out.println(String.format("%s: %s %.2f","[app cpu]",new Date(appCPUUsage.getTimestamp()).toString(),
                appCPUUsage.getLoad()));
    }

    @Override
    public void systemCPUUsage(SystemCPUUsage systemCPUUsage) {
        System.out.println(String.format("%s: %s %.2f","[system cpu]",
                new Date(systemCPUUsage.getTimestamp()).toString(),systemCPUUsage.getLoad()));
    }

    @Override
    public void appMemoryUsage(AppMemoryUsage appMemoryUsage) {
        System.out.println(String.format("%s: %s %d %d %d","[app memory]",new Date(appMemoryUsage.getTimestamp()).toString(),appMemoryUsage.getUsed(),
                appMemoryUsage.getCommited(),appMemoryUsage.getMax()));
    }

    @Override
    public void systemMemoryUsage(SystemMemoryUsage systemMemoryUsage) {
        System.out.println(String.format("%s: %s %d %d","[sys memory]",
                new Date(systemMemoryUsage.getTimestamp()).toString(),systemMemoryUsage.getUsed(),systemMemoryUsage.getMax()));
    }
}
