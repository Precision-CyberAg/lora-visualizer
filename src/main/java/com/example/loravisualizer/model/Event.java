package com.example.loravisualizer.model;

import com.example.loravisualizer.model.events.*;

public class Event {

    public Event(EventType eventType, Double eventTime){

        this.eventType = eventType;
        this.eventTime = eventTime;

    }
    public enum EventType{
        MOBILITY_TRACE_COURSE_CHANGE(MobilityTraceCourseChangeEvent.class),
        PHY_END_DEVICE_STATE(PhyEndDeviceStateChangeEvent.class),
        PHY_TRACE_START_SENDING(PhyTraceStartSendingEvent.class),
        PHY_TRACE_END_SENDING_EVENT(PhyTraceEndSendingEvent.class),
        PHY_TRACE_RECEIVED_PACKET(PhyTraceReceivedPacketEvent.class);

        private Class event;
        EventType(Class event) {
            this.event = event;
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    private final EventType eventType;

    public Double getEventTime() {
        return eventTime;
    }

    private final Double eventTime;

}
