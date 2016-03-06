package com.github.yafithekid.project_y.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

import com.github.yafithekid.project_y.agent.exceptions.ClassNotExistException;
import com.github.yafithekid.project_y.agent.exceptions.IsAbstractMethodException;
import com.github.yafithekid.project_y.agent.exceptions.IsInterfaceException;
import com.github.yafithekid.project_y.agent.exceptions.MethodNotExistException;
import com.github.yafithekid.project_y.config.*;
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
    public byte[] modifyClass(MonitoredClass monitoredClass)
            throws IsInterfaceException,ClassNotExistException {
        ClassPool cp = ClassPool.getDefault();
                ClassLoader cl
                = Thread.currentThread().getContextClassLoader(); // or something else
        cp.insertClassPath(new LoaderClassPath(cl));
        CtClass cc;
        byte ret[];
        try {
            cc = cp.get(monitoredClass.getName());
            if (cc.isInterface()){
                throw new IsInterfaceException(monitoredClass.getName());
            }
//            System.out.println("modify class " + monitoredClass.getName());
            createDataCollectMethod(cc);
            for(MonitoredMethod method:monitoredClass.getMethods()) {
                try {
                    CtMethod ctMethod = cc.getDeclaredMethod(method.getName());
//                    System.out.println("trying to modify: "+monitoredClass.getName()+"#"+method.getName());
                    if (!isAbstract(ctMethod)){
//                        System.out.println("modifying "+monitoredClass.getName()+"#"+method.getName());
//                        createOverloadMethod(cc, ctMethod);
                        insertLocalVariables(cc,ctMethod);
//                        if (method.isTrace()){
//                            insertExpressionEditor(cc,ctMethod);
//                        }
//                    insertRunningTime(cc,ctMethod);
//                    insertMemoryUsage(cc,ctMethod);
                        insertDataCollect(cc,ctMethod);
                    } else {
                        throw new IsAbstractMethodException(monitoredClass.getName(),method.getName());
                    }
                } catch (NotFoundException e) {
                    System.out.println(monitoredClass.getName()+"#"+method.getName()+" does not exists");
                } catch (IsAbstractMethodException e) {
                    System.out.println(e.getMessage());
                }
            }
            ret = cc.toBytecode();
            cc.detach();
//            System.out.println("modify class completed");
            return ret;
        } catch (NotFoundException e) {
            throw new ClassNotExistException(monitoredClass.getName());
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
//            System.out.println(replacedLoadadClassName+" found");
            try {
                return modifyClass(mc);
            } catch (IsInterfaceException e) {
                System.out.println(e.getMessage());
                return byteCode;
            } catch (ClassNotExistException e) {
                System.out.println(e.getMessage());
                return byteCode;
            }
        } else {
            return byteCode;
        }
    }

    /**
     * Add method for sending data via socket
     * the first parameter of method is a string that will be given to socket
     * to access the parameter, use "$1"
     */
    void createOverloadMethod(CtClass ctClass,CtMethod ctMethod){
        try {
            //add string as first parameter
            CtClass[] parameterTypesArray = ctMethod.getParameterTypes();
            List<CtClass> parameterTypes = new ArrayList<CtClass>();
            parameterTypes.add(getClassString());
            for(int i = 0; i < parameterTypesArray.length; i++){
                parameterTypes.add(parameterTypesArray[i]);
            }
            int nParams = parameterTypes.size();
            //construct $2...nParams
            StringBuilder sb = new StringBuilder();
            for(int i = 2; i <= nParams; i++){
                if (i > 2) sb.append(",");
                sb.append("$").append(i);
            }
            String methodBody;
            if (ctMethod.getReturnType().equals(CtClass.voidType)){
                methodBody = String.format("{%s(%s); System.out.println(\"dummy\");}",ctMethod.getName(),sb.toString());
            } else {
                methodBody = String.format("{ %s __ret = this.%s(%s); System.out.println(\"dummy\"); return __ret; }",ctMethod.getReturnType().getName(),
                        ctMethod.getName(),sb.toString());
            }
            CtMethod dataCollectMethod;
            CtClass[] params = new CtClass[parameterTypes.size()];
            for(int i = 0; i < params.length; i++){
                params[i] = parameterTypes.get(i);
            }
            dataCollectMethod = CtNewMethod.make(ctMethod.getReturnType(),ctMethod.getName(),
                    params,ctMethod.getExceptionTypes(),methodBody,ctClass);
            ctClass.addMethod(dataCollectMethod);
        } catch (CannotCompileException e){
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    void insertLocalVariables(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException {
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

    void insertDataCollect(CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException {
        //TODO dilema between adding JSON library to instrumented JAR, or just hardcoding like this
        String data = "\"metinv "+cc.getName()+" "+m.getName()+" \"+__startTime+\" \"+__endTime+\" \"+__startMem+\" \"+__endMem+\" \"+__invocationId";
        m.insertAfter("{" +
                "__endMem = Runtime.getRuntime().freeMemory();" +
                "__endTime = System.currentTimeMillis();" +
                DATA_COLLECT_METHOD+"("+data+");" +
//                "com.github.yafithekid.project_y.agent.Sender.get().send("+data+");" +
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
//                    methodCall.replace("{ $_ = $proceed($$); System.out.println(\"hore\"); }");
                    methodCall.replace("{ $_ =  $proceed($$,__invocationId);}");
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

