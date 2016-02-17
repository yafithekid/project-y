package com.yafithekid.instrumentation.agent;


import com.yafithekid.instrumentation.config.Config;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        inst.addTransformer(new BasicClassFileTransformer(config));
        Thread t = new HardwareDaemon(config);
        t.start();
    }



}
