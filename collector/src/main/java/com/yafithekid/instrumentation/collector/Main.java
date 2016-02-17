package com.yafithekid.instrumentation.collector;

import com.yafithekid.instrumentation.collector.services.ProfilingWriter;
import com.yafithekid.instrumentation.collector.services.ProfilingWriterMockImpl;
import com.yafithekid.instrumentation.collector.services.ProfilingWriterMongoImpl;
import com.yafithekid.instrumentation.config.Config;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Collector collector;
        ProfilingWriter profilingWriter;
        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        if (!config.getCollector().isUseMock()){
            profilingWriter = new ProfilingWriterMongoImpl("127.0.0.1",27017);
        } else {
            profilingWriter = new ProfilingWriterMockImpl();
        }

        try {
            collector = new Collector(Collector.DEFAULT_PORT,profilingWriter);
            collector.run();
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot start collector!");
            e.printStackTrace();
        }

    }
}
