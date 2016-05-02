package com.github.yafithekid.project_y.agent;

import com.github.yafithekid.project_y.commons.JsonConstruct;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by yafi on 02-May-16.
 */
public class DatagramSender extends SenderTrait implements SendToCollector {

    public DatagramSender(JsonConstruct jsonConstruct) {
        super(jsonConstruct);
    }

    @Override
    void send(String data) {

    }
}
