package com.yafithekid.instrumentation.collector.services;

/**
 * Interface for writes profiling result from agent
 */
public interface ProfilingWriter {
    void methodCall(String classname,String methodname,long start,long end);

}
