package com.yafithekid.instrumentation.collector;


import com.yafithekid.instrumentation.collector.services.ProfilingWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

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
            DataInputStream is = new DataInputStream(mSocket.getInputStream());
            StringBuffer data = new StringBuffer();
            String sockInput;
            boolean stop = false;
            while (!stop){
                sockInput = is.readUTF();
                data.append(sockInput);
                if (sockInput.endsWith("\n")){
                    stop = true;

                }
            }
            //TODO transform the data
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
