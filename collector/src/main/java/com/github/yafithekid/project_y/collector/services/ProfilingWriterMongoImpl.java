package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.db.models.*;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;

public class ProfilingWriterMongoImpl implements ProfilingWriter {
    final Datastore datastore;

    public ProfilingWriterMongoImpl(MorphiaFactory morphiaFactory){
        datastore = morphiaFactory.createDatastore();
    }

    @Override
    public void methodCall(MethodCall methodCall){
        datastore.save(methodCall);
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
