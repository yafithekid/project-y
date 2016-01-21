package com.yafithekid.instrumentation.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            Runtime.getRuntime().exec("java -jar instrumentation-listener-1.0.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // registers the transformer
        inst.addTransformer(new SleepingClassFileTransformer());
    }



}
