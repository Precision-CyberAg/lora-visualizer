package com.example.loravisualizer.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;

public class Node extends Pane {
    public Node(String nodeId, String deviceAddress,  DeviceType deviceType, NodePosition position) {
        this.nodeId = nodeId;
        this.position = position;
        this.deviceAddress = deviceAddress;
        this.deviceType = deviceType;
        events = new ArrayList<>();

        this.setLayoutX(position.x);
        this.setLayoutY(position.y);
        this.nodeIdLabel = new Label(nodeId+", ("+position.x+", "+position.y+")");

        if(deviceType==DeviceType.END_DEVICE){
            nodeIcon = new Circle(defaultNodeIconRadius, Color.GREY);
        }else if(deviceType==DeviceType.GATEWAY_DEVICE){
            nodeIcon = new Rectangle(defaultNodeWidth, defaultNodeHeight, Color.GREY);
        }else{
            nodeIcon = new Circle(defaultNodeIconRadius, Color.RED);
        }
        Pane pane = new Pane();
        pane.getChildren().add(nodeIcon);
        nodeIconOuterCircle = new Circle(defaultNodeIconRadius*2, Color.BLACK);
        nodeIconOuterCircle.setVisible(false);
        nodeIconOuterCircle.setStrokeWidth(defaultNodeIconRadius/5);
        nodeIconOuterCircle.setFill(null);
        nodeIconOuterCircle.setStroke(Color.BLACK);


        nodeIconOuterCircle2 = new Circle(defaultNodeIconRadius*4, Color.BLACK);
        nodeIconOuterCircle2.setVisible(false);
        nodeIconOuterCircle2.setStrokeWidth(defaultNodeIconRadius/5);
        nodeIconOuterCircle2.setFill(null);
        nodeIconOuterCircle2.setStroke(Color.BLACK);

        pane.getChildren().add(nodeIconOuterCircle);
        pane.getChildren().add(nodeIconOuterCircle2);


        packetUid = new Label();

        pane.getChildren().add(packetUid);


//        VBox vBox = new VBox();
//        vBox.getChildren().add(pane);
//        vBox.getChildren().add(nodeIdLabel);
        this.getChildren().add(pane);
        this.getChildren().add(nodeIdLabel);
    }

    public void changeNodeIconColor(Color color){
        Platform.runLater(() -> ((Shape)this.nodeIcon).setFill(color));
    }

    private final Label packetUid;
    private final Label nodeIdLabel;
    private final javafx.scene.Node nodeIcon;

    private final Circle nodeIconOuterCircle, nodeIconOuterCircle2;

    public static final double defaultNodeIconRadius = 5;

    private final double defaultNodeHeight = 10;
    private final double defaultNodeWidth = 10;

    public void toggleIdVisibility(boolean b) {
            nodeIdLabel.setVisible(b);
    }

    public KeyFrame showPacketStartSendingAnimation(String packetUidString, Duration keyFrameDuration) {

        packetUid.setText("PacketUid: "+packetUidString);
        packetUid.setVisible(true);


        nodeIconOuterCircle.setVisible(true);
        nodeIconOuterCircle2.setVisible(true);

        Duration duration = new Duration(keyFrameDuration.toMillis()+1000);

        return new KeyFrame(duration, event -> {
            packetUid.setVisible(false);
            nodeIconOuterCircle.setVisible(false);
            nodeIconOuterCircle2.setVisible(false);
        });
    }



    public enum NodeState{
        TX,
        RX,
        SLEEP,
        STANDBY
    }
    public enum DeviceType{
        END_DEVICE,
        GATEWAY_DEVICE,
        NETWORK_SERVER
    }

    private final String nodeId;
    private final NodePosition position;
    private final String deviceAddress;
    private final DeviceType deviceType;

    public ArrayList<Event> getEvents() {
        return events;
    }

    private final ArrayList<Event> events;

    public String getNodeId() {
        return nodeId;
    }

    public NodePosition getPosition() {
        return position;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setNodePosition(double x, double y, double z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;

        this.setLayoutX(x);
        this.setLayoutY(y);

        Platform.runLater(() -> this.nodeIdLabel.setText(nodeId+", ("+this.position.x+", "+this.position.y+")"));
    }

    public void addEvent(Event event){
        this.events.add(event);
    }
    public static class NodePosition{
        private double x, y, z;

        public NodePosition(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
    public void setGraphPaneScaleProperty(DoubleProperty val, double defaultLabelFontSize){
        val.addListener((observable, oldValue, newValue) -> {
            switch (deviceType){
                case END_DEVICE -> {
                    ((Circle)nodeIcon).setRadius(defaultNodeIconRadius/newValue.doubleValue());


                }
                case GATEWAY_DEVICE -> {
                    ((Rectangle)nodeIcon).setHeight(defaultNodeHeight/newValue.doubleValue());
                    ((Rectangle)nodeIcon).setWidth(defaultNodeWidth/newValue.doubleValue());
                }
            }
            nodeIdLabel.setStyle("-fx-font-size: "+defaultLabelFontSize/newValue.doubleValue()+"px;");
            nodeIconOuterCircle.setRadius((defaultNodeIconRadius*2)/newValue.doubleValue());
            nodeIconOuterCircle.setStrokeWidth(defaultNodeIconRadius/5/newValue.doubleValue());

            nodeIconOuterCircle2.setRadius((defaultLabelFontSize*4)/ newValue.doubleValue());
            nodeIconOuterCircle2.setStrokeWidth(defaultNodeIconRadius/5/newValue.doubleValue());

            packetUid.setStyle("-fx-font-size: "+defaultLabelFontSize/newValue.doubleValue()+"px;");
        });
    }
    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;

        Node node = (Node) obj;
        return Objects.equals(this.nodeId, node.nodeId) &&
                Objects.equals(this.deviceAddress, node.deviceAddress) &&
                this.deviceType == node.deviceType;
    }
}
