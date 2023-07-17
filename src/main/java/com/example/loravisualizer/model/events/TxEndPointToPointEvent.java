package com.example.loravisualizer.model.events;

public class TxEndPointToPointEvent extends TxRxPointToPointEvent{
    public TxEndPointToPointEvent(EventType eventType, Double eventTime, double timeInMillis, String senderId, String recieverId) {
        super(eventType, eventTime, timeInMillis, senderId, recieverId);
    }
}
