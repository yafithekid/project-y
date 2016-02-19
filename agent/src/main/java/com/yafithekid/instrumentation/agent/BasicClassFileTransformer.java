package com.yafithekid.instrumentation.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
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
     * @param methodName the name of method to be monitored
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void insertRunningTime(CtClass cc,String methodName) throws NotFoundException, CannotCompileException{
        //TODO overloaded method??
        CtMethod m = cc.getDeclaredMethod(methodName);
        String fieldName = "elapsedTime_"+methodName;
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
            for(MonitoredMethod method:monitoredClass.getMethods()){
                if (method.isTrace()){
                    insertExpressionEditor(cc,method.getName());
                }
                insertRunningTime(cc,method.getName());
                insertMemoryUsage(cc,method.getName());
                insertDataCollect(cc,method.getName());
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
     * @param methodName the name of method to be monitored
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void insertMemoryUsage(CtClass cc,String methodName) throws NotFoundException, CannotCompileException {
        //TODO overloaded method??
        CtMethod m = cc.getDeclaredMethod(methodName);
        String fieldName = "memoryUsage_"+methodName;
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

    void insertDataCollect(CtClass cc,String methodName) throws NotFoundException, CannotCompileException {
        CtMethod m = cc.getDeclaredMethod(methodName);
        //invocation
        //TODO dilema between adding JSON library to instrumented JAR, or just hardcoding like this
        m.addLocalVariable("__startTime",CtClass.longType);
        m.addLocalVariable("__endTime",CtClass.longType);
        m.insertBefore("__startTime = System.currentTimeMillis();");
        String invocationId = UUID.randomUUID().toString();
        m.insertAfter("{"+
                "__endTime = System.currentTimeMillis();" +
                 DATA_COLLECT_METHOD+"(\"metinv "+cc.getName()+" "+methodName+" \"+__startTime+\" \"+__endTime);" +
                "}");
    }

    void insertExpressionEditor(CtClass cc,String methodName) throws NotFoundException,CannotCompileException {
        CtMethod m = cc.getDeclaredMethod(methodName);
        m.instrument(new ExprEditor(){
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                m.replace(String.format("{ System.out.println(\"start %s\"); $_ = $proceed($$); System.out.println(\"end %s\"); }",m.getMethodName(),m.getMethodName()));
            }
        });
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

