package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.AppCPUUsage;
import org.mongodb.morphia.Datastore;

public class AppCPUUsageDaoImpl implements AppCPUUsageDao {
    Datastore datastore;

    public AppCPUUsageDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(AppCPUUsage appCPUUsage) {
        datastore.save(appCPUUsage);
    }

}
