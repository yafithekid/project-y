package com.github.yafithekid.project_y.commons.dbs.services;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MorphiaFactory {
    final String host;
    final int port;
    final Morphia morphia;
    //TODO change database name to file config
    static final String DB_NAME = "profiling";

    public MorphiaFactory(String host,int port){
        morphia = new Morphia();
        morphia.mapPackage("com.yafithekid.instrumentation.collector.models");
        this.host = host;
        this.port = port;
    }

    public Datastore createDatastore(){
        Datastore datastore = morphia.createDatastore(new MongoClient(host,port),DB_NAME);
        datastore.ensureIndexes();
        return datastore;
    }
}
