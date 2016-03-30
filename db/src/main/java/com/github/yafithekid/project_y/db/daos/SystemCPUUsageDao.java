package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemCPUUsage;

import java.util.List;

public interface SystemCPUUsageDao {
    void save(SystemCPUUsage systemCPUUsage);

    List<SystemCPUUsage> getWithinTimestamp(long start, long end);
}
