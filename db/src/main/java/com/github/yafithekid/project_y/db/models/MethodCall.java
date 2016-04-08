package com.github.yafithekid.project_y.db.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Map;

@Entity(value = "methodCall",noClassnameStored = true)
@Indexes(value={
    @Index(fields = {@Field("clazz")}),
    @Index(fields = {@Field("start"),@Field("end")}),
    @Index(fields = {@Field("invocationId")}),
    @Index(fields = {@Field("isReqHandler")}),
    @Index(fields = {@Field("duration")}),
    @Index(fields = {@Field("memory")}),
    @Index(fields = {@Field("avgCpu")})
})
public class MethodCall {
    public static final long UNDEFINED_MAX_MEMORY = -1;
    public static final long UNDEFINED_CPU_USAGE = -1;
    @Id
    private ObjectId id;
    private String clazz;
    private String method;
    private Long start;
    private Long end;
    private long duration;
    private long memory;

    /**
     * Max memory usage of all methods invoked by this http request
     * Invoked
     */
    private long maxMemory;

    /**
     * Average CPU usage when the method is called
     */
    private double avgCpu;

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
        mc.setDuration(mc.getEnd() - mc.getStart());

        mc.setMemory(Long.parseLong(map.get("memory")));
        mc.setMaxMemory(UNDEFINED_MAX_MEMORY);
        mc.setAvgCpu(UNDEFINED_CPU_USAGE);
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

    public long getDuration() {
        return duration;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getAvgCpu() {
        return avgCpu;
    }

    public void setAvgCpu(double avgCpu) {
        this.avgCpu = avgCpu;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(getClazz())
                .append("#")
                .append(getMethod())
                .append(":").append(getRetClass())
                .append(" [").append(getDuration()).append("ms,")
                .append(" ").append(getMemory()).append(" byte]");
        if (isReqHandler()){
            sb.append(" ").append(getReqMethod()).append(getUrl());
        }
        return sb.toString();
    }
}
