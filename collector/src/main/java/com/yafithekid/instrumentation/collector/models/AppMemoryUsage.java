package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appMemoryUsage")
@Indexes({
    @Index(fields = {@Field("timestamp")}),
    @Index(fields = {@Field("systemId")})
})
public class AppMemoryUsage {
    @Id
    private ObjectId id;
    private String systemId;
    private String appId;
    private long timestamp;
    private long used;
    private long commited;
    private long max;

    public AppMemoryUsage(String appId,String systemId,long timestamp,long used,long commited,long max){
        this.appId = appId;
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.used = used;
        this.commited = commited;
        this.max = max;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String machine_id) {
        this.systemId = machine_id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getCommited() {
        return commited;
    }

    public void setCommited(long commited) {
        this.commited = commited;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
}
