package com.yafithekid.instrumentation.agent;


import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        //TODO change to file configuration
        String collectorHost = "127.0.0.1";
        int collectorPort = 9000;
        inst.addTransformer(new BasicClassFileTransformer(collectorHost,collectorPort));
        Thread t = new HardwareDaemon(collectorHost,collectorPort);
        t.start();
    }



}
