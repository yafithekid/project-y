package com.yafithekid.instrumentation.collector.services;

/**
 * Interface for writes profiling result from agent
 */
public interface ProfilingWriter {
    void methodCall(String classname,String methodname,long start,long end);
    void appCPUUsage(String appId, String systemId, long timestamp, double load);
    void systemCPUUsage(String systemId,long timestamp,double load);
    void appMemoryUsage(String appId, String systemId, long timestamp, long used, long commited, long max);
}
