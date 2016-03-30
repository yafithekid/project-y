package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.MethodCall;
import org.mongodb.morphia.Datastore;

public class MethodCallDaoImpl implements MethodCallDao {
    Datastore datastore;

    public MethodCallDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(MethodCall methodCall) {
        datastore.save(methodCall);
    }
}
