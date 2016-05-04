package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.JsonConstruct;
import com.github.yafithekid.project_y.commons.config.Config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;

public class DatagramSender extends SenderTrait implements SendToCollector {
    DatagramSocket datagramSocket;
    Config config;

    public DatagramSender(Config config) throws SocketException {
        super(new JsonConstruct());
        datagramSocket = new DatagramSocket();
        this.config = config;
    }

    @Override
    void send(String data) {
        byte[] sendData;
        sendData = data.getBytes();
        SocketAddress socketAddress = new InetSocketAddress(config.getCollector().getHost(),config.getCollector().getPort());
        DatagramPacket datagramPacket = new DatagramPacket(sendData,sendData.length,socketAddress);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
