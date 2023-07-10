package com.example.loravisualizer;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;

import java.util.ArrayList;
import java.util.Iterator;

public class LoraTimeline {

    private ArrayList<TimelineData> timelineData;
    private Iterator<TimelineData> timelineIterator;

    public ArrayList<TimelineData> getTimelineData() {
        return timelineData;
    }

    private TimelineData next;
    public LoraTimeline(){
        this.timelineData = new ArrayList<>();
    }

    public void addToTimeline(TimelineData timelineData){
        this.timelineData.add(timelineData);
        reset();
    }

    public void reset(){
        timelineIterator = timelineData.iterator();
        next = null;
    }

    public TimelineData peek(){
        if(next!=null){
            return next;
        }else if(timelineIterator.hasNext()){
            next = timelineIterator.next();
            return next;
        }else{
            return null;
        }
    }

    public TimelineData next(){
            TimelineData data = next;
            if(timelineIterator.hasNext()){
                next = timelineIterator.next();
            }else next = null;
            return data;
    }

    public  boolean hasNext(){
        return next!=null || timelineIterator.hasNext();
    }


    public static class TimelineData{

        private final double eventTime;
        private final Node node;
        private final Event event;

        public TimelineData(double eventTime, Node node, Event event) {
            this.eventTime = eventTime;
            this.node = node;
            this.event = event;
        }

        public double getEventTime() {
            return eventTime;
        }

        public Node getNode() {
            return node;
        }

        public Event getEvent() {
            return event;
        }
    }
}
