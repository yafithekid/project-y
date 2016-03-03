package com.github.yafithekid.project_y.example_spring.services;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaconIpsumRestClient {
    String BASE_URL = "http://baconipsum.com/api";

    public String getAllMeat() throws IOException {
        return getAllMeat(10);
    }
    public String getAllMeat(int paras) throws IOException {
        URL url = new URL(BASE_URL+"/?type=all-meat&paras="+paras);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer result = new StringBuffer();
        while ((inputLine = in.readLine())!= null){
            result.append(inputLine);
        }
        in.close();
        return result.toString();
    }
}
