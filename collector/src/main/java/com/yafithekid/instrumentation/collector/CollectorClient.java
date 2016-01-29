package com.yafithekid.instrumentation.collector;

import java.io.*;
import java.net.Socket;

/**
 * TODO erase
 * Just experiment class
 */
public class CollectorClient
{
    public void connect()
    {
        String serverName = "127.0.0.1";
        int port = Collector.DEFAULT_PORT;
        try
        {
            System.out.println("Connecting to " + serverName +
                    " on port " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to "
                    + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from "
                    + client.getLocalSocketAddress());
            InputStream inFromServer = client.getInputStream();
            DataInputStream in =
                    new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
            client.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        CollectorClient collectorClient = new CollectorClient();
        collectorClient.connect();
    }
}
