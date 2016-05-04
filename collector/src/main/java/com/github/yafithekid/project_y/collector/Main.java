package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriterDBImpl;
import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import com.github.yafithekid.project_y.collector.services.ProfilingWriterMockImpl;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.MongoHandler;
import com.github.yafithekid.project_y.db.services.DaoFactory;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Collector collector;
        List<ProfilingWriter> profilingWriters = new ArrayList<ProfilingWriter>();

        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        if (config.getCollector().isDebug()){
            profilingWriters.add(new ProfilingWriterMockImpl());
        }
        MongoHandler mongoHandler = config.getCollector().getMongoHandler();
        if (config.getCollector().getMongoHandler().isActive()){
            MorphiaFactory morphiaFactory = new MorphiaFactory(mongoHandler.getHost()
                    ,mongoHandler.getPort(),mongoHandler.getDbName());
            DaoFactory daoFactory = new DaoFactory(morphiaFactory,DaoFactory.MONGO_DB);
            profilingWriters.add(new ProfilingWriterDBImpl(daoFactory,config));
        }

        try {
//            collector = new SocketCollector(SocketCollector.DEFAULT_PORT,profilingWriters);
            collector = new DatagramCollector(SocketCollector.DEFAULT_PORT,profilingWriters);
            collector.run();
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot start collector!");
            e.printStackTrace();
        }

    }
}
