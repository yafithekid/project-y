package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.db.models.*;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;

public class ProfilingWriterMongoImpl implements ProfilingWriter {
    final Datastore datastore;
    final Config mConfig;

    public ProfilingWriterMongoImpl(
        MorphiaFactory morphiaFactory,
        Config config
    ){
        datastore = morphiaFactory.createDatastore();
        mConfig = config;
    }

    @Override
    public void methodCall(MethodCall methodCall){
        long currentTimestamp = mConfig.getCurrentTimestampRounded();
        datastore.save(methodCall);
        if (methodCall.isReqHandler()){
            RequestTime requestTime = datastore.find(RequestTime.class)
                    .field("timestamp").equal(currentTimestamp).get();
            if (requestTime == null){
                requestTime = new RequestTime();
                requestTime.setTimestamp(currentTimestamp);
            }
            long timeDiff = methodCall.getEnd() - methodCall.getStart();
            requestTime.setLoadTime(Math.max(requestTime.getLoadTime(),timeDiff));
            datastore.save(requestTime);
        }
    }

    @Override
    public void memoryPool(MemoryPool memoryPool) {
        datastore.save(memoryPool);
    }

    @Override
    public void appCPUUsage(AppCPUUsage appCPUUsage) {
        datastore.save(appCPUUsage);
    }

    @Override
    public void systemCPUUsage(SystemCPUUsage systemCPUUsage) {
        datastore.save(systemCPUUsage);
    }

    @Override
    public void appMemoryUsage(AppMemoryUsage appMemoryUsage) {
        datastore.save(appMemoryUsage);
    }

    @Override
    public void systemMemoryUsage(SystemMemoryUsage systemMemoryUsage) {
        datastore.save(systemMemoryUsage);
    }
}
