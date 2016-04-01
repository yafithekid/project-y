package com.github.yafithekid.project_y.example_spring.services.mock;

public interface Service {
    void overload();

    void overload(int x);

    void overrideNoSuper();

    void overrideWithSuper();

    void foo();
}
