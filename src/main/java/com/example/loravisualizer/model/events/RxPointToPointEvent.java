package com.example.loravisualizer.model.events;

public class RxPointToPointEvent extends TxRxPointToPointEvent{
    public RxPointToPointEvent(EventType eventType, Double eventTime, double timeInMillis, String senderId, String recieverId) {
        super(eventType, eventTime, timeInMillis, senderId, recieverId);
    }

}
