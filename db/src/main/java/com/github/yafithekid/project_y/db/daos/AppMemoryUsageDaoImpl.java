package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.AppMemoryUsage;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;

public class AppMemoryUsageDaoImpl implements AppMemoryUsageDao {
    Datastore datastore;

    public AppMemoryUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    public void save(AppMemoryUsage appMemoryUsage){
        datastore.save(appMemoryUsage, WriteConcern.UNACKNOWLEDGED);
    }
}
