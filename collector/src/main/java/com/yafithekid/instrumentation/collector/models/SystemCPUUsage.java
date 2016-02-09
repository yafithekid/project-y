package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "systemCpuUsage")
@Indexes(value = {
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("start"),@Field("end")})
})
public class SystemCPUUsage {
    @Id
    private ObjectId id;
    private String systemId;
    private double load;
    private long timestamp;

    public SystemCPUUsage(String systemId,long timestamp,double load){
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.load = load;
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

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
