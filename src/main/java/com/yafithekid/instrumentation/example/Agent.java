package com.yafithekid.instrumentation.example;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            Runtime.getRuntime().exec("java -jar instrumentation-listener-1.0.jar");
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // registers the transformer
        inst.addTransformer(new SleepingClassFileTransformer());
    }



}
