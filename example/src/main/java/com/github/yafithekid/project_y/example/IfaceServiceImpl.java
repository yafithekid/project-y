package com.github.yafithekid.project_y.example;

public class IfaceServiceImpl implements Service {
    final String cname = "IfaceServiceImpl";

    @Override
    public void overload() {
        System.out.println(cname + "#overload");
    }

    @Override
    public void overload(int x) {
        System.out.println(cname + "#overload(int)");
    }

    @Override
    public void overrideNoSuper() {
        System.out.println(cname + "#overrideNoSuper");
    }

    @Override
    public void overrideWithSuper() {
        System.out.println(cname + "#overrideWithSuper");
    }

    @Override
    public void foo() {
        System.out.println(cname+"#foo");
    }


}
