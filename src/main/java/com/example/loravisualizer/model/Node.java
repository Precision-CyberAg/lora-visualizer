package com.example.loravisualizer.model;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

        nodeIcon = new Circle(defaultNodeIconRadius);
        nodeIcon.setStroke(Color.BLACK);
        nodeIcon.setStrokeWidth(1);
        nodeIcon.setFill(Color.TRANSPARENT);

        Pane pane = new Pane();
        pane.getChildren().add(nodeIcon);

        deviceTypeText = new Text(deviceType.toString());
        deviceTypeText.setLayoutX(nodeIcon.getLayoutX()-(2*defaultNodeIconRadius));
        deviceTypeText.setLayoutY(nodeIcon.getLayoutY()+(2*defaultNodeIconRadius));

        switch (deviceType){
            case END_DEVICE -> deviceTypeText.setText("ED");
            case GATEWAY_DEVICE -> deviceTypeText.setText("GW");
            case NETWORK_SERVER -> deviceTypeText.setText("NS");
        }

        deviceTypeText.setFont(Font.font("Arial", defaultLabelFontSize));
        pane.getChildren().add(deviceTypeText);



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

        this.getChildren().add(pane);
        this.getChildren().add(nodeIdLabel);

    }


    public void changeNodeIconColor(Color color){
        ((Shape)this.nodeIcon).setFill(color);
    }

    private final Label packetUid;
    private final Label nodeIdLabel;
    private final Circle nodeIcon;

    private final Text deviceTypeText;

    private final Circle nodeIconOuterCircle, nodeIconOuterCircle2;

    public static final double defaultNodeIconRadius = 9;
    private double defaultLabelFontSize = 9;

    private final double defaultNodeHeight = 10;
    private final double defaultNodeWidth = 10;

    public void toggleIdVisibility(boolean b) {
            nodeIdLabel.setVisible(b);
    }

    public void showPacketStartSendingAnimation(String packetUidString) {

        packetUid.setText("PacketUid: "+packetUidString);
        packetUid.setVisible(true);


        nodeIconOuterCircle.setVisible(true);
        nodeIconOuterCircle2.setVisible(true);
    }

    public void hidePacketStartSendingAnimation(){
        packetUid.setVisible(false);
        nodeIconOuterCircle.setVisible(false);
        nodeIconOuterCircle2.setVisible(false);
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

            ((Circle)nodeIcon).setRadius(defaultNodeIconRadius/newValue.doubleValue());

            nodeIcon.setStrokeWidth(1/newValue.doubleValue());

            nodeIdLabel.setStyle("-fx-font-size: "+defaultLabelFontSize/newValue.doubleValue()+"px;");
            nodeIconOuterCircle.setRadius((defaultNodeIconRadius*2)/newValue.doubleValue());
            nodeIconOuterCircle.setStrokeWidth(defaultNodeIconRadius/5/newValue.doubleValue());

            nodeIconOuterCircle2.setRadius((defaultLabelFontSize*4)/ newValue.doubleValue());
            nodeIconOuterCircle2.setStrokeWidth(defaultNodeIconRadius/5/newValue.doubleValue());

            deviceTypeText.setLayoutX(nodeIcon.getLayoutX()-(nodeIcon.getRadius()/2));
            deviceTypeText.setLayoutY(nodeIcon.getLayoutY()+(nodeIcon.getRadius()/2));

            deviceTypeText.setStyle("-fx-font-size: "+defaultLabelFontSize/newValue.doubleValue()+"px;");
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
