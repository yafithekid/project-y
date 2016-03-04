package com.github.yafithekid.project_y.example;

public class TReturn {
    final String cname = "TReturn";

    public void returnVoid() {
        System.out.println(cname + "#returnVoid");
    }

    public int returnInt() {
        System.out.println(cname + "#returnInt");
        return -1;
    }

    public String returnString() {
        System.out.println(cname + "#returnString");
        return "woi";
    }
}
