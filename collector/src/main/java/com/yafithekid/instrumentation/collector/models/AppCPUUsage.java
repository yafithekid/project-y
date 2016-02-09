package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appCpuUsage")
@Indexes(value = {
    @Index(fields = {@Field("appId")}),
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("timestamp")})
})
public class AppCPUUsage {
    @Id
    private ObjectId id;
    private String appId;
    private String systemId;
    private double load;
    private long timestamp;

    public AppCPUUsage(String appId, String systemId,long timestamp, double load){
        this.appId = appId;
        this.load = load;
        this.systemId = systemId;
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String machine_id) {
        this.appId = machine_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
