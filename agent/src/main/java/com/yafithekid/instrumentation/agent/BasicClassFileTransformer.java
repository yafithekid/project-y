package com.yafithekid.instrumentation.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yafithekid.instrumentation.agent.configs.Config;
import com.yafithekid.instrumentation.agent.configs.MonitoredClass;
import com.yafithekid.instrumentation.agent.configs.MonitoredMethod;
import javassist.*;

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

    public BasicClassFileTransformer(String collectorHost,int collectorPort){
        mCollectorHost = collectorHost;
        mCollectorPort = collectorPort;
    }

//    public static final String COLLECTOR_CLIENT_CLASSNAME = "com.yafithekid.instrumentation.CollectorClient";
    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        classfileBuffer = modifyByteCode(classfileBuffer,className,Config.createDummy());
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
     * modify the content of bytecode
     * @param bytecode
     * @param className classname is loaded with "com.yafithekid.instrumentation" not "com/yafithekid/instrumentation"
     * @param methodNames
     * @return
     */
    public byte[] modifyClass(byte[] bytecode, String className, List<String> methodNames){
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = null;
        byte ret[] = bytecode;
        try {
            cc = cp.get(className);
            createDataCollectMethod(cc);
            //TODO erase
//            CtMethod[] methods = cc.getMethods();
//            for(CtMethod method:methods){
//                System.out.println(method.getName());
//            }
            for(String methodName:methodNames){
                insertRunningTime(cc,methodName);
                insertMemoryUsage(cc,methodName);
                insertDataCollect(cc,methodName);
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
     * @param byteCode class bytecode
     * @param loadedClassName loaded class from class loader
     * @param config base configuration
     * @return modified bytecode
     */
    public byte[] modifyByteCode(byte[] byteCode,String loadedClassName,Config config){
        byte[] modified = byteCode;
        for (MonitoredClass mc: config.getClasses()){
            String classname = mc.getName();
            //replace classname with "." since JVM load class with "/" instead of "."
            classname = classname.replace(".","/");
            if (loadedClassName.equals(classname)){
                System.out.println(loadedClassName+" found");

                //TODO change based on monitored method
                List<String> mboh = new ArrayList<String>();
                for(MonitoredMethod mm: mc.getMethods()){
                    mboh.add(mm.getName());
                }
                return modifyClass(modified,mc.getName(),mboh);
            }
        }
        return modified;
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

