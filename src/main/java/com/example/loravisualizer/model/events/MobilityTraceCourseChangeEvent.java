package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;

public class MobilityTraceCourseChangeEvent extends Event {

    public MobilityTraceCourseChangeEvent(EventType eventType, Double eventTime, Node.NodePosition currentPosition) {
        super(eventType, eventTime);
        this.currentPosition = currentPosition;
    }

    public Node.NodePosition getCurrentPosition() {
        return currentPosition;
    }

    private final Node.NodePosition currentPosition;
}
