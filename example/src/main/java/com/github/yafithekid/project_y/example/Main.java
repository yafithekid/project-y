package com.github.yafithekid.project_y.example;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args){
//        TResource resource = new TResource();
//        resource.testMemory();
//        resource.testTime();
//        TReturn tReturn = new TReturn();
//        int x = tReturn.returnInt();
//        System.out.println(tReturn.returnString());
//        tReturn.returnVoid();
        Gson gson = new Gson();
//        URL url = null;
//        try {
//            url = new URL("http://baconipsum.com/api/?type=all-meat");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            StringBuffer result = new StringBuffer();
//            while ((inputLine = in.readLine())!= null){
//                result.append(inputLine);
//            }
//            in.close();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        AbsService absService = new AbsServiceImpl();
        AbsServiceImpl absServiceImpl = new AbsServiceImpl();
        Service service = new IfaceServiceImpl();
        System.out.println("scenario 1");
        absService.overrideNoSuper();
        System.out.println("scenario 2");
        absService.overrideWithSuper();
        System.out.println("scenario 3");
        absService.foo();
        System.out.println("scenario 4");
        absService.bar();
        System.out.println("scenario 5");
        absServiceImpl.overrideNoSuper();
        System.out.println("scenario 6");
        absServiceImpl.overrideWithSuper();
        System.out.println("scenario 7");
        absService.foo();
        System.out.println("scenario 8");
        absService.bar();
        System.out.println("scenario 9");
        int x = AbsService.staticInt();
        AbsService.staticVoid();
        System.out.println("scenario 10");
        x = AbsServiceImpl.staticInt();
        AbsService.staticVoid();
        service.overload();
        service.overload(1);
    }
}
