package com.github.yafithekid.project_y.collector.services;

import com.github.yafithekid.project_y.commons.config.Config;
import com.github.yafithekid.project_y.db.daos.MethodCallDao;
import com.github.yafithekid.project_y.db.daos.RequestTimeDao;
import com.github.yafithekid.project_y.db.models.MethodCall;
import com.github.yafithekid.project_y.db.models.RequestTime;
import com.github.yafithekid.project_y.db.models.SystemCPUUsage;

import java.util.LinkedList;
import java.util.Queue;

public class MethodCallQueue {
    private Queue<MethodCall> queue;
    private int maxQueueSize;

    public MethodCallQueue(Config config,MethodCallDao methodCallDao,RequestTimeDao requestTimeDao){
        queue = new LinkedList<>();
        Thread t = new DaoInsertionThread(queue,methodCallDao,
                requestTimeDao,config);
        t.start();
        maxQueueSize = config.getCollector().getMongoHandler().getMaxQueueSize();
    }

    public void enqueue(MethodCall methodCall){
        synchronized (queue){
            if (queue.size() < maxQueueSize){
                queue.add(methodCall);
            } else {
//                System.out.println("Warning: exceeding max queue size = "+maxQueueSize);
            }
        }
    }

    private class DaoInsertionThread extends Thread {
        private Queue<MethodCall> queue;
        private MethodCallDao methodCallDao;
        private RequestTimeDao requestTimeDao;
        private long queueInsertMillis;
        private Config mConfig;

        public DaoInsertionThread(Queue<MethodCall> queue, MethodCallDao methodCallDao,
                                  RequestTimeDao requestTimeDao,Config config){
            this.queue = queue;
            this.methodCallDao = methodCallDao;
            this.requestTimeDao = requestTimeDao;
            this.queueInsertMillis = config.getCollector().getMongoHandler().getQueueInsertMillis();
            mConfig = config;
        }

        @Override
        public void run() {
            while (true){
                synchronized (queue){
                    while (!queue.isEmpty()){
                        MethodCall methodCall = queue.poll();
                        long currentTimestamp = mConfig.getCurrentTimestampRounded();
                        methodCallDao.save(methodCall);
                        if (methodCall.isReqHandler()){
                            RequestTime requestTime = requestTimeDao.findEqualTimestamp(currentTimestamp);
                            if (requestTime == null){
                                requestTime = new RequestTime();
                                requestTime.setTimestamp(currentTimestamp);
                            }
                            long timeDiff = methodCall.getEnd() - methodCall.getStart();
                            requestTime.setLoadTime(Math.max(requestTime.getLoadTime(),timeDiff));
                            requestTimeDao.save(requestTime);
                        }
                    }
                }
                try {
                    Thread.sleep(queueInsertMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
