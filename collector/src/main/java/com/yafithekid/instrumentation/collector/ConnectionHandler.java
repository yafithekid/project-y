package com.yafithekid.instrumentation.collector;


import com.yafithekid.instrumentation.collector.services.ProfilingWriter;

import java.io.DataInputStream;
import java.io.IOException;
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
        DataInputStream in;
        try {
            in = new DataInputStream(mSocket.getInputStream());
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
