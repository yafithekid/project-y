package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriterMongoImpl;
import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.collector.services.ProfilingWriterMockImpl;
import com.github.yafithekid.project_y.commons.config.Config;

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
