package com.github.yafithekid.project_y.agent;

public interface SendToCollector {

    void methodCall(String className,String methodName,
                    String startTime,String endTime,String startMem,String endMem,String threadId);

    void reqHandlerMethodCall(String className,String methodName,
                    long startTime,long endTime,long startMem,long endMem,String threadId,String httpVerb,String url);
}
