package com.yafithekid.instrumentation.collector;


import com.yafithekid.instrumentation.collector.services.ProfilingWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private Socket mSocket;
    private ProfilingWriter mProfilingWriter;

    public ConnectionHandler(Socket socket, ProfilingWriter profilingWriter){
        this.mSocket = socket;
        this.mProfilingWriter = profilingWriter;
    }

    @Override
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
