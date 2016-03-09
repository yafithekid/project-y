package com.github.yafithekid.project_y.commons.dbs.services;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MorphiaFactory {
    final String host;
    final int port;
    final Morphia morphia;
    final String dbName;

    public MorphiaFactory(String host,int port,String dbName){
        morphia = new Morphia();
        morphia.mapPackage("com.yafithekid.instrumentation.collector.models");
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    public Datastore createDatastore(){
        Datastore datastore = morphia.createDatastore(new MongoClient(host,port), dbName);
        datastore.ensureIndexes();
        return datastore;
    }
}
