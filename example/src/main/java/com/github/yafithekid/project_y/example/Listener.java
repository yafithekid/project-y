package com.github.yafithekid.project_y.example;

import java.io.IOException;
import java.net.ServerSocket;


public class Listener {
    public static void main(){
        ServerSocket serverSocket;
        try {
            serverSocket= new ServerSocket(8000);
            System.out.println("server listening at 8000");
            serverSocket.accept();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
