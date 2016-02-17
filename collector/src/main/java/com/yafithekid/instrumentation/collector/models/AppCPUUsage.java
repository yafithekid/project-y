package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appCpuUsage",noClassnameStored = true)
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

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    public static AppCPUUsage newInstance(String data){
        AppCPUUsage appCPUUsage = new AppCPUUsage();
        String[] strings = data.split("\\s+");
        appCPUUsage.setAppId(strings[1]);
        appCPUUsage.setSystemId(strings[2]);
        appCPUUsage.setTimestamp(Long.parseLong(strings[3]));
        appCPUUsage.setLoad(Double.parseDouble(strings[4]));
        return appCPUUsage;
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

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
