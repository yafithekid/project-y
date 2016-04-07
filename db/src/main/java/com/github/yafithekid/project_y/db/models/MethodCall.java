package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "methodCall",noClassnameStored = true)
@Indexes(value={
    @Index(fields = {@Field("clazz")}),
    @Index(fields = {@Field("start"),@Field("end")}),
    @Index(fields = {@Field("invocationId")}),
    @Index(fields = {@Field("isReqHandler")})
})
public class MethodCall {
    @Id
    private ObjectId id;
    private String clazz;
    private String method;
    private Long start;
    private Long end;
    private long memory;
    private String invocationId;
    private String reqMethod;
    private String url;
    private boolean isReqHandler;
    private String retClass;

    public static MethodCall newInstance(Map<String,String> map){
        MethodCall mc = new MethodCall();
        mc.setClazz(map.get("clazz"));
        mc.setMethod(map.get("method"));
        mc.setStart(Long.parseLong(map.get("start")));
        mc.setEnd(Long.parseLong(map.get("end")));
        mc.setMemory(Long.parseLong(map.get("memory")));
        mc.setInvocationId(map.get("invocationId"));
        mc.setRetClass(map.get("retClass"));
        if (map.containsKey("reqMethod") && map.containsKey("url")){
            mc.setReqHandler(true);
            mc.setReqMethod(map.get("reqMethod"));
            mc.setUrl(map.get("url"));
        }
        return mc;
    }

    public String getRetClass() {
        return retClass;
    }

    public void setRetClass(String retClass) {
        this.retClass = retClass;
    }

    public String getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(String reqMethod) {
        this.reqMethod = reqMethod;
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

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String class_name) {
        this.clazz = class_name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isReqHandler() {
        return isReqHandler;
    }

    public void setReqHandler(boolean reqHandler) {
        isReqHandler = reqHandler;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(getClazz())
                .append("#")
                .append(getMethod())
                .append(":").append(getRetClass())
                .append(" [").append(getEnd() - getStart()).append("ms,")
                .append(" ").append(getMemory()).append(" byte]");
        if (isReqHandler()){
            sb.append(" ").append(getReqMethod()).append(getUrl());
        }
        return sb.toString();
    }
}
