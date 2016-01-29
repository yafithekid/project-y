package com.yafithekid.instrumentation.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new BasicClassFileTransformer("127.0.0.1",9000));
    }



}
