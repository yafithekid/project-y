package com.github.yafithekid.project_y.commons.config;

public class CollectorConfig {
    private MongoHandler mongoHandler;
    private String host;
    private int port;
    private String dbName;
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
