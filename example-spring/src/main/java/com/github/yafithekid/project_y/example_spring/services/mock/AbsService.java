package com.github.yafithekid.project_y.example_spring.services.mock;

public abstract class AbsService implements Service{
    final static String cname = "AbsService";
    @Override
    public void overrideNoSuper() {
        System.out.println(cname+"#overrideNoSuper");
    }

    @Override
    public void overrideWithSuper() {
        System.out.println(cname+"#overrideWithSuper");
    }

    @Override
    public void foo() {
        System.out.println(cname+"#foo");
    }

    public abstract void bar();

    public static void staticVoid(){
        System.out.println(cname+"staticVoid");
    }

    public static int staticInt(){
        System.out.println(cname+"staticInt");
        return 1;
    }
}
