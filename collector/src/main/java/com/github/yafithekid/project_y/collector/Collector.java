package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Collector {
    ServerSocket mServerSocket;
    ProfilingWriter mProfilingWriter;
    public static final int DEFAULT_PORT = 9000;

    public Collector(int port,ProfilingWriter profilingWriter) throws IOException {
        mServerSocket = new ServerSocket(port);
        mProfilingWriter = profilingWriter;
        //TODO still need to hardcoded?
        //TODO need to specify timeout?
//        mServerSocket.setSoTimeout(10000);
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
