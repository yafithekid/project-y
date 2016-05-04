package com.github.yafithekid.project_y.agent.daemons;

import com.github.yafithekid.project_y.commons.config.Config;

import java.io.IOException;
import java.net.*;

public class HardwareDaemonWriterCollectorImpl implements HardwareDaemonWriter {
    private Config config;
    private DatagramSocket datagramSocket;
    private static String suffix = "#";

    public HardwareDaemonWriterCollectorImpl(Config config) throws SocketException {
        this.config = config;
        this.datagramSocket = new DatagramSocket();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void write(String data) {
        if (!data.endsWith(suffix)){
            data += suffix;
        }
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
