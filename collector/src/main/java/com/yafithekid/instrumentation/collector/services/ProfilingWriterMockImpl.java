package com.yafithekid.instrumentation.collector.services;

import com.yafithekid.instrumentation.collector.models.*;

import java.util.Date;

public class ProfilingWriterMockImpl implements ProfilingWriter {
    @Override
    public void methodCall(MethodCall methodCall) {
        System.out.println(String.format("%s: %s %s %s %s","[method call]",methodCall.getClazz(),
                methodCall.getMethod(),new Date(methodCall.getStart()).toString(),
                new Date(methodCall.getEnd()).toString()));
    }

    @Override
    public void appCPUUsage(AppCPUUsage appCPUUsage) {
        System.out.println(String.format("%s: %s %s %s %.2f","[app cpu]",appCPUUsage.getAppId(),
                appCPUUsage.getSystemId(),new Date(appCPUUsage.getTimestamp()).toString(),
                appCPUUsage.getLoad()));
    }

    @Override
    public void systemCPUUsage(SystemCPUUsage systemCPUUsage) {
        System.out.println(String.format("%s: %s %s %.2f","[system cpu]",systemCPUUsage.getSystemId(),
                new Date(systemCPUUsage.getTimestamp()).toString(),systemCPUUsage.getLoad()));
    }

    @Override
    public void appMemoryUsage(AppMemoryUsage appMemoryUsage) {
        System.out.println(String.format("%s: %s %s %s %d %d %d","[app memory]",appMemoryUsage.getAppId(),
                appMemoryUsage.getSystemId(),new Date(appMemoryUsage.getTimestamp()).toString(),appMemoryUsage.getUsed(),
                appMemoryUsage.getCommited(),appMemoryUsage.getMax()));
    }

    @Override
    public void systemMemoryUsage(SystemMemoryUsage systemMemoryUsage) {
        System.out.println(String.format("%s: %s %s %d %d","[sys memory]",systemMemoryUsage.getSystemId(),
                new Date(systemMemoryUsage.getTimestamp()).toString(),systemMemoryUsage.getUsed(),systemMemoryUsage.getMax()));
    }
}
