package com.yafithekid.instrumentation.collector.services;

import com.mongodb.MongoClient;
import com.yafithekid.instrumentation.collector.models.Memory;
import com.yafithekid.instrumentation.collector.models.MethodCall;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;

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

    public void methodCall(String classname, String methodname, long start, long end){
        MethodCall methodCall = new MethodCall(classname,methodname,start,end);
        datastore.save(methodCall);
    }

    public static void main(String[] args){
        ProfilingWriter profilingWriter;
        profilingWriter = new ProfilingWriterMongoImpl("127.0.0.1",9000);
        profilingWriter.methodCall("a","b",1,2);

    }
}
