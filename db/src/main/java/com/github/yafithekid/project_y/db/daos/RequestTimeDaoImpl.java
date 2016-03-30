package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.RequestTime;
import org.mongodb.morphia.Datastore;

public class RequestTimeDaoImpl implements RequestTimeDao {
    Datastore datastore;

    public RequestTimeDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(RequestTime requestTime) {
        datastore.save(requestTime);
    }

    @Override
    public RequestTime findEqualTimestamp(long currentTimestamp) {
        return datastore.find(RequestTime.class)
                .field("timestamp").equal(currentTimestamp).get();
    }
}
