package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.db.daos.*;
import com.github.yafithekid.project_y.db.models.*;
import com.github.yafithekid.project_y.db.services.DatabaseService;
import com.github.yafithekid.project_y.db.services.DaoFactory;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;

import java.util.Queue;

public class ProfilingWriterDBImpl implements ProfilingWriter {
    AppCPUUsageDao appCPUUsageDao;
    MemoryPoolDao memoryPoolDao;
    MethodCallDao methodCallDao;
    RequestTimeDao requestTimeDao;
    SystemCPUUsageDao systemCPUUsageDao;
    AppMemoryUsageDao appMemoryUsageDao;
    SystemMemoryUsageDao systemMemoryUsageDao;
    private MethodCallQueue methodCallQueue;
    final Config mConfig;

    public ProfilingWriterDBImpl(DaoFactory daoFactory,Config config){
        appCPUUsageDao = daoFactory.createAppCPUUsageDao();
        memoryPoolDao = daoFactory.createMemoryPoolDao();
        methodCallDao = daoFactory.createMethodCallDao();
        requestTimeDao = daoFactory.createRequestTimeDao();
        systemCPUUsageDao = daoFactory.createSystemCPUUsageDao();
        appMemoryUsageDao = daoFactory.createAppMemoryUsageDao();
        systemMemoryUsageDao = daoFactory.createSystemMemoryUsageDao();
        methodCallQueue = new MethodCallQueue(config,methodCallDao,requestTimeDao);
        mConfig = config;
    }

    @Override
    public void methodCall(MethodCall methodCall){
        methodCallQueue.enqueue(methodCall);
    }

    @Override
    public void memoryPool(MemoryPool memoryPool) {
        memoryPoolDao.save(memoryPool);
    }

    @Override
    public void appCPUUsage(AppCPUUsage appCPUUsage) {
        appCPUUsageDao.save(appCPUUsage);
    }

    @Override
    public void systemCPUUsage(SystemCPUUsage systemCPUUsage) {
        systemCPUUsageDao.save(systemCPUUsage);
    }

    @Override
    public void appMemoryUsage(AppMemoryUsage appMemoryUsage) {
        appMemoryUsageDao.save(appMemoryUsage);
    }

    @Override
    public void systemMemoryUsage(SystemMemoryUsage systemMemoryUsage) {
        systemMemoryUsageDao.save(systemMemoryUsage);
    }

}
