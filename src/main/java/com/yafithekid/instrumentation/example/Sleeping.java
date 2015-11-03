package com.yafithekid.instrumentation.example;


public class Sleeping {

    public void randomSleep() throws InterruptedException {

        // randomly sleeps between 500ms and 1200s
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);

    }

    public static void main(String[] args){
        Sleeping sleeping = new Sleeping();
        try {
            sleeping.randomSleep();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
