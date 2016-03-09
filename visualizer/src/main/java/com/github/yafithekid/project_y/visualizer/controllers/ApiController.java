package com.github.yafithekid.project_y.visualizer.controllers;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.MongoHandler;
import com.github.yafithekid.project_y.commons.dbs.models.MethodCall;
import com.github.yafithekid.project_y.commons.dbs.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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


}
