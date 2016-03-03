package com.github.yafithekid.project_y.example_spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

}
