package com.example.loravisualizer;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;
import com.example.loravisualizer.model.events.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class LoraAnimationParser {
    LoraAnimationParser(File selectedFile) {
        nodes = new ArrayList<>();
        loraTimeline = new LoraTimeline();
        parseFile(selectedFile);
    }

    private LoraTimeline loraTimeline;

    public LoraTimeline getLoraTimeline() {
        return loraTimeline;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    private ArrayList<Node> nodes;

    private double highestX = 0;
    private double highestY = 0;

    private double maxDuration = 0;

    public double getHighestX() {
        return highestX;
    }

    public double getHighestY() {
        return highestY;
    }

    private void parseFile(File selectedFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            String data = new String(fileInputStream.readAllBytes());
            fileInputStream.close();

            JSONArray jsonArray = new JSONArray(data);

            for (Object o : jsonArray) {

                JSONObject jsonObject = (JSONObject) o;

                jsonObject.keys().forEachRemaining(time -> {

                    maxDuration = Double.parseDouble(time);

                    JSONObject nodeJson = jsonObject.getJSONObject(time);

                    String traceType = nodeJson.getString("TraceType");

                    String nodeId = nodeJson.getString("NodeId");

                    Event event = null;
                    Node node = null;

                    switch (traceType){


                        case "MobilityTraceCourseChange" -> {

                            Node.NodePosition nodePosition = parseNodePosition(nodeJson.getString("Position"));

                            if(nodePosition.getX() > highestX) highestX = nodePosition.getX();
                            if(nodePosition.getY() > highestY) highestY = nodePosition.getY();

                            event = new MobilityTraceCourseChangeEvent(
                                    Event.EventType.MOBILITY_TRACE_COURSE_CHANGE,
                                    maxDuration,
                                    nodePosition
                            );

                            node = new Node(
                                    nodeId,
                                    nodeJson.getString("DeviceAddress"),
                                    parseDeviceType(
                                            nodeJson.getString("DeviceType")
                                    ),
                                   new Node.NodePosition(0,0,0)
                            );

                            int nodeIndex = nodes.indexOf(node);

                            if(nodeIndex == -1){

                                nodes.add(node);
                                node.addEvent(event);

                            }else{

                                nodes.get(nodeIndex).addEvent(event);
                                node = nodes.get(nodeIndex);

                            }
                            System.out.println();
                        }

                        case "PhyEndDeviceState" -> {
                            event = new PhyEndDeviceStateChangeEvent(
                                    Event.EventType.PHY_END_DEVICE_STATE,
                                    maxDuration,
                                    getNodeState(nodeJson.getString("DeviceState1")),
                                    getNodeState(nodeJson.getString("DeviceState2"))
                            );
                            for(Node node1: nodes){
                                if(node1.getNodeId().equals(nodeId)){
                                    node = node1;
                                }
                            }
                        }

                        case "PHYTraceStartSending" -> {
                            event = new PhyTraceStartSendingEvent(
                                    Event.EventType.PHY_TRACE_START_SENDING,
                                    Double.parseDouble(time),
                                    nodeJson.getString("PacketUid"));
                            for(Node node1: nodes){
                                if(node1.getNodeId().equals(nodeId)){
                                    node = node1;
                                }
                            }

                            Event endingEvent = new PhyTraceEndSendingEvent(
                                    Event.EventType.PHY_TRACE_END_SENDING_EVENT,
                                    Double.parseDouble(time)+Double.parseDouble(nodeJson.getString("Duration"))
                            );

                            loraTimeline.addToTimeline(new LoraTimeline.TimelineData(
                                    endingEvent.getEventTime(),
                                    node,
                                    endingEvent
                            ));

                        }

                        case "PHYTraceReceivedPacket" -> {
                            event = new PhyTraceReceivedPacketEvent(
                                    Event.EventType.PHY_TRACE_RECEIVED_PACKET,
                                    Double.parseDouble(time),
                                    nodeJson.getString("PacketUid"));
                            for(Node node1: nodes){
                                if(node1.getNodeId().equals(nodeId)){
                                    node = node1;
                                }
                            }
                        }
                    }
                    if(event!=null && node!=null){
                        loraTimeline.addToTimeline(new LoraTimeline.TimelineData(
                                        Double.parseDouble(time),
                                        node,
                                        event
                                )
                        );
                    }
                });

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Node.NodeState getNodeState(String nodeState){
        switch (nodeState){
            case "TX" -> {
                return Node.NodeState.TX;
            }
            case "RX" -> {
                return Node.NodeState.RX;
            }
            case "SLEEP" -> {
                return Node.NodeState.SLEEP;
            }
            case "STANDBY" ->{
                return Node.NodeState.STANDBY;
            }
        }
        return null;
    }
    private Node.NodePosition getNodePrevPosition(String nodeId){
        Node.NodePosition nodePosition=null;
        for (Node node : nodes) {
            if (node.getNodeId().equals(nodeId)) {
                nodePosition = node.getPosition();
                break;
            }
        }
        if (nodePosition!=null) return nodePosition;
        else return new Node.NodePosition(0,0,0);

    }
    private Node.DeviceType parseDeviceType(String devString){

        switch (devString) {
            case "EndDevice" -> {
                return Node.DeviceType.END_DEVICE;
            }
            case "Gateway" -> {
                return Node.DeviceType.GATEWAY_DEVICE;
            }
        }
        throw new RuntimeException("Unspecified device type");

    }

    private Node.NodePosition parseNodePosition(String position){

        String[] arr = position.split(",");
        return new Node.NodePosition(
                Double.parseDouble(arr[0]),
                Double.parseDouble(arr[1]),
                Double.parseDouble(arr[2]));

    }

    public double getDuration() {
        return this.maxDuration;
    }
}
