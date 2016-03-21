package com.github.yafithekid.project_y.visualizer.controllers;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.MongoHandler;
import com.github.yafithekid.project_y.db.models.AppCPUUsage;
import com.github.yafithekid.project_y.db.models.AppMemoryUsage;
import com.github.yafithekid.project_y.db.models.MemoryPool;
import com.github.yafithekid.project_y.db.models.MethodCall;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value="/api")
public class ApiController {

    static Config config;
    static Datastore datastore;

    public ApiController () throws FileNotFoundException {
        if (config == null){
            config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        }
        MongoHandler mongoHandler = config.getCollector().getMongoHandler();
        MorphiaFactory morphiaFactory = new MorphiaFactory(
                mongoHandler.getHost(),mongoHandler.getPort(),mongoHandler.getDbName());
        datastore = morphiaFactory.createDatastore();
    }

    @RequestMapping("/urls")
    public List<MethodCall> getUrls() {
        return datastore.find(MethodCall.class)
                .field("isReqHandler").equal(true)
                .order("-start")
                .limit(10)
                .asList();
    }

    @RequestMapping("/methods")
    public List<MethodCall> getMethods(
            @RequestParam(name = "invocationId") String invocationId,
            @RequestParam(name = "start") long start,
            @RequestParam(name = "end") long end
    ) {
        return datastore.find(MethodCall.class)
                .field("invocationId").equal(invocationId)
                .field("start").greaterThanOrEq(start)
                .field("end").lessThanOrEq(end)
                .order("start")
                .asList();
    }

    @RequestMapping("/cpus/app")
    public List<AppCPUUsage> getAppCPUUsage(
            @RequestParam(name = "startTimestamp") long startTimestamp,
            @RequestParam(name = "endTimestamp") long endTimestamp
    ){
        return datastore.find(AppCPUUsage.class)
                .field("timestamp").lessThanOrEq(endTimestamp)
                .field("timestamp").greaterThanOrEq(startTimestamp)
                .order("-timestamp")
                .asList();
    }

    @RequestMapping("/memories/app")
    public List<AppMemoryUsage> getAppMemoryUsage(
            @RequestParam(name = "startTimestamp") long startTimestamp,
            @RequestParam(name = "endTimestamp") long endTimestamp,
            @RequestParam(name = "type") String type)
    {
        Query<AppMemoryUsage> query = datastore.find(AppMemoryUsage.class)
                .field("timestamp").lessThanOrEq(endTimestamp)
                .field("timestamp").greaterThanOrEq(startTimestamp);
        if (type != null && !type.isEmpty()){
            query.field("type").equal(type);
        }

        return query.order("timestamp")
                .asList();
    }

    @RequestMapping("/memories/pools")
    public List<MemoryPool> getMemoryPools(
            @RequestParam(name = "startTimestamp") long startTimestamp,
            @RequestParam(name = "endTimestamp") long endTimestamp,
            @RequestParam(name = "type",defaultValue ="") String type)
    {
        Query<MemoryPool> query = datastore.find(MemoryPool.class)
                .field("timestamp").lessThanOrEq(endTimestamp)
                .field("timestamp").greaterThanOrEq(startTimestamp);

        if (type != null && !type.isEmpty()){
            query.field("type").equal(type);
        }

        return query.order("timestamp")
                .asList();
    }

    @RequestMapping("/timestamp")
    public long getCurrentTimestamp(){
        return System.currentTimeMillis();
    }

}
