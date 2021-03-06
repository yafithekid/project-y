package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.SystemMemoryUsage;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;

public class SystemMemoryUsageDaoImpl implements SystemMemoryUsageDao{
    Datastore datastore;

    public SystemMemoryUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(SystemMemoryUsage systemMemoryUsage){
        datastore.save(systemMemoryUsage, WriteConcern.UNACKNOWLEDGED);
    }
}
