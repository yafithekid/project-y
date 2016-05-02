package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.JsonConstruct;

public abstract class SenderTrait implements SendToCollector {
    private JsonConstruct jsonConstruct;

    public SenderTrait(JsonConstruct jsonConstruct){
        this.jsonConstruct = jsonConstruct;
    }

    @Override
    public void methodCall(String className, String methodName,long startTime, long endTime, long startMem,long endMem,String threadId,Object retVal) {
        String retClass = "Void";
        if (retVal != null){
        retClass = retVal.getClass().getName();
        }
        String data = jsonConstruct.constructMethodCall(className,methodName,startTime,endTime,Agent.getObjectSize(retVal),threadId,retClass);
    //        String data = String.format("%s %s %s %d %d %d %d %s",
    //                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId);
        data += BasicClassFileTransformer.SEPARATOR;
        send(data);
    }

    @Override
    public void reqHandlerMethodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId, String httpVerb, String url,Object retVal) {
        String retClass = "Void";
        if (retVal != null){
        retClass = retVal.getClass().getName();
        }
        String data = jsonConstruct.constructReqHandlerMethodCall(className,methodName,startTime,endTime,Agent.getObjectSize(retVal),threadId,httpVerb,url,retClass);
//        String data = String.format("%s %s %s %d %d %d %d %s %s %s",
//                ProfilingPrefix.METHOD_INVOCATION,className,methodName,startTime,endTime,startMem,endMem,threadId,
//                httpVerb,url);
        data += BasicClassFileTransformer.SEPARATOR;
        send(data);
    }

    abstract void send(String data);
}
