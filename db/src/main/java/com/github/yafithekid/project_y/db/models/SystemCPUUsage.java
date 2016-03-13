package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "systemCpuUsage",noClassnameStored = true)
@Indexes(value = {
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("timestamp")})
})
public class SystemCPUUsage {
    @Id
    private ObjectId id;
    private double load;
    private long timestamp;

    public static SystemCPUUsage newInstance(Map<String,String> map){
        SystemCPUUsage systemCPUUsage = new SystemCPUUsage();
        systemCPUUsage.setTimestamp(Long.parseLong(map.get("timestamp")));
        systemCPUUsage.setLoad(Double.parseDouble(map.get("load")));
        return systemCPUUsage;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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
