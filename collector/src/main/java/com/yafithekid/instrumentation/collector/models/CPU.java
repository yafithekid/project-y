package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "cpu")
@Indexes(value = {
    @Index(fields = {@Field("machine_id")}),
    @Index(fields = {@Field("timestamp")})
})
public class CPU {
    @Id
    private ObjectId id;
    private String machine_id;
    private int timestamp;

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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
