package com.yafithekid.instrumentation.example;

public class Sleeping {

    public void randomSleep() throws InterruptedException {
        //sleep for 100 ms
        long randomSleepDuration = 100;
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);

    }

    public static void main(String[] args){
        Sleeping sleeping = new Sleeping();
        try {
            long eltime = System.currentTimeMillis();
            sleeping.randomSleep();
            System.out.println("total = "+(System.currentTimeMillis() - eltime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args){
//        int n = Integer.parseInt(args[0]);
//        Sleeping sleeping = new Sleeping();
//        try {
//            long eltime = System.currentTimeMillis();
//            for(int i = 0; i < n; i++){
//                sleeping.randomSleep();
//            }
//            System.out.println("total = "+(System.currentTimeMillis() - eltime));
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


}

