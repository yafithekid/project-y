package com.yafithekid.instrumentation.collector;

import com.yafithekid.instrumentation.collector.services.ProfilingWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Collector {
    ServerSocket mServerSocket;
    ProfilingWriter mProfilingWriter;
    static final int DEFAULT_PORT = 9000;

    public Collector(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
        //TODO still need to hardcoded?
        //TODO need to specify timeout?
//        mServerSocket.setSoTimeout(10000);
    }

    public static void main(String[] args){
        Collector collector = null;
        try {
            collector = new Collector(DEFAULT_PORT);
            collector.run();
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot start collector!");
            e.printStackTrace();
        }

    }

    public void run(){
        while(true)
        {
            try
            {
                System.out.println("Waiting for client on port " +
                        mServerSocket.getLocalPort() + "...");
                Socket server = mServerSocket.accept();
                System.out.println("Just connected to "
                        + server.getRemoteSocketAddress());
                Thread t = new ConnectionHandler(server,mProfilingWriter);
                t.start();
            } catch(SocketTimeoutException s)
            {
                System.out.println("Socket timed out!");
                break;
            }catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }
}
