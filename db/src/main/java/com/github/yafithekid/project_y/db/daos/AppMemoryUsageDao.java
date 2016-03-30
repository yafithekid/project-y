package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.AppMemoryUsage;

public interface AppMemoryUsageDao {
    void save(AppMemoryUsage appMemoryUsage);
}
