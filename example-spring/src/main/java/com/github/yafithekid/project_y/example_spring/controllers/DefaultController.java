package com.github.yafithekid.project_y.example_spring.controllers;

import com.github.yafithekid.project_y.example_spring.services.BaconIpsumRestClient;
import com.github.yafithekid.project_y.example_spring.services.mock.AbsService;
import com.github.yafithekid.project_y.example_spring.services.mock.AbsServiceImpl;
import com.github.yafithekid.project_y.example_spring.services.mock.Service;
import com.github.yafithekid.project_y.example_spring.services.mock.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DefaultController {
    @Autowired
    DriverManagerDataSource driverManagerDataSource;

    //for ensuring the controller works
    @RequestMapping("/")
    public String testHome(){
        return "hello world!";
    }


    @RequestMapping("/memories")
    public String testMemories(
            //default size 50MB = 50
            @RequestParam(name="size",defaultValue = "52428800") long size
    ){
        List<Integer> shorts = new ArrayList<Integer>();
        for(long i = 0; i < size / Integer.SIZE; i++){
            shorts.add(1);
        }
        long memExhausted = shorts.size() * Integer.SIZE;
        return String.valueOf(memExhausted);
    }

    //this request will be displayed in about 5 seconds
    @RequestMapping("/long")
    public String testLong(
        @RequestParam(name = "time",value = "",defaultValue = "5000") int time
    ){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "you wait for "+time+" millisecs";
    }

    @ResponseBody
    @RequestMapping("/tolls")
    public List<String> getTolls() throws SQLException {
        Connection c = driverManagerDataSource.getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT * FROM tolls");
        ResultSet rs = ps.executeQuery();
        List<String> retval = new ArrayList<String>();
        while (rs.next()){
            retval.add(rs.getString("name"));
        }
        return retval;
    }

    @RequestMapping("/http")
    public String testHttp() throws IOException {
        BaconIpsumRestClient client = new BaconIpsumRestClient();
        return client.getAllMeat();
    }

    @RequestMapping("/cpu")
    public String testCpu(
            @RequestParam(name="time", defaultValue = "10000") long time
    ){
        Thread t = new FooThread(time);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.valueOf("Runned for " +time);
        //create new thread and run it

    }

    @RequestMapping("/methoda")
    public String testMethodA(){
        return methodA();

    }

    @RequestMapping("/methodb")
    public String testMethodB(){
        return methodB();
    }

    @RequestMapping("/methodinv")
    public String testMethodInvocation(){
        AbsService absService = new AbsServiceImpl();
        AbsServiceImpl absServiceImpl = new AbsServiceImpl();
        Service service = new ServiceImpl();
        ServiceImpl serviceImpl = new ServiceImpl();
        //TC-01-01
        service.foo();
        //TC-01-02
        absService.bar();
        //TC-01-03
        absService.foo();
        //TC-01-04
        absServiceImpl.foo();
        //TC-01-05
        absServiceImpl.overrideNoSuper();
        //TC-01-06
        absServiceImpl.overrideWithSuper();
        //TC-01-07
        AbsServiceImpl.staticVoid();
        //TC-01-08
        AbsServiceImpl.staticInt();
        //TC-01-09
        serviceImpl.foo();
        return "method invocation ends. see the visualization";
    }

    @RequestMapping("/db")
    public String testDB(){
        //TODO impl
        //TODO return value
        return "";
    }

    public static void main(String[] args) throws IOException {
        BaconIpsumRestClient client = new BaconIpsumRestClient();
        System.out.println(client.getAllMeat());
    }

    private String methodA(){ return "method A"; }

    private String methodB(){ return "method B"; }
}

class FooThread extends Thread{
    long duration;

    public FooThread(long duration){
        this.duration = duration;
    }

    @Override
    public void run() {
        long stopTime = System.currentTimeMillis() + duration;
        int x = 0;
        while (System.currentTimeMillis() < stopTime){
            x++;
        }
    }

}
