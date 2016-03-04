package com.github.yafithekid.project_y.example;

import java.util.ArrayList;
import java.util.List;

public class TResource {
    public void testMemory(){
        final int EXPECTED_MEMORY_SIZE = Integer.SIZE*1000000;
        List<Integer> integers = new ArrayList<Integer>();
        for(int i = 0,_n = EXPECTED_MEMORY_SIZE/Integer.SIZE; i < _n; i++){
            integers.add(i);
        }
    }

    public void testTime(){
        long randomSleepDuration = 2000;
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        try {
            Thread.sleep(randomSleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
