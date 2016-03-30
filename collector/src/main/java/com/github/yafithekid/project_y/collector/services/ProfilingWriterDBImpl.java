package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.db.daos.*;
import com.github.yafithekid.project_y.db.models.*;
import com.github.yafithekid.project_y.db.services.DatabaseService;
import com.github.yafithekid.project_y.db.services.DaoFactory;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;

public class ProfilingWriterDBImpl implements ProfilingWriter {
    AppCPUUsageDao appCPUUsageDao;
    MemoryPoolDao memoryPoolDao;
    MethodCallDao methodCallDao;
    RequestTimeDao requestTimeDao;
    SystemCPUUsageDao systemCPUUsageDao;
    AppMemoryUsageDao appMemoryUsageDao;
    SystemMemoryUsageDao systemMemoryUsageDao;
    final Config mConfig;

    public ProfilingWriterDBImpl(DaoFactory daoFactory,Config config){
        appCPUUsageDao = daoFactory.createAppCPUUsageDao();
        memoryPoolDao = daoFactory.createMemoryPoolDao();
        methodCallDao = daoFactory.createMethodCallDao();
        requestTimeDao = daoFactory.createRequestTimeDao();
        systemCPUUsageDao = daoFactory.createSystemCPUUsageDao();
        appMemoryUsageDao = daoFactory.createAppMemoryUsageDao();
        systemMemoryUsageDao = daoFactory.createSystemMemoryUsageDao();
        mConfig = config;
    }

    @Override
    public void methodCall(MethodCall methodCall){
        long currentTimestamp = mConfig.getCurrentTimestampRounded();
        methodCallDao.save(methodCall);
        if (methodCall.isReqHandler()){
            RequestTime requestTime = requestTimeDao.findEqualTimestamp(currentTimestamp);
            if (requestTime == null){
                requestTime = new RequestTime();
                requestTime.setTimestamp(currentTimestamp);
            }
            long timeDiff = methodCall.getEnd() - methodCall.getStart();
            requestTime.setLoadTime(Math.max(requestTime.getLoadTime(),timeDiff));
            requestTimeDao.save(requestTime);
        }
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
