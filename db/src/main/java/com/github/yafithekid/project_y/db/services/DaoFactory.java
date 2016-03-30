package com.github.yafithekid.project_y.db.services;

import com.github.yafithekid.project_y.db.daos.*;
import org.mongodb.morphia.Datastore;

public class DaoFactory {
    public static final int MONGO_DB = 1;
    private Datastore datastore;
    private int option;

    public DaoFactory(MorphiaFactory morphiaFactory,int opt){
        this.option = opt;
        datastore = morphiaFactory.createDatastore();
    }

    public AppCPUUsageDao createAppCPUUsageDao(){
        return new AppCPUUsageDaoImpl(datastore);
    }

    public MemoryPoolDao createMemoryPoolDao(){
        return new MemoryPoolDaoImpl(datastore);
    }

    public MethodCallDao createMethodCallDao(){
        return new MethodCallDaoImpl(datastore);
    }

    public RequestTimeDao createRequestTimeDao(){
        return new RequestTimeDaoImpl(datastore);
    }

    public SystemCPUUsageDao createSystemCPUUsageDao(){
        return new SystemCPUUsageDaoImpl(datastore);
    }

    public AppMemoryUsageDao createAppMemoryUsageDao(){
        return new AppMemoryUsageDaoImpl(datastore);
    }

    public SystemMemoryUsageDao createSystemMemoryUsageDao(){
        return new SystemMemoryUsageDaoImpl(datastore);
    }
}
