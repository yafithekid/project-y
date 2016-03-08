package com.github.yafithekid.project_y.commons.config;

public class CollectorConfig {
    private MongoHandler mongoHandler;
    private String host = "127.0.0.1";
    private int port = 27017;
    private boolean debug = true;

    public MongoHandler getMongoHandler() {
        return mongoHandler;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
