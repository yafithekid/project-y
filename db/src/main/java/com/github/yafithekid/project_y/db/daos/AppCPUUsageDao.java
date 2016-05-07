package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.AppCPUUsage;

import java.util.List;

public interface AppCPUUsageDao {
    void save(AppCPUUsage appCPUUsage);

    public List<AppCPUUsage> getWithinTimestamp(long start, long end);
}
