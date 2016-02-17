package com.yafithekid.instrumentation.agent.configs;

import java.util.ArrayList;
import java.util.List;

public class MonitoredClass {
    private String name;
    private List<MonitoredMethod> methods;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MonitoredMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<MonitoredMethod> methods) {
        this.methods = methods;
    }

    //    public MonitoredClass(String name){
//        this.name = name;
//        methods = new ArrayList<String>();
//    }

//    public void addMonitoredMethod(MonitoredMethod mm){
//        methods.add(mm);
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<MonitoredMethod> getMethods() {
//        return methods;
//    }
//
//    public void setMethods(List<MonitoredMethod> methods) {
//        this.methods = methods;
//    }
//
//    //TODO remove
//    public static void main(String[] args){
//        byte[] woi = new byte[10];
//        woi[0] = 0;
//        woi[1] = 1;
//        byte[] mboh = woi;
//        System.out.print(mboh[0]+ " " + mboh[1]);
//        woi[0] = 1;
//        System.out.print(mboh[0]+ " " + mboh[1]);
//    }
}
