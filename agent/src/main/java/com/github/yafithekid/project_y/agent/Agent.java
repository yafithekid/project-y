package com.github.yafithekid.project_y.agent;


import com.github.yafithekid.project_y.commons.config.Config;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);

//        Sender.initialize(config.getCollector());
        inst.addTransformer(new BasicClassFileTransformer(config));
        if (config.isEnableResourceCollect()){
            Thread t = new HardwareDaemon(config);
        t.start();
        }
    }



}
