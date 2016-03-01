package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.collector.models.*;

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
