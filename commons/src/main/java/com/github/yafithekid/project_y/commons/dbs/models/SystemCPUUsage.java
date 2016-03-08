package com.github.yafithekid.project_y.commons.dbs.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "systemCpuUsage",noClassnameStored = true)
@Indexes(value = {
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("timestamp")})
})
public class SystemCPUUsage {
    @Id
    private ObjectId id;
    private String systemId;
    private double load;
    private long timestamp;

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    public static SystemCPUUsage newInstance(String data){
        String[] strings = data.split("\\s+");

        SystemCPUUsage systemCPUUsage = new SystemCPUUsage();
        systemCPUUsage.setSystemId(strings[1]);
        systemCPUUsage.setTimestamp(Long.parseLong(strings[2]));
        systemCPUUsage.setLoad(Double.parseDouble(strings[3]));

        return systemCPUUsage;
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
