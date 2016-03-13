package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "appCpuUsage",noClassnameStored = true)
@Indexes(value = {
    @Index(fields = {@Field("appId")}),
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("timestamp")})
})
public class AppCPUUsage {
    @Id
    private ObjectId id;
    private double load;
    private long timestamp;

    public static AppCPUUsage newInstance(Map<String,String> map){
        AppCPUUsage appCPUUsage = new AppCPUUsage();
        appCPUUsage.setTimestamp(Long.parseLong(map.get("timestamp")));
        appCPUUsage.setLoad(Double.parseDouble(map.get("load")));
        return appCPUUsage;
    }

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

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

}
