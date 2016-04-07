package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.MemoryPool;
import com.mongodb.WriteConcern;
import org.mongodb.morphia.Datastore;

public class MemoryPoolDaoImpl implements MemoryPoolDao {
    Datastore datastore;

    public MemoryPoolDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(MemoryPool memoryPool) {
        datastore.save(memoryPool, WriteConcern.UNACKNOWLEDGED);
    }
}
