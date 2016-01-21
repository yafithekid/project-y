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

public class SleepingClassFileTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ///
        /// Start of transform using ASM
        ///
//        if (className.equals("com/yafithekid/instrumentation/example/Sleeping")) {
//            ClassReader cr = new ClassReader(classfileBuffer);
//            ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES);
//            FieldNode fn = new FieldNode(Opcodes.ACC_PRIVATE, "memUsage", Type.getDescriptor(Integer.class), null, 1);
//            cr.accept(new MemoryUsageVisitor(fn,cw), ClassReader.EXPAND_FRAMES);
//        }
//        return classfileBuffer;
        ///
        /// END of transform using ASM
        ///



        //START OF JAVASSSIST

        byte[] byteCode = classfileBuffer;
        return modifyByteCode(byteCode,className,Config.createDummy());


        // END OF TRANSFORMATION USING JAVASSIST

    }

    public byte[] monitorRunningTime(byte[] byteCode,String className,String methodName){
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = null;
        try {
            cc = cp.get(className);
            CtMethod m = cc.getDeclaredMethod(methodName);
            String fieldName = "elapsedTime_"+methodName;
            CtField ctField = new CtField(CtClass.longType,fieldName,cc);
            cc.addField(ctField, CtField.Initializer.constant(5L));
            m.insertBefore(fieldName+" = System.currentTimeMillis();");
            m.insertAfter("{"+fieldName+" = System.currentTimeMillis() - "+fieldName+";"
                    + "System.out.println(\"Method Executed in ms: \" + "+fieldName+");}");
            byteCode = cc.toBytecode();
            cc.detach();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
        }
        return byteCode;
    }

    public void insertRunningTime(CtClass cc,String methodName) throws NotFoundException, CannotCompileException{
        CtMethod m = cc.getDeclaredMethod(methodName);
        String fieldName = "elapsedTime_"+methodName;
        CtField ctField = new CtField(CtClass.longType,fieldName,cc);
        cc.addField(ctField, CtField.Initializer.constant(5L));
        m.insertBefore(fieldName+" = System.currentTimeMillis();");
        m.insertAfter("{"+fieldName+" = System.currentTimeMillis() - "+fieldName+";"
                + "System.out.println(\"Method Executed in ms: \" + "+fieldName+");}");
    }

    public void insertMemoryUsage(CtClass cc,String methodName) throws NotFoundException, CannotCompileException {
        CtMethod m = cc.getDeclaredMethod(methodName);
        String fieldName = "memoryUsage_"+methodName;
        CtField ctField = new CtField(CtClass.longType,fieldName,cc);
        cc.addField(ctField, CtField.Initializer.constant(5L));
        m.insertBefore(fieldName+" = Runtime.getRuntime().freeMemory();");
        m.insertAfter("{"+fieldName+" = "+fieldName+" - Runtime.getRuntime().freeMemory();"
                + "System.out.println(\"Memory usage is: \" + "+fieldName+");}");
    }


    public byte[] monitorMemoryUsage(byte[] byteCode,String className,List<String> methodNames){
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = null;
        try {
            cc = cp.get(className);
            for(String methodName:methodNames){
                insertMemoryUsage(cc,methodName);
                insertRunningTime(cc,methodName);
            }
            byte[] ret = cc.toBytecode();
            cc.detach();
            return ret;
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] modifyByteCode(byte[] byteCode,String className,Config config){
        byte[] modified = byteCode;
        //TODO impl
        for (MonitoredClass mc: config.getClasses()){
            String classname = new String(mc.getName());
            classname = classname.replace(".","/");
            if (className.equals(classname)){
                System.out.println(className+" found");
                List<String> mboh = new ArrayList<>();
                for(MonitoredMethod mm: mc.getMethods()){
                    mboh.add(mm.getName());
                }
                modified = monitorMemoryUsage(modified,mc.getName(),mboh);
            }
        }
        return modified;
    }

}

