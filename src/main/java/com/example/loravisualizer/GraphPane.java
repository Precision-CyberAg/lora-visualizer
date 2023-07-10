package com.example.loravisualizer;


import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;
import com.example.loravisualizer.model.events.MobilityTraceCourseChangeEvent;
import com.example.loravisualizer.model.events.PhyEndDeviceStateChangeEvent;
import com.example.loravisualizer.model.events.PhyTraceReceivedPacketEvent;
import com.example.loravisualizer.model.events.PhyTraceStartSendingEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GraphPane extends Pane {

    TreeMap<String, Node> packetUidMap;
    GraphPane(ArrayList<Node> nodes, LoraTimeline loraTimeline, Timeline timeline) {
        packetUidMap = new TreeMap<String, Node>();
        this.nodes = nodes;

        callback = new GraphPaneCallback() {
            @Override
            public boolean graphLinesIconClicked() {
                if (graphLinesVisible) {
                    for (Line line : graphLines) line.setVisible(false);
                    graphLinesVisible = false;
                } else {
                    for (Line line : graphLines) line.setVisible(true);
                    graphLinesVisible = true;
                }
                return graphLinesVisible;
            }

            @Override
            public boolean numberIconClicked() {
                if (graphLabelsVisible) {
                    for (Text text : graphLabels) text.setVisible(false);
                    graphLabelsVisible = false;
                } else {
                    for (Text text : graphLabels) text.setVisible(true);
                    graphLabelsVisible = true;
                }
                return graphLabelsVisible;
            }

            @Override
            public void zoomInClicked() {
                if (scrollPane != null) {
                    scrollPane.onScroll(3, new Point2D(0, 0));
                }
            }

            @Override
            public void zoomOutClicked() {
                if (scrollPane != null) {
                    scrollPane.onScroll(-3, new Point2D(GraphPane.this.getWidth() / 2, GraphPane.this.getHeight() / 2));
                }
            }

            @Override
            public boolean iDClicked() {
                nodeIdVisible = !nodeIdVisible;
                for (Node node : nodes) node.toggleIdVisibility(nodeIdVisible);

                return nodeIdVisible;
            }
        };

//        zoomCallback = zoomFactor -> generateGraph();
        graphLabels = new ArrayList<>();
        graphLines = new ArrayList<>();

        this.timeline = timeline;
        for(LoraTimeline.TimelineData timelineData: loraTimeline.getTimelineData()){
            Duration keyFrameDuration = new Duration(timelineData.getEventTime()*1000);
            KeyFrame keyFrame = new KeyFrame(keyFrameDuration, keyFrameEvent -> {
                Event event = timelineData.getEvent();
                Node node = timelineData.getNode();

                switch (event.getEventType()){
                    case PHY_END_DEVICE_STATE -> {
                        PhyEndDeviceStateChangeEvent stateChangeEvent = (PhyEndDeviceStateChangeEvent) event;
                            switch (stateChangeEvent.getCurrState()){
                                case SLEEP -> {
                                    node.changeNodeIconColor(Color.GREY);
                                }
                                case RX -> {
                                    node.changeNodeIconColor(Color.GREEN);
                                }
                                case STANDBY -> {
                                    node.changeNodeIconColor(Color.YELLOW);
                                }
                                case TX -> {
                                    node.changeNodeIconColor(Color.RED);
                                }
                            }
                    }
                    case PHY_TRACE_START_SENDING -> {
                        PhyTraceStartSendingEvent sendingEvent = (PhyTraceStartSendingEvent) event;
                        packetUidMap.put(sendingEvent.getPacketUid(), node);
                        this.timeline.getKeyFrames().add(
                                node.showPacketStartSendingAnimation(
                                        sendingEvent.getPacketUid(),
                                        keyFrameDuration));
                    }
                    case PHY_TRACE_RECEIVED_PACKET -> {
                        PhyTraceReceivedPacketEvent receivedPacketEvent = (PhyTraceReceivedPacketEvent) event;
                        String packetUid = receivedPacketEvent.getPacketUid();
                        Node senderNode = packetUidMap.get(packetUid);
                        if(senderNode!=null){
                            this.timeline.getKeyFrames().add(
                                    showPacketReceivedAnimation(
                                            senderNode,
                                            node,
                                            packetUid,
                                            keyFrameDuration)
                            );
                        }
                    }
                    case MOBILITY_TRACE_COURSE_CHANGE -> {
                        MobilityTraceCourseChangeEvent mobilityTraceCourseChangeEvent = (MobilityTraceCourseChangeEvent) event;
                        Node.NodePosition currentPos = mobilityTraceCourseChangeEvent.getCurrentPosition();
                        node.setNodePosition(currentPos.getX(), currentPos.getY(), currentPos.getZ());
                    }
                }

            });
            this.timeline.getKeyFrames().add(keyFrame);

            play = new Button("PLAY");
            play.setPrefHeight(100);
            play.setPrefWidth(100);
            play.setOnMouseClicked(event -> {

                this.timeline.playFromStart();
            });
        }

        generateGraph();

    }

    Button play;

    Timeline timeline;

    private KeyFrame showPacketReceivedAnimation(Node senderNode, Node receiverNode, String packetUid, Duration keyFrameDuration) {

            Line line = new Line(senderNode.getLayoutX(), senderNode.getLayoutY(), receiverNode.getLayoutX(), receiverNode.getLayoutY());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(Node.defaultNodeIconRadius/5/this.getScaleX());

            Label label = new Label("R");
            label.setStyle("-fx-font-size: "+defaultLabelFontSize/getScaleX()+"px;");

            this.getChildren().add(line);
            this.getChildren().add(label);

        return new KeyFrame(new Duration(keyFrameDuration.toMillis()+1000), event -> {

            this.getChildren().remove(line);
            this.getChildren().remove(label);

        });

    }

    public double getScaledFont() {
        return defaultLabelFontSize / getScaleX();
    }

    public GraphPaneCallback getCallback() {
        return callback;
    }

    public GraphPaneZoomCallback getZoomCallback() {
        return zoomCallback;
    }

    private final GraphPaneCallback callback;

    private boolean nodeIdVisible = true;

    private GraphPaneZoomCallback zoomCallback;
    private List<Line> graphLines;
    private List<Text> graphLabels;
    private ArrayList<Node> nodes;
    private Boolean graphLinesVisible = true;

    private double space = 500;

    private double defaultLabelFontSize = 9;

    private ZoomableScrollPane scrollPane;
    private double labelFontSize = 9;
    private Boolean graphLabelsVisible = true;


    private void createLinesAndLabels(Pane parent, double space) {
        System.out.println("Space: " + space);
        double paneHeight = parent.getHeight();
        double paneWidth = parent.getWidth();

        int numberOfHLines = (int) (paneHeight / space);
        int numberOfVLines = (int) (paneWidth / space);

        for (int i = 0; i < numberOfHLines; i++) {
            double y = i * space;
            Line line = createLine(-paneWidth, y, paneWidth, y);
            Text label = createLabel(String.valueOf((int) y), 0, y);
            parent.getChildren().add(line);
            parent.getChildren().add(label);

            Line line2 = createLine(-paneWidth, -y, paneWidth, -y);
            Text label2 = createLabel(String.valueOf((int) -y), 0, -y);
            parent.getChildren().add(line2);
            parent.getChildren().add(label2);

        }

        for (int i = 0; i < numberOfVLines; i++) {
            double x = i * space;
            Line line = createLine(x, -paneHeight, x, paneHeight);
            Text label = createLabel(String.valueOf((int) x), x, 0);
            parent.getChildren().add(line);
            parent.getChildren().add(label);

            Line line2 = createLine(-x, -paneHeight, -x, paneHeight);
            Text label2 = createLabel(String.valueOf((int) -x), -x, 0);
            parent.getChildren().add(line2);
            parent.getChildren().add(label2);

        }


    }

    private Line createLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(1);
        line.setStyle("-fx-stroke: black;");
        line.setVisible(graphLinesVisible);
        graphLines.add(line);
        return line;
    }

    private Text createLabel(String text, double x, double y) {
        Text label = new Text(x, y, text);
        label.setStyle("-fx-font-size: " + defaultLabelFontSize / getScaleX() + "px; -fx-font-weight: bold;");
        label.setVisible(graphLabelsVisible);
        graphLabels.add(label);
        return label;
    }

    public void generateGraph() {
        getChildren().clear();
        graphLines.clear();
        graphLabels.clear();
        System.out.println("Graph reset!!!");
        createLinesAndLabels(this, space);
        System.out.println(space);
        getChildren().addAll(nodes);

        getChildren().add(play);
        for (Node node : nodes)
            node.setGraphPaneScaleProperty(
                    scaleXProperty(),
                    defaultLabelFontSize
            );

    }

    public void setZoomableScrollPane(ZoomableScrollPane pane) {
        this.scrollPane = pane;
    }


    public interface GraphPaneCallback {
        boolean graphLinesIconClicked();

        boolean numberIconClicked();

        void zoomInClicked();

        void zoomOutClicked();

        boolean iDClicked();
    }

    public interface GraphPaneZoomCallback {
        void onZoom(double zoomFactor);
    }
}
