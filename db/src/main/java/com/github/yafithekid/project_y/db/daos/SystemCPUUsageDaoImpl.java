package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemCPUUsage;
import org.mongodb.morphia.Datastore;

public class SystemCPUUsageDaoImpl implements SystemCPUUsageDao {
    Datastore datastore;

    public SystemCPUUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(SystemCPUUsage systemCPUUsage) {
        datastore.save(systemCPUUsage);
    }
}
