package com.example.loravisualizer.model.events;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;

public class PhyEndDeviceStateChangeEvent extends Event {
    public PhyEndDeviceStateChangeEvent(EventType eventType, Double eventTime, Node.NodeState prevState, Node.NodeState currState) {
        super(eventType, eventTime);
        this.prevState = prevState;
        this.currState = currState;
    }

    private final Node.NodeState prevState;
    private final Node.NodeState currState;

    public Node.NodeState getCurrState() {
        return currState;
    }
}
