package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appMemoryUsage")
@Indexes({
    @Index(fields = {@Field("timestamp")}),
    @Index(fields = {@Field("systemId")}),
    @Index(fields = {@Field("appId")})
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

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    public static AppMemoryUsage newInstance(String data){
        AppMemoryUsage appMemoryUsage = new AppMemoryUsage();
        //This will cause any number of consecutive spaces to split
        String[] strings = data.split("\\s+");
        appMemoryUsage.setAppId(strings[1]);
        appMemoryUsage.setSystemId(strings[2]);
        appMemoryUsage.setTimestamp(Long.parseLong(strings[3]));
        appMemoryUsage.setUsed(Long.parseLong(strings[4]));
        appMemoryUsage.setCommited(Long.parseLong(strings[5]));
        appMemoryUsage.setMax(Long.parseLong(strings[6]));
        return appMemoryUsage;
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
