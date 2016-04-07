package com.github.yafithekid.project_y.commons.config;

public class MongoHandler {
    String host;
    String dbName;
    int port;
    boolean active;
    private int maxQueueSize;
    private long queueInsertMillis;

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public long getQueueInsertMillis() {
        return queueInsertMillis;
    }

    public void setQueueInsertMillis(long queueInsertMillis) {
        this.queueInsertMillis = queueInsertMillis;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
