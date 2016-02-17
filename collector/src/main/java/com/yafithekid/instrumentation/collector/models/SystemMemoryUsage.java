package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appMemoryUsage",noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("timestamp")}),
        @Index(fields = {@Field("systemId")})
})
public class SystemMemoryUsage {
    @Id
    private ObjectId id;
    private String systemId;
    private long timestamp;
    private long used;
    private long max;

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    //sme systemId timestamp used max
    public static SystemMemoryUsage newInstance(String data){
        SystemMemoryUsage systemMemoryUsage = new SystemMemoryUsage();
        String[] strings = data.split("\\s+");

        systemMemoryUsage.setSystemId(strings[1]);
        systemMemoryUsage.setTimestamp(Long.parseLong(strings[2]));
        systemMemoryUsage.setUsed(Long.parseLong(strings[3]));
        systemMemoryUsage.setMax(Long.parseLong(strings[4]));

        return systemMemoryUsage;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
}
