package com.yafithekid.instrumentation.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.yafithekid.instrumentation.config.*;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class BasicClassFileTransformer implements ClassFileTransformer {
    /**
     * Method name for data collecting. will be appended to each end of method
     */
    public static final String DATA_COLLECT_METHOD = "__dcMethod";

    /**
     * Collector hostname
     */
    public final String mCollectorHost;

    /**
     * Collector port
     */
    public final int mCollectorPort;

    private Config mConfig;

    private MonitoredMethodSearchMap mMethodInvocationSearchMap;
    private MonitoredClassSearchMap mMonitoredClassSearchMap;

    public BasicClassFileTransformer(Config config){
        mConfig = config;
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        mMethodInvocationSearchMap = new MonitoredMethodSearchMap(config.getClasses());
        mMonitoredClassSearchMap = new MonitoredClassSearchMap(config.getClasses());
    }

//    public static final String COLLECTOR_CLIENT_CLASSNAME = "com.yafithekid.instrumentation.CollectorClient";
    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        classfileBuffer = modifyByteCode(classfileBuffer,className);
        return classfileBuffer;
    }

    /**
     * Insert bytecode for monitoring running time.
     * @param cc class to be modified
     * @param m the method to be altered
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void insertRunningTime(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException{
        //TODO overloaded method??
        String fieldName = "elapsedTime_"+m.getName();
        CtField ctField = new CtField(CtClass.longType,fieldName,cc);
        cc.addField(ctField, CtField.Initializer.constant(5L));
        m.insertBefore(fieldName+" = System.currentTimeMillis();");
        m.insertAfter("{"+fieldName+" = System.currentTimeMillis() - "+fieldName+";"
                + "System.out.println(\"Method Executed in ms: \" + "+fieldName+");}");
    }

    /**
     * Modify class based on configuration
     * @param monitoredClass monitored class configuration
     * @return modified bytecode
     */
    public byte[] modifyClass(MonitoredClass monitoredClass){
        ClassPool cp = ClassPool.getDefault();
        CtClass cc;
        byte ret[];
        try {
            cc = cp.get(monitoredClass.getName());
            createDataCollectMethod(cc);
            for(MonitoredMethod method:monitoredClass.getMethods()) {
                try {
                    CtMethod ctMethod = cc.getDeclaredMethod(method.getName());
                    createOverloadMethod(cc, ctMethod);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            for(MonitoredMethod method:monitoredClass.getMethods()){
                try {
                    CtMethod ctMethod = cc.getDeclaredMethod(method.getName());
                    insertLocalVariables(cc,ctMethod);
                    if (method.isTrace()){
                        insertExpressionEditor(cc,ctMethod);
                    }
                    insertRunningTime(cc,ctMethod);
                    insertMemoryUsage(cc,ctMethod);
                    insertDataCollect(cc,ctMethod);
                } catch (NotFoundException e){
                    System.out.println(method.getName()+" in class "+monitoredClass.getName()+" not found!");
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
     * Insert bytecode for monitoring memory usage.
     * @param cc class to be modified
     * @param m the name of method to be monitored
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void insertMemoryUsage(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException {
        //TODO overloaded method??
        String fieldName = "memoryUsage_"+m.getName();
        CtField ctField = new CtField(CtClass.longType,fieldName,cc);
        cc.addField(ctField, CtField.Initializer.constant(5L));
        m.insertBefore(fieldName+" = Runtime.getRuntime().freeMemory();");
        m.insertAfter("{"+fieldName+" = "+fieldName+" - Runtime.getRuntime().freeMemory();"
                + "System.out.println(\"Memory usage is: \" + "+fieldName+");}");
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
            System.out.println(replacedLoadadClassName+" found");
            return modifyClass(mc);
        } else {
            return byteCode;
        }
    }

    /**
     * Add method for sending data via socket
     * the first parameter of method is a string that will be given to socket
     * to access the parameter, use "$1"
     */
    void createDataCollectMethod(CtClass ctClass){
        try {
            //TODO set to async
            ClassPool cp = ClassPool.getDefault();
            //construct method body
            //make the method abstract, insert the method and set to non-abstract.
            String methodBody = "{" +
                    "java.net.Socket __client = new java.net.Socket(\""+ mCollectorHost +"\","+ mCollectorPort +");" +
                    "java.io.OutputStream __outToServer = __client.getOutputStream();" +
                    "if (!($1).endsWith(\"\\n\")) { ($1) = ($1) + \"\\n\"; } " +
                    "java.io.DataOutputStream __out = new java.io.DataOutputStream(__outToServer);" +
                    "System.out.println($1);" +
                    "__out.writeUTF($1);"+
                    "__client.close();" +
                    "}";
//            String methodBody = "{" + "System.out.println($1);" + "}";
            CtMethod dataCollectMethod = CtNewMethod.make("public abstract void "+DATA_COLLECT_METHOD+"(java.lang.String data);",ctClass);

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

    /**
     * Add method for sending data via socket
     * the first parameter of method is a string that will be given to socket
     * to access the parameter, use "$1"
     */
    void createOverloadMethod(CtClass ctClass,CtMethod ctMethod){
        try {
            String methodBody = String.format("{ System.out.println(\"dummy %s\");}",ctMethod.getName());
            CtMethod dataCollectMethod = CtNewMethod.make(CtClass.voidType,"__"+ctMethod.getName(),null,null,methodBody,ctClass);
            ctClass.addMethod(dataCollectMethod);
        } catch (CannotCompileException e) {
            //TODO what to do?
            e.printStackTrace();
        }
    }

    void insertLocalVariables(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException {
        m.addLocalVariable("__startTime",CtClass.longType);
        m.addLocalVariable("__endTime",CtClass.longType);
        m.addLocalVariable("__invocationId",getClassString());
        m.insertBefore("__startTime = System.currentTimeMillis();");
        m.insertBefore("__invocationId = \""+UUID.randomUUID().toString()+"\";");
    }

    void insertDataCollect(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException {
        //TODO dilema between adding JSON library to instrumented JAR, or just hardcoding like this
        m.insertAfter("{" +
                "__endTime = System.currentTimeMillis();" +
                 DATA_COLLECT_METHOD+"(\"metinv "+cc.getName()+" "+m.getName()+" \"+__startTime+\" \"+__endTime+\" \"+__invocationId);" +
                "}");
    }

    void insertExpressionEditor(CtClass cc,CtMethod m) throws NotFoundException,CannotCompileException {
        m.instrument(new ExprEditor(){
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (mMethodInvocationSearchMap.exist(methodCall.getClassName(),methodCall.getMethodName())){
                    try {
                        methodCall.getMethod();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                    methodCall.replace(String.format("{ System.out.println(\"start %s\"); $_ = $proceed($$); __%s(); System.out.println(\"end %s\"); }",
                            methodCall.getMethodName(),methodCall.getMethodName(),methodCall.getMethodName()));
                }

            }
        });
    }

    CtClass getClassString(){
        try {
            return ClassPool.getDefault().get("java.lang.String");
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    TODO should the add-ons of collectorclient is a new class or not?
//    /**
//     * create collector client to establish socket connection between target java process and collector process.
//     * if it is already created, it will not be created again.
//     */
//    void createCollectorClient(){
//        ClassPool cp = ClassPool.getDefault();
//        try {
//            cp.get(COLLECTOR_CLIENT_CLASSNAME);
//            System.out.println("found...");
//        } catch (NotFoundException e) {
//            System.out.println("not found...");
//            String a = COLLECTOR_CLIENT_CLASSNAME;
//            CtClass ctClass = cp.makeClass(a);
//            ctClass.setModifiers(ctClass.getModifiers() | Modifier.PUBLIC);
//            String methodBody = "{System.out.println($1); System.out.println(\"the outsider\");}";
//            try {
//                CtMethod dataCollectMethod = CtNewMethod.make("public static abstract void "+DATA_COLLECT_METHOD+"(java.lang.String data);",ctClass);
//                dataCollectMethod.setBody(methodBody);
//                dataCollectMethod.setModifiers(dataCollectMethod.getModifiers() & ~Modifier.ABSTRACT);
//                ctClass.addMethod(dataCollectMethod);
//                ctClass.writeFile();
//                System.out.println("done");
//            } catch (CannotCompileException e1) {
//                e1.printStackTrace();
//            } catch (NotFoundException e1) {
//                e1.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//    }
}

