package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.AppCPUUsage;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;

import java.util.List;

public class AppCPUUsageDaoImpl implements AppCPUUsageDao {
    Datastore datastore;

    public AppCPUUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(AppCPUUsage appCPUUsage) {
        datastore.save(appCPUUsage, WriteConcern.UNACKNOWLEDGED);
    }

    @Override
    public List<AppCPUUsage> getWithinTimestamp(long startTimestamp, long endTimestamp) {
        return datastore.find(AppCPUUsage.class)
                .field("timestamp").lessThanOrEq(endTimestamp)
                .field("timestamp").greaterThanOrEq(startTimestamp)
                .order("-timestamp")
                .asList();
    }

}
