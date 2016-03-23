package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * Save maximum (worst) load time of a HTTP Request between "timestamp" and the next "timestamp"
 */
@Entity(value = "requestTime",noClassnameStored = true)
@Indexes(value={
        @Index(fields = {@Field("timestamp")})
})
public class RequestTime {
    @Id
    private ObjectId id;
    private long timestamp;
    private long loadTime;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }
}
