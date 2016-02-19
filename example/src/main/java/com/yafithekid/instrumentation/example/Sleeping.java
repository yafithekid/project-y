package com.yafithekid.instrumentation.example;


import java.util.ArrayList;
import java.util.List;

public class Sleeping {
    static List<Integer> mMboh = new ArrayList<Integer>();
    /**
     * this method will randomly sleeps between 500ms and 1200ms
     * @throws InterruptedException
     */
    public void randomSleep() throws InterruptedException {
        // randomly sleeps between 500ms and 1200s
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);

    }

    /**
     * this method will consume huge memory
     * @return the created list
     */
    public List<Integer> bigList(){
        List<Integer> mboh = new ArrayList<Integer>();

        for(int i = 0; i < 1000000; i++){
            mboh.add(i);
        }
        System.out.println("add list");
        return mboh;
    }

    public List<Integer> mediumList(){
        for(int i = 0; i < 1000; i++){
            mMboh.add(i);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mMboh;
    }
    /**
     * this method will just print hello world
     */
    public void hello(){
        System.out.println("hello world");
    }

    public void callBC(){
        b();
        c();
    }


    public int callBCInt(){
        b();
        c();
        return 2;
    }

    public int b(){
        int x = 2;
        System.out.println("b...");
        return x;
    }
    public void c(){
        System.out.println("c...");
    }
    public static void main(String[] args){
        Sleeping sleeping = new Sleeping();
        try {
            sleeping.randomSleep();
            sleeping.bigList();
            sleeping.hello();
            sleeping.callBC();
            sleeping.callBCInt();
            int x = sleeping.callBCInt();
            System.out.println(sleeping.callBCInt());
//            for(int i = 0; i < 1000; i++){
//                sleeping.mediumList();
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
