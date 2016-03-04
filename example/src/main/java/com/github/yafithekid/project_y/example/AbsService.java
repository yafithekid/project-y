package com.github.yafithekid.project_y.example;

public abstract class AbsService implements Service{
    final String cname = "AbsService";
    @Override
    public void overrideNoSuper() {
        System.out.println(cname+"#overrideNoSuper");
    }

    @Override
    public void overrideWithSuper() {
        System.out.println(cname+"#overrideWithSuper");
    }
}
