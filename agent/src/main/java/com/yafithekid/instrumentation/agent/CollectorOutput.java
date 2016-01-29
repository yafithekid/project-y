package com.yafithekid.instrumentation.agent;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for representing value that will be sent to the collector.
 * It will be serialized to JSON, with toString() member function
 */
public class CollectorOutput {
    /**
     * Invoke id is needed to uniquely identify two different method invocations
     */
    private String invoke_id;
    private String class_name;
    private String method_name;
    private long start;
    private long end;

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public CollectorOutput(String invoke_id,String class_name,String method_name,long start,long end){
        this.invoke_id = invoke_id;
        this.class_name = class_name;
        this.method_name = method_name;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
