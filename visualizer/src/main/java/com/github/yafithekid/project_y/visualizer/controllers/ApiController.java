package com.github.yafithekid.project_y.visualizer.controllers;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.commons.config.MongoHandler;
import com.github.yafithekid.project_y.db.daos.MethodCallDao;
import com.github.yafithekid.project_y.db.daos.SystemCPUUsageDao;
import com.github.yafithekid.project_y.db.models.*;
import com.github.yafithekid.project_y.db.services.DaoFactory;
import com.github.yafithekid.project_y.db.services.MorphiaFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.*;

@RestController
@RequestMapping(value="/api")
public class ApiController {

    private static Config config;
    private static Datastore datastore;

    private SystemCPUUsageDao systemCPUUsageDao;
    private MethodCallDao methodCallDao;

    public ApiController () throws FileNotFoundException {
        if (config == null){
            config = Config.readFromFile(Config.DEFAULT_FILE_CONFIG_LOCATION);
        }
        MongoHandler mongoHandler = config.getCollector().getMongoHandler();
        MorphiaFactory morphiaFactory = new MorphiaFactory(
                mongoHandler.getHost(),mongoHandler.getPort(),mongoHandler.getDbName());
        datastore = morphiaFactory.createDatastore();
        DaoFactory daoFactory = new DaoFactory(morphiaFactory,DaoFactory.MONGO_DB);
        systemCPUUsageDao = daoFactory.createSystemCPUUsageDao();
        methodCallDao = daoFactory.createMethodCallDao();
    }

    @RequestMapping("/urls")
    public List<MethodCall> getUrls() {
        return datastore.find(MethodCall.class)
                .field("url").exists()
                .order("-start")
                .limit(10)
                .asList();
    }

    @RequestMapping("/urls/most_memory_consuming")
    public List<MethodCall> getMostConsumingMemory(
            @RequestParam("startTimestamp") long startTimestamp,
            @RequestParam("endTimestamp") long endTimestamp
    ){
        //update all method call http request that has undefined max memory usage
        List<MethodCall> httpRequests = methodCallDao.getUndefinedMaxMemoryHTTPRequest();
        if (httpRequests != null){
            for(MethodCall httpReq:httpRequests){
                List<MethodCall> chaineds = methodCallDao
                        .getMethodsInvokedByThisId(httpReq.getId().toString());
                long result = -1;
                for(MethodCall chained: chaineds){
                    result = Math.max(result,chained.getMemory());
                }
                httpReq.setMaxMemory(result);
                methodCallDao.save(httpReq);
            }
        }
        return methodCallDao.getMostConsumingMemoryHTTPRequest(startTimestamp,endTimestamp);
    }

    @RequestMapping("/urls/longest")
    public List<MethodCall> getLongestHTTPRequest(
        @RequestParam(name="startTimestamp") long startTimestamp,
        @RequestParam(name="endTimestamp") long endTimestamp
    ) {
        return methodCallDao.getLongestHTTPRequest(startTimestamp,endTimestamp);
    }
//    @RequestMapping("/urls/longest")
//    public List<MethodCall> getLongestRuntimeMethod(
//        @RequestParam(name="startTimestamp") long startTimestamp,
//        @RequestParam(name="endTimestamp") long endTimestamp
//    ){
//        List<MethodCall> methodCalls = datastore.find(MethodCall.class)
//                .field("url").exists()
//                .field("start").greaterThanOrEq(startTimestamp)
//                .field("end").lessThanOrEq(endTimestamp).asList();
//
//        Comparator<MethodCall> comparator = new Comparator<MethodCall>() {
//            @Override
//            public int compare(MethodCall o1, MethodCall o2) {
//                long o1diff = o1.getEnd() - o1.getStart();
//                long o2diff = o2.getEnd() - o2.getStart();
//                if (o1diff < o2diff) return -1;
//                else if (o1diff > o2diff) return 1;
//                else return 0;
//            }
//        };
//
//        //create unique url
//        Map<String,MethodCall> mapUniqueResult = new HashMap<String, MethodCall>();
//        for (MethodCall newMC: methodCalls) {
//            MethodCall oldMC = mapUniqueResult.get(newMC.getUrl());
//            if (oldMC == null){
//                mapUniqueResult.put(newMC.getUrl(),newMC);
//            } else {
//                //if newMC is longer, change the data value
//                if (comparator.compare(oldMC,newMC) <= 0){
//                    mapUniqueResult.put(newMC.getUrl(),newMC);
//                }
//            }
//        }
//        //send unique url to result
//        methodCalls.clear();
//        Set<String> keys = mapUniqueResult.keySet();
//        for (String key : keys) {
//            methodCalls.add(mapUniqueResult.get(key));
//        }
//        Collections.sort(methodCalls,comparator);
//        Collections.reverse(methodCalls);
//        return methodCalls;
//    }

    /**
     * get all http request that invoked within start and end
     * @param url the invoked url
     * @param startTimestamp start timestamp
     * @param endTimestamp end timestamp
     * @return list of method call
     */
    @RequestMapping("/urls/specific")
    public List<MethodCall> getUrlsSpecific(
            @RequestParam(name="url") String url,
            @RequestParam(name="startTimestamp") long startTimestamp,
            @RequestParam(name="endTimestamp") long endTimestamp
    ){
        return datastore.find(MethodCall.class)
                .field("url").equal(url)
                .field("start").greaterThanOrEq(startTimestamp)
                .field("start").lessThanOrEq(endTimestamp)
                .asList();
    }

    @RequestMapping("/methods/{id}")
    public ResponseEntity<MethodCall> getMethodById(
            @PathVariable("id") String id){
        MethodCall mc = methodCallDao.findMethodById(id);
        if (mc == null){
            return new ResponseEntity<MethodCall>(new MethodCall(),HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<MethodCall>(mc,HttpStatus.OK);
        }
    }

    @RequestMapping("/invoked_methods/{id}")
    public ResponseEntity<List<MethodCall>> getMethodsInvokedById(
            @PathVariable("id") String id
    ) {
        if (methodCallDao.findMethodById(id) == null){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(methodCallDao.getMethodsInvokedByThisId(id),HttpStatus.OK);
        }
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

    @RequestMapping("/cpus/sys")
    public List<SystemCPUUsage> getSystemCPUUsage(
            @RequestParam(name = "startTimestamp") long startTimestamp,
            @RequestParam(name = "endTimestamp") long endTimestamp
    ){
        return systemCPUUsageDao.getWithinTimestamp(startTimestamp,endTimestamp);
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

    @RequestMapping("/reqtime")
    public List<RequestTime> getRequestTime(
            @RequestParam(name = "startTimestamp") long startTimestamp,
            @RequestParam(name = "endTimestamp") long endTimestamp
    ){
        return datastore.find(RequestTime.class)
                .field("timestamp").greaterThanOrEq(startTimestamp)
                .field("timestamp").lessThanOrEq(endTimestamp)
                .asList();

    }

}
