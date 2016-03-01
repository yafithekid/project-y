package com.github.yafithekid.project_y.config;

public class CollectorConfig {
    private String host = "127.0.0.1";
    private int port = 27017;
    private boolean useMock = true;

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

    public boolean isUseMock() {
        return useMock;
    }

    public void setUseMock(boolean useMock) {
        this.useMock = useMock;
    }
}
