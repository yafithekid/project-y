package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "memoryPool",noClassnameStored = true)
@Indexes(value = {
})
public class MemoryPool {
    @Id
    private ObjectId id;
    private long timestamp;
    private String name;
    private long used;
    private long commited;
    private long max;
    private String type;

    public static MemoryPool newInstance(Map<String,String> map){
        MemoryPool mp = new MemoryPool();
        mp.setTimestamp(Long.parseLong(map.get("timestamp")));
        mp.setName(map.get("name"));
        mp.setUsed(Long.parseLong(map.get("used")));
        mp.setCommited(Long.parseLong(map.get("commited")));
        mp.setMax(Long.parseLong(map.get("max")));
        mp.setType(map.get("type"));
        return mp;
    }

    public static MemoryPool newInstance(String data){
        String[] strings = data.split("\\s+");
        MemoryPool mp = new MemoryPool();
        System.out.println("WOI "+data);
        mp.setTimestamp(Long.parseLong(strings[1]));
        mp.setName(strings[2]);
        mp.setUsed(Long.parseLong(strings[3]));
        mp.setCommited(Long.parseLong(strings[4]));
        mp.setMax(Long.parseLong(strings[5]));
        return mp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
