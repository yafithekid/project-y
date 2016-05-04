package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.config.Config;

import java.io.IOException;

/**
 * Created by yafi on 02-May-16.
 */
public class Sender {
    private static SendToCollector instance;

    public static SendToCollector getInstance() throws IOException {
        if (instance == null) {
            Config config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
//            instance = new SocketSender(config);
            instance = new DatagramSender(config);
        }
        return instance;
    }
}
