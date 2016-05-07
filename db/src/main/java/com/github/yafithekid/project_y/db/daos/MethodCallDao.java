package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.MethodCall;

import java.util.List;

public interface MethodCallDao {
    void save(MethodCall methodCall);

    /**
     * Get longest http request that invoked between start and end timestamp
     * @return lis of method call
     */
    List<MethodCall> getLongestHTTPRequest(long start,long end);

    /**
     * Get most consuming memory http request that invoked between start and end timstamp
     * @return list of method call
     */
    List<MethodCall> getMostConsumingMemoryHTTPRequest(long start, long end);

    /**
     * Get all methods that called by method with the specified id value
     * @param id
     * @return
     */
    List<MethodCall> getMethodsInvokedByThisId(String id);

    /**
     * Get all http request that has undefined max memory in its field
     * @return list of method call
     * @param startTimestamp
     * @param endTimestamp
     */
    List<MethodCall> getUndefinedMaxMemoryHTTPRequest(long startTimestamp, long endTimestamp);

    List<MethodCall> getUndefinedCPUUsageHTTPRequest(long startTimestamp, long endTimestamp);

    MethodCall findMethodById(String id);
}
