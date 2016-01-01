package com.yafithekid.instrumentation.example;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.*;

public class SleepingClassFileTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;
        if (className.equals("com/yafithekid/instrumentation/example/Sleeping")){
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get("com.yafithekid.instrumentation.example.Sleeping");
                CtMethod m = cc.getDeclaredMethod("randomSleep");
                String fieldName = "elapsedTime_sleep";
                CtField ctField = new CtField(CtClass.longType, fieldName, cc);
                cc.addField(ctField, CtField.Initializer.constant(5L));
                m.insertBefore(fieldName + " = System.currentTimeMillis();");
                m.insertAfter("{" + fieldName + " = System.currentTimeMillis() - " + fieldName + ";"
                        + "System.out.println(\"Method Executed in ms: \" + " + fieldName + ");}");
                byteCode = cc.toBytecode();
                cc.detach();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteCode;
    }
}

