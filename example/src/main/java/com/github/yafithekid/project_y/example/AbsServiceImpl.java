package com.github.yafithekid.project_y.example;

public class AbsServiceImpl extends AbsService {
    final static String cname = "AbsServiceImpl";

    @Override
    public void overload() {
        System.out.println(cname+"#overload");
    }

    @Override
    public void overload(int x) {
        System.out.println(cname+"#overload(int)");
    }

    @Override
    public void overrideNoSuper() {
        System.out.println(cname+"#overrideNoSuper");
    }

    @Override
    public void overrideWithSuper() {
        super.overrideWithSuper();
        System.out.println(cname+"#overrideWithSuper");
    }

    @Override
    public void bar() {
        System.out.println(cname+"#bar");
    }

    public static void staticVoid(){
        System.out.println(cname+"staticVoid");
    }

    public static int staticInt(){
        System.out.println(cname+"staticInt");
        return 1;
    }
}
