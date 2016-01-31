package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "memory")
@Indexes({
    @Index(fields = {@Field("timestamp")}),
    @Index(fields = {@Field("machine_id")})
})
public class Memory {
    @Id
    private ObjectId id;
    private String machine_id;
    private Long timestamp;
    //TODO add init used commited and max
    private Long used;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMachineId() {
        return machine_id;
    }

    public void setMachineId(String machine_id) {
        this.machine_id = machine_id;
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
}
