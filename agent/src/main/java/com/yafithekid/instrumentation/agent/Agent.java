package com.yafithekid.instrumentation.agent;


import com.yafithekid.instrumentation.agent.configs.Config;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
        String file = "C:\\tugas\\ta\\instrumentation\\agent\\src\\main\\java\\com\\yafithekid\\instrumentation\\agent\\configs\\config.json";
        Config config = Config.readFromFile(file);
        inst.addTransformer(new BasicClassFileTransformer(config));
        Thread t = new HardwareDaemon(config);
        t.start();
    }



}
