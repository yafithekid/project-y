package com.yafithekid.instrumentation.collector.models;

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

    //met classname methodname start end

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
        return methodCall;
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
}
