package com.github.yafithekid.project_y.collector;

import com.github.yafithekid.project_y.collector.services.ProfilingWriter;
import org.mongodb.morphia.mapping.lazy.proxy.CollectionObjectReference;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class SocketCollector implements Collector{
    ServerSocket mServerSocket;
    List<ProfilingWriter> mProfilingWriters;
    public static final int DEFAULT_PORT = 9000;

    public SocketCollector(int port, List<ProfilingWriter> profilingWriters) throws IOException {
        mServerSocket = new ServerSocket(port);
        mProfilingWriters = profilingWriters;
    }

    @Override
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
                Thread t = new ConnectionHandler(server,mProfilingWriters);
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
