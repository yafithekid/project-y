package com.github.yafithekid.project_y.example;

public class AbsServiceImpl extends AbsService {
    final String cname = "AbsServiceImpl";

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
}
