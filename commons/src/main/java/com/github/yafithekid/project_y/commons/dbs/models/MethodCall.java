package com.github.yafithekid.project_y.commons.dbs.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "methodCall",noClassnameStored = true)
@Indexes(value={
    @Index(fields = {@Field("clazz")}),
    @Index(fields = {@Field("start"),@Field("end")})
})
public class MethodCall {
    @Id
    private ObjectId id;
    private String clazz;
    private String method;
    private Long start;
    private Long end;
    private Long freeMemStart;
    private Long freeMemEnd;
    private String invocationId;
    private String reqMethod;
    private String url;
    private boolean isReqHandler;

    /**
     * Create new instance based from agent output
     * @param data agent output
     * @return new instance
     */
    public static MethodCall newInstance(String data){
        MethodCall methodCall = new MethodCall();
        //This will cause any number of consecutive spaces to split
        String[] strings = data.split("\\s+");
        methodCall.setClazz(strings[1]);
        methodCall.setMethod(strings[2]);
        methodCall.setStart(Long.parseLong(strings[3]));
        methodCall.setEnd(Long.parseLong(strings[4]));
        methodCall.setFreeMemStart(Long.parseLong(strings[5]));
        methodCall.setFreeMemEnd(Long.parseLong(strings[6]));
        methodCall.setInvocationId(strings[7]);
        try {
            methodCall.setUrl(strings[9]);
            methodCall.setReqMethod(strings[8]);
            methodCall.setReqHandler(true);
        } catch(ArrayIndexOutOfBoundsException e){
            methodCall.setUrl(null);
            methodCall.setReqMethod(null);
            methodCall.setReqHandler(false);
        }
        return methodCall;
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

    public Long getFreeMemStart() {
        return freeMemStart;
    }

    public void setFreeMemStart(Long freeMemStart) {
        this.freeMemStart = freeMemStart;
    }

    public Long getFreeMemEnd() {
        return freeMemEnd;
    }

    public void setFreeMemEnd(Long freeMemEnd) {
        this.freeMemEnd = freeMemEnd;
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
}
