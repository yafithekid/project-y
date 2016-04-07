package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemCPUUsage;

import java.util.List;

public interface SystemCPUUsageDao {
    void save(SystemCPUUsage systemCPUUsage);

    /**
     * Get system cpu usage that start <= timestamp <= end
     * @param start start timestamp
     * @param end end timestamp
     * @return list of systemcpuusage
     */
    List<SystemCPUUsage> getWithinTimestamp(long start, long end);

    /**
     * Get system cpu usage that timestampA <= start, end <= timestampB
     * timestampA is the biggest possible value, and timestampB is the lowest possible value
     * @param start start timestamp
     * @param end end timestamp
     * @return list of systemcpuusage
     */
    List<SystemCPUUsage> getNearTimestamp(long start,long end);
}
