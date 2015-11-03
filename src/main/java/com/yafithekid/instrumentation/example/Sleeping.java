package com.yafithekid.instrumentation.example;


import java.util.ArrayList;
import java.util.List;

public class Sleeping {

    public void randomSleep() throws InterruptedException {
        // randomly sleeps between 500ms and 1200s
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);

    }

    public List<Integer> bigList(){
        List<Integer> mboh = new ArrayList<>();
        for(int i = 0; i < 1000000; i++){
            mboh.add(i);
        }
        return mboh;
    }

    public static void main(String[] args){
        Sleeping sleeping = new Sleeping();
        try {
            sleeping.randomSleep();
            sleeping.bigList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
