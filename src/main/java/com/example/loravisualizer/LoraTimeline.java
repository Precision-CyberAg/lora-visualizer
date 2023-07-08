package com.example.loravisualizer;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;

import java.util.ArrayList;

public class LoraTimeline {

    ArrayList<TimelineData> timelineData;

    public LoraTimeline(){
        this.timelineData = new ArrayList<>();
    }

    public void addToTimeline(TimelineData timelineData){
        this.timelineData.add(timelineData);
    }


    public static class TimelineData{

        private double eventTime;
        private Node node;
        private Event event;

        public TimelineData(double eventTime, Node node, Event event) {
            this.eventTime = eventTime;
            this.node = node;
            this.event = event;
        }
    }
}
