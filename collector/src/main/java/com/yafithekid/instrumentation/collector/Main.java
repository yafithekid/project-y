package com.yafithekid.instrumentation.collector;

import com.yafithekid.instrumentation.collector.services.ProfilingWriter;
import com.yafithekid.instrumentation.collector.services.ProfilingWriterMongoImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        Collector collector;
        ProfilingWriter profilingWriter = new ProfilingWriterMongoImpl("127.0.0.1",27017);

        try {
            collector = new Collector(Collector.DEFAULT_PORT,profilingWriter);
            collector.run();
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot start collector!");
            e.printStackTrace();
        }

    }
}
