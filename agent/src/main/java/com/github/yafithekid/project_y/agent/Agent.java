package com.github.yafithekid.project_y.agent;


import com.github.yafithekid.project_y.agent.daemons.HardwareDaemon;
import com.github.yafithekid.project_y.agent.daemons.HardwareDaemonWriter;
import com.github.yafithekid.project_y.agent.daemons.HardwareDaemonWriterCollectorImpl;
import com.github.yafithekid.project_y.agent.daemons.HardwareDaemonWriterMockImpl;
import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.ResourceMonitor;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class Agent {
    //singleton to prevent multiple thread of hardware daemon within tomcat or other webserver
    private static Thread hardwareThread;

    public static void premain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
        Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);

        inst.addTransformer(new BasicClassFileTransformer(config));

        ResourceMonitor rm = config.getResourceMonitor();
        if (rm.isActive() && hardwareThread == null){
            List<HardwareDaemonWriter> writers = new ArrayList<HardwareDaemonWriter>();

            if (rm.isDebug()) writers.add(new HardwareDaemonWriterMockImpl());
            if (rm.isSendToCollector()) writers.add(new HardwareDaemonWriterCollectorImpl(config));
            hardwareThread = new HardwareDaemon(config,writers);
            hardwareThread.start();
        }
    }



}
