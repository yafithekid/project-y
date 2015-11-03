package com.yafithekid.instrumentation.example;


import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import com.yafithekid.instrumentation.configs.Config;
import com.yafithekid.instrumentation.configs.MonitoredClass;
import com.yafithekid.instrumentation.configs.MonitoredMethod;
import javassist.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.FieldNode;

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
        System.out.println("loader: "+loader.getClass().getName()+" class: "+className);
//        if (className.equals("com/yafithekid/instrumentation/example/Sleeping")) {
//            //TODO kayaknya modifikasinya pada suatu CC harus sekali doang.
//            //byteCode = monitorRunningTime(byteCode, "com.yafithekid.instrumentation.example.Sleeping", "randomSleep");
            //byteCode = monitorMemoryUsage(byteCode, "com.yafithekid.instrumentation.example.Sleeping", "randomSleep");
////            try {
////                ClassPool cp = ClassPool.getDefault();
////                CtClass cc = cp.get("com.yafithekid.instrumentation.example.Sleeping");
////                CtMethod m = cc.getDeclaredMethod("randomSleep");
////                CtField ctField = new CtField(CtClass.longType,"elapsedTime",cc);
////                cc.addField(ctField, CtField.Initializer.constant(5L));
////                //m.addLocalVariable("elapsedTime", CtClass.longType);
////                m.insertBefore("elapsedTime = System.currentTimeMillis();");
////                m.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
////                        + "System.out.println(\"Method Executed in ms: \" + elapsedTime);}");
////
////                byteCode = cc.toBytecode();
////                cc.detach();
////            } catch (Exception ex) {
////                ex.printStackTrace();
////            }
//        }

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

    public byte[] monitorMemoryUsage(byte[] byteCode,String className,String methodName){
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = null;
        try {
            cc = cp.get(className);
            CtMethod m = cc.getDeclaredMethod(methodName);
            String fieldName = "memoryUsage_"+methodName;
            CtField ctField = new CtField(CtClass.longType,fieldName,cc);
            cc.addField(ctField, CtField.Initializer.constant(5L));
            m.insertBefore(fieldName+" = Runtime.getRuntime().freeMemory();");
            m.insertAfter("{"+fieldName+" = "+fieldName+" - Runtime.getRuntime().freeMemory();"
                    + "System.out.println(\"Memory usage is: \" + "+fieldName+");}");
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
        System.out.println("modifying bytecodes...");
        for (MonitoredClass mc: config.getClasses()){
            String classname = new String(mc.getName());
            classname = classname.replace(".","/");
            if (className.equals(classname)){
                System.out.println(className+" found");
                for(MonitoredMethod mm: mc.getMethods()){
                    byte[] old = Arrays.copyOf(modified, modified.length);
                    modified = monitorMemoryUsage(modified,mc.getName(),mm.getName());
                    assert (!Arrays.equals(old,modified));
                }
            }
        }
        return modified;
    }

//    class MemoryUsageVisitor extends ClassVisitor {
//        FieldNode memUsageFn;
//
//        public MemoryUsageVisitor(FieldNode memUsageFn, ClassVisitor cv) {
//            super(Opcodes.ASM5,cv);
//            this.memUsageFn = memUsageFn;
//        }
//
//
//        @Override
//        public void visitEnd() {
//            memUsageFn.accept(cv);
//            super.visitEnd();
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//            System.out.println(name + " - " + desc + " - " + signature);
//            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//            if (name.equals("randomSleep")){
//                System.out.println(name);
//                mv = new MonitorTimeAdapter(Opcodes.ASM5,mv,access,name,desc,memUsageFn);
//            }
//            return mv;
//        }
//    }
//
//    class MonitorTimeAdapter  extends AdviceAdapter {
//        String methodName;
//        double millis;
//        FieldNode memUsageFn;
//        /**
//         * Creates a new {@link AdviceAdapter}.
//         *
//         * @param api    the ASM API version implemented by this visitor. Must be one
//         *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
//         * @param mv     the method visitor to which this adapter delegates calls.
//         * @param access the method's access flags (see {@link Opcodes}).
//         * @param name   the method's name.
//         * @param desc   the method's descriptor (see {@link Type Type}).
//         */
//        public MonitorTimeAdapter(int api, MethodVisitor mv, int access, String name, String desc,
//                                  FieldNode memUsageFN) {
//            super(api,mv,access,name,desc);
//            this.memUsageFn = memUsageFN;
//            this.methodName = name;
//        }
//
//        @Override
//        protected void onMethodEnter() {
//            //super.onMethodEnter();
//            millis = System.currentTimeMillis();
//        }
//
//
//        @Override
//        protected void onMethodExit(int opcode) {
//            super.onMethodExit(opcode);
//            System.out.println(methodName + (System.currentTimeMillis() - millis));
//        }
//    }
}

