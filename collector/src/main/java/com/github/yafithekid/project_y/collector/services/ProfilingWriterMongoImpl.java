package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.commons.dbs.models.*;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class ProfilingWriterMongoImpl implements ProfilingWriter {
    final Morphia morphia;
    final Datastore datastore;
    //TODO change database name to file config
    static final String DB_NAME = "profiling";

    public ProfilingWriterMongoImpl(String host,int port){
        morphia = new Morphia();
        morphia.mapPackage("com.yafithekid.instrumentation.collector.models");
        datastore = morphia.createDatastore(new MongoClient(host,port),DB_NAME);
        datastore.ensureIndexes();
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
