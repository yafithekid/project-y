package com.github.yafithekid.project_y.commons.config;



import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents data in the config file
 */
public class Config {
    public static final String DEFAULT_FILE_CONFIG_LOCATION = "C:\\tugas\\ta\\instrumentation\\config\\src\\main\\resources\\config.json";
    private List<MonitoredClass> classes;
    private CollectorConfig collector;
    private long resourceCollectRateMillis;
    private String appId;
    private String systemId;

    public static Config readFromFile(String location) throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader(location));
        return gson.fromJson(reader, Config.class);
    }

    public Config(){
        this.classes = new ArrayList<MonitoredClass>();
    }

    public static void main(String []args) throws IOException {
//        String file = Thread.currentThread().getContextClassLoader().getResource("com/github/yafithekid/project_y/config/config.json").getPath();
//        String file = "C:\\tugas\\ta\\instrumentation\\agent\\src\\main\\java\\com\\yafithekid\\instrumentation\\agent\\configs\\config.json";
        String file = DEFAULT_FILE_CONFIG_LOCATION;
        Config config = readFromFile(file);
        Gson gson = new Gson();
        String s = gson.toJson(config);
        System.out.println(s);
    }

    public List<MonitoredClass> getClasses() {
        return classes;
    }

    public void setClasses(List<MonitoredClass> classes) {
        this.classes = classes;
    }

    public CollectorConfig getCollector() {
        return collector;
    }

    public void setCollector(CollectorConfig collector) {
        this.collector = collector;
    }

    public long getResourceCollectRateMillis() {
        return resourceCollectRateMillis;
    }

    public void setResourceCollectRateMillis(long resourceCollectRateMillis) {
        this.resourceCollectRateMillis = resourceCollectRateMillis;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
