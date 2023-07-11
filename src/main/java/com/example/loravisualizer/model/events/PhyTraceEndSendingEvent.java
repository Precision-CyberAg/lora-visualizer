package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;

public class PhyTraceEndSendingEvent extends Event {
    public PhyTraceEndSendingEvent(EventType eventType, Double eventTime) {
        super(eventType, eventTime);
    }

}
