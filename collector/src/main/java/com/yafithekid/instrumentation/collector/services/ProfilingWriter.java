package com.yafithekid.instrumentation.collector.services;

import com.yafithekid.instrumentation.collector.models.*;

/**
 * Interface for writes profiling result from agent
 */
public interface ProfilingWriter {
    void methodCall(MethodCall methodCall);
    void appCPUUsage(AppCPUUsage appCPUUsage);
    void systemCPUUsage(SystemCPUUsage systemCPUUsage);
    void appMemoryUsage(AppMemoryUsage appMemoryUsage);
    void systemMemoryUsage(SystemMemoryUsage systemMemoryUsage);
}
