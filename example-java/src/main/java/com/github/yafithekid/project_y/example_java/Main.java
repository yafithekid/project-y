package com.github.yafithekid.project_y.example_java;

public class Main {
    public static void main(String[] args){
        try {
            Thread.sleep(100);
            System.out.println("sleep for 100 ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
