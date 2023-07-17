package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;

public class PhyTraceEndSendingEvent extends Event {
    public PhyTraceEndSendingEvent(EventType eventType, Double eventTime, String packetUid) {
        super(eventType, eventTime);
        this.packetUid = packetUid;
    }

    private final String packetUid;

    public String getPacketUid() {
        return packetUid;
    }
}
