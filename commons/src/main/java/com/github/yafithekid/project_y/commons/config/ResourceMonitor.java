package com.github.yafithekid.project_y.commons.config;

public class ResourceMonitor {
    boolean active;
    boolean sendToCollector;
    long collectRateMillis;
    boolean debug;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCollectRateMillis() {
        return collectRateMillis;
    }

    public void setCollectRateMillis(long collectRateMillis) {
        this.collectRateMillis = collectRateMillis;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isSendToCollector() {
        return sendToCollector;
    }

    public void setSendToCollector(boolean sendToCollector) {
        this.sendToCollector = sendToCollector;
    }
}
