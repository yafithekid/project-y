package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "appMemoryUsage",noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("timestamp")}),
        @Index(fields = {@Field("systemId")})
})
public class SystemMemoryUsage {
    @Id
    private ObjectId id;
    private long timestamp;
    private long used;
    private long max;

    public static SystemMemoryUsage newInstance(Map<String,String> map){
        SystemMemoryUsage sysMemUsage = new SystemMemoryUsage();
        sysMemUsage.setTimestamp(Long.parseLong(map.get("timestamp")));
        sysMemUsage.setUsed(Long.parseLong(map.get("used")));
        sysMemUsage.setMax(Long.parseLong(map.get("max")));
        return sysMemUsage;
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
