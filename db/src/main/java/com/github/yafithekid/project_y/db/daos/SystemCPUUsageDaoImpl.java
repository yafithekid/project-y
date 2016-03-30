package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemCPUUsage;
import org.mongodb.morphia.Datastore;

import java.util.List;

public class SystemCPUUsageDaoImpl implements SystemCPUUsageDao {
    private Datastore datastore;

    public SystemCPUUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(SystemCPUUsage systemCPUUsage) {
        datastore.save(systemCPUUsage);
    }

    @Override
    public List<SystemCPUUsage> getWithinTimestamp(long start, long end) {
        return datastore.find(SystemCPUUsage.class)
                .field("timestamp").greaterThanOrEq(start)
                .field("timestamp").lessThanOrEq(end)
                .asList();
    }
}
