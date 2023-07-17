package com.example.loravisualizer.model.events;

public class TxStartPointToPointEvent extends TxRxPointToPointEvent{
    public TxStartPointToPointEvent(EventType eventType, Double eventTime, double timeInMillis, String senderId, String recieverId) {
        super(eventType, eventTime, timeInMillis, senderId, recieverId);
    }
}
