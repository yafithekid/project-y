package com.github.yafithekid.project_y.db.services;

import com.github.yafithekid.project_y.db.models.AppCPUUsage;
import com.github.yafithekid.project_y.db.models.MemoryPool;
import com.github.yafithekid.project_y.db.models.MethodCall;
import com.github.yafithekid.project_y.db.models.RequestTime;

public interface DatabaseService {
    void save(AppCPUUsage appCPUUsage);

    void save(MemoryPool memoryPool);

    void save(MethodCall methodCall);

    void save(RequestTime requestTime);
}
