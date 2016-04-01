package com.github.yafithekid.project_y.example_spring.services.mock;

public class ServiceImpl implements Service {
    final String cname = "ServiceImpl";

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
