package com.yafithekid.instrumentation.example;


import java.util.ArrayList;
import java.util.List;

public class Sleeping {

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

    /**
     * this method will just print hello world
     */
    public void hello(){
        System.out.println("hello world");
    }

    public static void main(String[] args){
        Sleeping sleeping = new Sleeping();
        try {
            sleeping.randomSleep();
            sleeping.bigList();
            sleeping.hello();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
