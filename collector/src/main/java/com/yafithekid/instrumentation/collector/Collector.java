package com.yafithekid.instrumentation.collector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Collector extends Thread{
    ServerSocket mServerSocket;
    static final int DEFAULT_PORT = 9000;

    public Collector(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
        //TODO still need to hardcoded?
        //TODO need to specify timeout?
//        mServerSocket.setSoTimeout(10000);
    }

    public static void main(String[] args){
        try {
            Thread t = new Collector(DEFAULT_PORT);
            t.start();
        } catch (IOException e){
            System.out.println("Cannot start collector!");
            e.printStackTrace();
        }
        CollectorClient client = new CollectorClient();
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
                DataInputStream in =
                        new DataInputStream(server.getInputStream());
                System.out.println(in.readUTF());
                DataOutputStream out =
                        new DataOutputStream(server.getOutputStream());
                out.writeUTF("Thank you for connecting to "
                        + server.getLocalSocketAddress() + "\nGoodbye!");
                server.close();
            }catch(SocketTimeoutException s)
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
