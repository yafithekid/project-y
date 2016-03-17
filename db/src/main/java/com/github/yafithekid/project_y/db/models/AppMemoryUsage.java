package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "appMemoryUsage",noClassnameStored = true)
@Indexes({
    @Index(fields = {@Field("timestamp")})
})
public class AppMemoryUsage {
    @Id
    private ObjectId id;
    private long timestamp;
    private long used;
    private long commited;
    private long max;
    private String type;

    public static AppMemoryUsage newInstance(Map<String,String> map){
        AppMemoryUsage amu = new AppMemoryUsage();
        amu.setTimestamp(Long.parseLong(map.get("timestamp")));
        amu.setUsed(Long.parseLong(map.get("used")));
        amu.setCommited(Long.parseLong(map.get("commited")));
        amu.setMax(Long.parseLong(map.get("max")));
        amu.setType(map.get("type"));
        return amu;
    }

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    public static AppMemoryUsage newInstance(String data){
        AppMemoryUsage appMemoryUsage = new AppMemoryUsage();
        //This will cause any number of consecutive spaces to split
        String[] strings = data.split("\\s+");
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
