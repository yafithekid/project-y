package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "appMemoryUsage")
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
    private long commited;
    private long max;

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    //sme systemId timestamp used commited max
    public static SystemMemoryUsage newInstance(String data){
        SystemMemoryUsage systemMemoryUsage = new SystemMemoryUsage();
        String[] strings = data.split("\\s+");

        systemMemoryUsage.setSystemId(strings[1]);
        systemMemoryUsage.setTimestamp(Long.parseLong(strings[2]));
        systemMemoryUsage.setUsed(Long.parseLong(strings[3]));
        systemMemoryUsage.setCommited(Long.parseLong(strings[4]));
        systemMemoryUsage.setMax(Long.parseLong(strings[5]));

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
