package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;

public class PhyTraceReceivedPacketEvent extends Event {
    public PhyTraceReceivedPacketEvent(EventType eventType, Double eventTime, String packetUid) {
        super(eventType, eventTime);
        this.packetUid = packetUid;
    }

    private final String packetUid;

    public String getPacketUid() {
        return packetUid;
    }
}
