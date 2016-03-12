package com.github.yafithekid.project_y.agent.daemons;

public class HardwareDaemonWriterMockImpl implements HardwareDaemonWriter {
    @Override
    public void write(String data) {
        System.out.println(data);
    }
}
