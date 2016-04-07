package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemCPUUsage;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.List;

public class SystemCPUUsageDaoImpl implements SystemCPUUsageDao {
    private Datastore datastore;

    public SystemCPUUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(SystemCPUUsage systemCPUUsage) {
        datastore.save(systemCPUUsage, WriteConcern.UNACKNOWLEDGED);
    }

    @Override
    public List<SystemCPUUsage> getWithinTimestamp(long start, long end) {
        return datastore.find(SystemCPUUsage.class)
                .field("timestamp").greaterThanOrEq(start)
                .field("timestamp").lessThanOrEq(end)
                .asList();
    }

    @Override
    public List<SystemCPUUsage> getNearTimestamp(long start, long end) {
        SystemCPUUsage lowerbound = datastore.find(SystemCPUUsage.class)
                .field("timestamp")
                .lessThanOrEq(start)
                .order("-timestamp")
                .limit(1).get();
        SystemCPUUsage upperbound = datastore.find(SystemCPUUsage.class)
                .field("timestamp")
                .greaterThanOrEq("timestamp")
                .order("timestamp")
                .limit(1).get();
        long lowerTs = (lowerbound == null)?start:lowerbound.getTimestamp();
        long upperTs = (upperbound == null)?end:upperbound.getTimestamp();
        return datastore.find(SystemCPUUsage.class)
                .field("timestamp").greaterThanOrEq(lowerTs)
                .field("timestamp").lessThanOrEq(upperTs)
                .order("timestamp").asList();
    }
}
