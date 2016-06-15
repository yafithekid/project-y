package com.github.yafithekid.project_y.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.github.yafithekid.project_y.agent.exceptions.IsAbstractMethodException;
import com.github.yafithekid.project_y.agent.exceptions.RestrictedClassException;
import com.github.yafithekid.project_y.agent.exceptions.IsInterfaceException;
import com.github.yafithekid.project_y.commons.config.*;
import javassist.*;

public class BasicClassFileTransformer implements ClassFileTransformer {
    static final String SEPARATOR = "#";
    /**
     * Method name for data collecting. will be appended to each end of method
     */
    public static final String DATA_COLLECT_METHOD = "__dcMethod";

    static final String SOCKET_FIELD = "__socket";

    /**
     * Collector hostname
     */
    public final String mCollectorHost;

    /**
     * Collector port
     */
    public final int mCollectorPort;

    private Config mConfig;
    private MonitoredClassSearchMap mMonitoredClassSearchMap;

    public BasicClassFileTransformer(Config config){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        mMonitoredClassSearchMap = new MonitoredClassSearchMap(config.getClasses());
        mConfig = config;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        classfileBuffer = modifyByteCode(classfileBuffer,className);
        return classfileBuffer;
    }

    /**
     * Modify class based on configuration
     * @param monitoredClass monitored class configuration
     * @return modified bytecode
     */
    public byte[] modifyClass(MonitoredClass monitoredClass)
            throws IsInterfaceException,RestrictedClassException {
        ClassPool cp = ClassPool.getDefault();
        ClassLoader cl = Thread.currentThread().getContextClassLoader(); // or something else
        cp.insertClassPath(new LoaderClassPath(cl));
        CtClass cc;
        byte ret[];
        try {
            cc = cp.get(monitoredClass.getName());
            if (cc.isInterface()){
                throw new IsInterfaceException(monitoredClass.getName());
            }
            if (RestrictedPackagePrefix.isRestricted(monitoredClass.getName())){
                throw new RestrictedClassException(monitoredClass.getName());
            }
//            createDataCollectMethod(cc);
//            createSenderMethodCall(cc);
            for(MonitoredMethod method:monitoredClass.getMethods()) {
                try {
                    CtMethod ctMethod = cc.getDeclaredMethod(method.getName());
                    if (!isAbstract(ctMethod)){
                        insertLocalVariables(cc,ctMethod,method);
                        insertDataCollect(cc,ctMethod,method);
                    } else {
                        throw new IsAbstractMethodException(monitoredClass.getName(),method.getName());
                    }
                } catch (NotFoundException e) {
                    System.out.println(monitoredClass.getName()+"#"+method.getName()+" does not exists");
                    if (mConfig.getAgentConfig().getErrorAction().getMethodNotExists().equals(AgentConfigErrorAction.QUIT)){
                        System.exit(1);
                    }
                } catch (IsAbstractMethodException e) {
                    System.out.println(e.getMessage());
                    if (mConfig.getAgentConfig().getErrorAction().getIsAbstractMethod().equals(AgentConfigErrorAction.QUIT)){
                        System.exit(1);
                    }
                }
            }
            ret = cc.toBytecode();
            cc.detach();
            return ret;
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Modify bytecode based from classname and config file.
     * if current class needs to be modified, it will return bytecode of modified class
     * else, return original bytecode
     * @param byteCode class bytecode
     * @param loadedClassName loaded class from class loader
     * @return modified bytecode
     */
    public byte[] modifyByteCode(byte[] byteCode, String loadedClassName){
        String replacedLoadadClassName = loadedClassName.replace("/",".");
        if (mMonitoredClassSearchMap.exist(replacedLoadadClassName)){
            MonitoredClass mc = mMonitoredClassSearchMap.get(replacedLoadadClassName);
            try {
                return modifyClass(mc);
            } catch (IsInterfaceException e) {
                System.out.println(e.getMessage());
                if (mConfig.getAgentConfig().getErrorAction().getIsInterface().equals(AgentConfigErrorAction.QUIT)){
                    System.exit(1);
                }
                return byteCode;
            } catch (RestrictedClassException e) {
                //TODO this won't work since different classloader
                System.out.println(e.getMessage());
                return byteCode;
            }
        } else {
            return byteCode;
        }
    }

    void insertLocalVariables(CtClass cc,CtMethod m,MonitoredMethod mm) throws NotFoundException, CannotCompileException {
        m.addLocalVariable("__startTime",CtClass.longType);
        m.addLocalVariable("__endTime",CtClass.longType);
        m.addLocalVariable("__invocationId",getClassString());
        m.addLocalVariable("__startMem",CtClass.longType);
        m.addLocalVariable("__endMem",CtClass.longType);
        m.insertBefore("__startTime = System.currentTimeMillis();");
        m.insertBefore("__startMem = Runtime.getRuntime().freeMemory();");
//        m.insertBefore("__invocationId = \""+UUID.randomUUID().toString()+"\";");
        m.insertBefore("__invocationId = \"\"+Thread.currentThread().getId();");
    }

    void insertDataCollect(CtClass cc,CtMethod m,MonitoredMethod mm) throws CannotCompileException {
        //send data as json
        String data = String.format("\"{" +
                "\\\"_prefix\\\":\\\"%s\\\","+
                "\\\"clazz\\\":\\\"%s\\\","+
                "\\\"method\\\":\\\"%s\\\","+
                "\\\"start\\\":\"+%s+\","+
                "\\\"end\\\":\"+%s+\","+
                "\\\"freeMemStart\\\":\"+%s+\","+
                "\\\"freeMemEnd\\\":\"+%s+\","+
                "\\\"invocationId\\\":\\\"\"+%s+\"\\\"",ProfilingPrefix.METHOD_INVOCATION,cc.getName(),m.getName(),"__startTime","__endTime","__startMem","__endMem","__invocationId");
//        String data = "\"metinv "+cc.getName()+" "+m.getName()+" \"+__startTime+\" \"+__endTime+\" \"+__startMem+\" \"+__endMem+\" \"+__invocationId";
        String methodCall;
        if (mm.isRequestHandler()){
            data += String.format("," +
                    "\\\"reqMethod\\\":\\\"\"+%s+\"\\\","+
                    "\\\"url\\\":\\\"\"+%s+\"\\\"}%s\"","($1).getMethod()","($1).getRequestURI()",SEPARATOR);
            methodCall = String.format("com.github.yafithekid.project_y.agent.Sender.getInstance().reqHandlerMethodCall(\"%s\",\"%s\",__startTime,__endTime,__startMem,__endMem,__invocationId,\"\"+%s+\"\",\"\"+%s+\"\",__result);",
                    cc.getName(),m.getName(),"($1).getMethod()","($1).getRequestURI()");
//            m.insertBefore("System.out.println(($1).getRequestURI());");
//            data += "+\" \"+(($1).getMethod())+\" \"+(($1).getRequestURI())";
        } else {
            data +="}"+SEPARATOR+"\"";
            methodCall = String.format("com.github.yafithekid.project_y.agent.Sender.getInstance().methodCall(\"%s\",\"%s\",__startTime,__endTime,__startMem,__endMem,__invocationId,__result);",
                    cc.getName(),m.getName());
        }


        m.insertAfter("{" +
                "__endMem = Runtime.getRuntime().freeMemory();" +
                "__endTime = System.currentTimeMillis();" +
//                DATA_COLLECT_METHOD+"("+data+");" +
                "Object __result = ($w) $_;"+
                methodCall+
//                reqHandlerMethodCall(String className, String methodName, long startTime, long endTime, long startMem, long endMem,String threadId, String httpVerb, String url,Object retVal) {
//                "com.github.yafithekid.project_y.agent.Sender.getInstance().reqHandlerMethodCall(""));" +
//                "System.out.println(com.github.yafithekid.project_y.agent.Agent.getObjectSize(result));"+
//                "com.github.yafithekid.project_y.agent.Sender.getInstance().send("+data+");" +
//                "com.github.yafithekid.project_y.agent.Sender.get().send("+data+");" +
                "}");
        if (mConfig.getAgentConfig().isDebug()){
            m.insertAfter("System.out.println(\"Method "+cc.getName()+"#"+m.getName()+" executed in \"+(__endTime - __startTime)+\"ms\");");
        }
    }

//    void insertDataCollect(CtClass cc,CtMethod m,MonitoredMethod mm) throws CannotCompileException {
//        String append = "{" +
//                "__endMem = Runtime.getRuntime().freeMemory();" +
//                "__endTime = System.currentTimeMillis();" +
//                "try {" +
//                " Class clazz = Thread.currentThread().getContextClassLoader().loadClass(\"com.github.yafithekid.project_y.agent.Sender\");" +
//                " java.lang.reflect.Method getInstance = clazz.getDeclaredMethod(\"getInstance\",null);" +
////                " java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();" +
////                " java.lang.reflect.Method getInstance;" +
////                " java.lang.reflect.Method methodCall;" +
////                " for(int i = 0; i < methods.length; i++){" +
////                "  java.lang.reflect.Method method = methods[i];"+
////                "  if (method.getName().equals(\"getInstance\")) getInstance = method; "+
//////                "  if (method.getName().equals(\"methodCall\")) methodCall = method; " +
////                " };" +
//                " Object o = getInstance.invoke(null,null);" +
//                " java.lang.reflect.Method methodCall = clazz.getDeclaredMethod(\"methodCall\",new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class});" +
//                " methodCall.invoke(o,(Object) new String[]{\""+cc.getName()+"\",\""+m.getName()+"\",\"\"+__startTime,\"\"+__endTime,\"\"+__startMem,\"\"+__endMem,__invocationId});" +
//                "} catch (Exception e) {" +
//                " e.printStackTrace();" +
//                "}";
////                + "}";
//        if (mm.isRequestHandler()){
//            m.insertAfter(
//                    append +
////                    "com.github.yafithekid.project_y.agent.Sender.getInstance().reqHandlerMethodCall(\""+cc.getName()+"\",\""+m.getName()+"\",__startTime,__endTime,__startMem,__endMem,__invocationId,($1).getMethod(),($1).getRequestURI());" +
//                    "}");
//        } else {
//            m.insertAfter(
//                    append +
////                    "com.github.yafithekid.project_y.agent.Sender.getInstance().methodCall(\""+cc.getName()+"\",\""+m.getName()+"\",__startTime,__endTime,__startMem,__endMem,__invocationId);" +
//                    "}");
//        }
//    }

    CtClass getClassString(){
        try {
            return ClassPool.getDefault().get("java.lang.String");
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    void createSenderMethodCall(CtClass ctClass){
//        String methodBody = "{" +
//                " try {" +
//                "  Class clazz = Thread.currentThread().getContextClassLoader().loadClass(\""+Sender.class.getName()+"\");" +
//                "  java.lang.reflect.Method m = clazz.getMethod(\"getInstance\",null);" +
//                "  Object o = m.invoke(null);" +
//                "  System.out.println(o.getClass().getName());" +
//                " } catch (Exception e){" +
//                "  e.printStackTrace();" +
//                " }" + "}";
//        try {
//            CtMethod dataCollectMethod = CtNewMethod.make("public abstract static void __a();",ctClass);
//            dataCollectMethod.setBody(methodBody);
//            dataCollectMethod.setModifiers(dataCollectMethod.getModifiers() & ~Modifier.ABSTRACT);
//            ctClass.addMethod(dataCollectMethod);
//        } catch (CannotCompileException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    void createSocket(CtClass ctClass){
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass socketClass = cp.get("java.net.Socket");
            CtField ctField = CtField.make(SOCKET_FIELD,ctClass);
            ctField.setType(socketClass);
            ctField.setModifiers(Modifier.STATIC);
            ctClass.addField(ctField,"new java.net.Socket(\""+ mCollectorHost +"\","+mCollectorPort +");");
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    void createDataCollectMethodWithSender(CtClass ctClass){
        try {
            //TODO set to async
            ClassPool cp = ClassPool.getDefault();
            //construct method body
            //make the method abstract, insert the method and set to non-abstract.
            String methodBody = "{" +
                    "if (!($1).endsWith(\"\\n\")) { ($1) = ($1) + \"\\n\"; } " +
                    "Sender.getInstance().send($1);" +
                    "}";
            CtMethod dataCollectMethod = CtNewMethod.make("public abstract static void "+DATA_COLLECT_METHOD+"(java.lang.String data);",ctClass);

            dataCollectMethod.setBody(methodBody);

            dataCollectMethod.setModifiers(dataCollectMethod.getModifiers() & ~Modifier.ABSTRACT);
            ctClass.addMethod(dataCollectMethod);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    void createDataCollectMethod(CtClass ctClass){
        try {
            //TODO set to async
            ClassPool cp = ClassPool.getDefault();
            //construct method body
            //make the method abstract, insert the method and set to non-abstract.
            String methodBody = "{" +
            "($1) = ($1) +\""+SEPARATOR+"\";"+
            "com.github.yafithekid.project_y.agent.Sender.getInstance().send($1);" +
//            "java.net.Socket __client = new java.net.Socket(\""+ mCollectorHost +"\","+ mCollectorPort +");" +
//            "java.io.OutputStream __outToServer = __client.getOutputStream();" +
//            "if (!($1).endsWith(\"\\n\")) { ($1) = ($1) + \"\\n\"; } " +
//            "java.io.DataOutputStream __out = new java.io.DataOutputStream(__outToServer);" +
////            "System.out.println($1);" +
//            "__out.writeUTF($1);"+
//            "__client.close();" +
            "}";
//            String methodBody = "{" + "System.out.println($1);" + "}";
            CtMethod dataCollectMethod = CtNewMethod.make("public abstract static void "+DATA_COLLECT_METHOD+"(java.lang.String data);",ctClass);

            dataCollectMethod.setBody(methodBody);

            //add socket exception handler
            //https://jboss-javassist.github.io/javassist/tutorial/tutorial2.html
            CtClass ioExceptionClass = cp.get("java.io.IOException");
            String errorMessage = "[ERROR] " +
                    "Cannot connect to collector " +
                mCollectorHost + ":" + mCollectorPort;
            dataCollectMethod.addCatch("{System.out.println(\""+errorMessage+"\"); ($e).printStackTrace(); return;}",ioExceptionClass);
            ioExceptionClass.detach();
            dataCollectMethod.setModifiers(dataCollectMethod.getModifiers() & ~Modifier.ABSTRACT);
            ctClass.addMethod(dataCollectMethod);
        } catch (CannotCompileException e) {
            //TODO what to do?
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    boolean isAbstract(CtMethod method){
        return (method.getModifiers() & Modifier.ABSTRACT) != 0;
    }
}

