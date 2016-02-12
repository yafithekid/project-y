package com.yafithekid.instrumentation.collector.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity("methodCall")
@Indexes(value={
    @Index(fields = {@Field("className")}),
    @Index(fields = {@Field("start"),@Field("end")})
})
public class MethodCall {
    @Id
    private ObjectId id;
    private String className;
    private String method;
    private Long start;
    private Long end;

    public MethodCall(String class_name,String method,long start,long end){
        this.className = class_name;
        this.method = method;
        this.start = start;
        this.end = end;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String class_name) {
        this.className = class_name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
