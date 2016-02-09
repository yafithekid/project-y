package com.yafithekid.instrumentation.collector.services;

import com.mongodb.MongoClient;
import com.yafithekid.instrumentation.collector.models.AppCPUUsage;
import com.yafithekid.instrumentation.collector.models.AppMemoryUsage;
import com.yafithekid.instrumentation.collector.models.MethodCall;
import com.yafithekid.instrumentation.collector.models.SystemCPUUsage;
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
    public void methodCall(String classname, String methodname, long start, long end){
        MethodCall methodCall = new MethodCall(classname,methodname,start,end);
        datastore.save(methodCall);
    }

    @Override
    public void appCPUUsage(String appId, String systemId,long timestamp, double load) {
        AppCPUUsage appCPUUsage = new AppCPUUsage(appId,systemId,timestamp,load);
        datastore.save(appCPUUsage);
    }

    @Override
    public void systemCPUUsage(String systemId, long timestamp, double load) {
        SystemCPUUsage systemCPUUsage = new SystemCPUUsage(systemId,timestamp,load);
        datastore.save(systemCPUUsage);
    }

    @Override
    public void appMemoryUsage(String appId, String systemId, long timestamp, long used, long commited, long max) {
        AppMemoryUsage appMemoryUsage = new AppMemoryUsage(appId,systemId,timestamp,used,commited,max);
        datastore.save(appMemoryUsage);
    }

    public static void main(String[] args){
        ProfilingWriter profilingWriter;
        profilingWriter = new ProfilingWriterMongoImpl("127.0.0.1",9000);
        profilingWriter.methodCall("a","b",1,2);

    }
}
