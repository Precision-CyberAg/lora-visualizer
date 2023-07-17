package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;

public class TxRxPointToPointEvent extends Event {
    public TxRxPointToPointEvent(EventType eventType, Double eventTime, double timeInMillis, String senderId, String recieverId) {
        super(eventType, eventTime);
        this.timeInMillis = timeInMillis;
        this.senderId = senderId;
        this.receiver = recieverId;
    }

    private final double timeInMillis;
    private final String senderId, receiver;

    public double getTimeInMillis() {
        return timeInMillis;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiver() {
        return receiver;
    }
}
