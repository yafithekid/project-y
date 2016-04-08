package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.MethodCall;
import com.mongodb.WriteConcern;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class MethodCallDaoImpl implements MethodCallDao {
    Datastore datastore;

    public MethodCallDaoImpl(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public void save(MethodCall methodCall) {
        datastore.save(methodCall, WriteConcern.UNACKNOWLEDGED);
    }

    @Override
    public List<MethodCall> getLongestHTTPRequest(long start, long end) {
        return createHTTPRequestBetweenIntervalQuery(start,end)
            .order("-duration").asList();
    }

    @Override
    public List<MethodCall> getMostConsumingMemoryHTTPRequest(long start, long end) {
        return createHTTPRequestBetweenIntervalQuery(start,end)
            .order("-maxMemory").asList();
    }

    @Override
    public List<MethodCall> getMethodsInvokedByThisId(String id) {
        MethodCall root = this.findMethodById(id);
        if (root == null){
            return null;
        } else {
            return datastore.find(MethodCall.class)
                .field("invocationId").equal(root.getInvocationId())
                .field("start").greaterThanOrEq(root.getStart())
                .field("end").lessThanOrEq(root.getEnd())
                .order("start")
                .asList();
        }
    }

    @Override
    public List<MethodCall> getUndefinedMaxMemoryHTTPRequest() {
        return datastore.find(MethodCall.class)
                .field("isReqHandler").equal(true)
                .field("maxMemory").equal(MethodCall.UNDEFINED_MAX_MEMORY)
                .asList();
    }

    @Override
    public List<MethodCall> getUndefinedCPUUsageHTTPRequest() {
        return datastore.find(MethodCall.class)
                .field("isReqHandler").equal(true)
                .field("avgCpu").lessThan(-0.000000001).asList();
    }

    @Override
    public MethodCall findMethodById(String id) {
        return datastore.find(MethodCall.class)
                .field("id").equal(new ObjectId(id)).get();
    }

    private Query<MethodCall> createHTTPRequestBetweenIntervalQuery(long start,long end){
        Query<MethodCall> query = datastore.createQuery(MethodCall.class);
        query.and(
                query.criteria("isReqHandler").equal(true),
                query.or(
                        query.criteria("start").greaterThanOrEq(start).criteria("start").lessThanOrEq(end),
                        query.criteria("end").greaterThanOrEq(start).criteria("end").lessThanOrEq(end))
        );
        return query;
    }


}
