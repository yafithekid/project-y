package com.github.yafithekid.project_y.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

import com.github.yafithekid.project_y.agent.exceptions.ClassNotExistException;
import com.github.yafithekid.project_y.agent.exceptions.IsAbstractMethodException;
import com.github.yafithekid.project_y.agent.exceptions.IsInterfaceException;
import com.github.yafithekid.project_y.commons.config.*;
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

    private MonitoredClassSearchMap mMonitoredClassSearchMap;

    public BasicClassFileTransformer(Config config){
        mCollectorHost = config.getCollector().getHost();
        mCollectorPort = config.getCollector().getPort();
        mMonitoredClassSearchMap = new MonitoredClassSearchMap(config.getClasses());
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
            createDataCollectMethod(cc);
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
                } catch (IsAbstractMethodException e) {
                    System.out.println(e.getMessage());
                }
            }
            ret = cc.toBytecode();
            cc.detach();
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
                return byteCode;
            } catch (ClassNotExistException e) {
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
        //TODO dilema between adding JSON library to instrumented JAR, or just hardcoding like this
        String data = "\"metinv "+cc.getName()+" "+m.getName()+" \"+__startTime+\" \"+__endTime+\" \"+__startMem+\" \"+__endMem+\" \"+__invocationId";
        if (mm.isRequestHandler()){
//            m.insertBefore("System.out.println(($1).getRequestURI());");
            data += "+\" \"+(($1).getRequestURI())";
        }
        m.insertAfter("{" +
                "__endMem = Runtime.getRuntime().freeMemory();" +
                "__endTime = System.currentTimeMillis();" +
                DATA_COLLECT_METHOD+"("+data+");" +
//                "com.github.yafithekid.project_y.agent.Sender.get().send("+data+");" +
                "}");
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

