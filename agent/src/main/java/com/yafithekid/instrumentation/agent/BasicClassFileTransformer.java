package com.yafithekid.instrumentation.agent;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import com.yafithekid.instrumentation.agent.configs.Config;
import com.yafithekid.instrumentation.agent.configs.MonitoredClass;
import com.yafithekid.instrumentation.agent.configs.MonitoredMethod;
import javassist.*;

public class BasicClassFileTransformer implements ClassFileTransformer {
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
            for(String methodName:methodNames){
                insertRunningTime(cc,methodName);
                insertMemoryUsage(cc,methodName);
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

}

