package com.example.loravisualizer;


import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;
import com.example.loravisualizer.model.events.MobilityTraceCourseChangeEvent;
import com.example.loravisualizer.model.events.PhyEndDeviceStateChangeEvent;
import com.example.loravisualizer.model.events.PhyTraceReceivedPacketEvent;
import com.example.loravisualizer.model.events.PhyTraceStartSendingEvent;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GraphPane extends Pane {

    TreeMap<String, String> packetUidMap;
    GraphPane(ArrayList<Node> nodes) {
        packetUidMap = new TreeMap<String, String>();
        this.nodes = nodes;
        heightProperty().addListener((observable, oldValue, newValue) -> {
            generateGraph();
        });
        widthProperty().addListener((observable, oldValue, newValue) -> {
            generateGraph();
        });
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
        zoomCallback = zoomFactor -> generateGraph();
        graphLabels = new ArrayList<>();
        graphLines = new ArrayList<>();

        animationCallback = (time, interval) -> {
            for(Node node: nodes){
                new Thread(()->{
                    for(Event event: node.getEvents()){
                        if(event.getEventTime() <= time && event.getEventTime() >= (time - interval)){
                            System.out.println("Time: "+time+"Event for: "+node.getNodeId()+", "+event.getClass());
                            switch (event.getEventType()){
                                case MOBILITY_TRACE_COURSE_CHANGE -> {
                                    MobilityTraceCourseChangeEvent mobilityTraceCourseChangeEvent = (MobilityTraceCourseChangeEvent) event;
                                    Node.NodePosition currentPos = mobilityTraceCourseChangeEvent.getCurrentPosition();
                                    Platform.runLater(() -> node.setNodePosition(currentPos.getX(), currentPos.getY(), currentPos.getZ()));
                                }
                                case PHY_END_DEVICE_STATE -> {
                                    PhyEndDeviceStateChangeEvent stateChangeEvent = (PhyEndDeviceStateChangeEvent) event;
                                    Platform.runLater(() -> {
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
                                    });
                                }
                                case PHY_TRACE_START_SENDING -> {
                                    Platform.runLater(() -> {
                                        PhyTraceStartSendingEvent sendingEvent = (PhyTraceStartSendingEvent) event;
                                        packetUidMap.put(sendingEvent.getPacketUid(), node.getNodeId());
                                        node.showPacketStartSendingAnimation(sendingEvent.getPacketUid());
                                    });
                                }
                                case PHY_TRACE_RECEIVED_PACKET -> {
                                    PhyTraceReceivedPacketEvent receivedPacketEvent = (PhyTraceReceivedPacketEvent) event;
                                    String packetUid = receivedPacketEvent.getPacketUid();
                                    String nodeId = packetUidMap.get(packetUid);
                                    if(nodeId!=null){
                                        showPacketReceivedAnimation(nodeId, node.getNodeId(), packetUid);
                                    }
                                }
                            }
                        }
                    }
                }).start();

            }
        };
    }

    private void showPacketReceivedAnimation(String senderNodeId, String receiverNodeId, String packetUid) {
        Node senderNode = null, receiverNode = null;
        for(Node node: nodes)
            if(node.getNodeId().equals(senderNodeId)){
                senderNode = node;
                break;
            }
        for(Node node: nodes)
            if(node.getNodeId().equals(receiverNodeId)){
                receiverNode = node;
                break;
            }
        if(senderNode !=null  || receiverNode != null){

            Line line = new Line(senderNode.getLayoutX(), senderNode.getLayoutY(), receiverNode.getLayoutX(), receiverNode.getLayoutY());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(Node.defaultNodeIconRadius/5/this.getScaleX());

            double angle = Math.atan2((receiverNode.getLayoutY() - senderNode.getLayoutY()), (receiverNode.getLayoutX() - senderNode.getLayoutX()));

            double arrowLength = calculateLength(senderNode.getLayoutX(), senderNode.getLayoutY(), receiverNode.getLayoutX(), receiverNode.getLayoutY());

            arrowLength = 36;
            Polygon arrowTip = new Polygon(
                    0, 0,
                    arrowLength,
                    -arrowLength/2,
                    arrowLength,
                    arrowLength/2
            );

            Label label = new Label("R");
            label.setStyle("-fx-font-size: "+defaultLabelFontSize/getScaleX()+"px;");

            arrowTip.setRotate(Math.toDegrees(-angle));
            arrowTip.setTranslateX(line.getEndX());
            arrowTip.setTranslateY(line.getEndY());

            Platform.runLater(() -> {
                this.getChildren().add(line);
                this.getChildren().add(label);
            });
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        GraphPane.this.getChildren().remove(line);
                        GraphPane.this.getChildren().remove(label);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }
    }

    private static double calculateLength(double x1, double y1, double x2, double y2) {
        // Calculate the horizontal and vertical differences
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calculate the square of the differences
        double dxSquared = dx * dx;
        double dySquared = dy * dy;

        // Calculate the sum of the squares and take the square root
        double length = Math.sqrt(dxSquared + dySquared);

        return length;
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

    private final GraphPaneZoomCallback zoomCallback;
    private List<Line> graphLines;
    private List<Text> graphLabels;
    private ArrayList<Node> nodes;
    private Boolean graphLinesVisible = true;

    private double space = 500;

    private double defaultLabelFontSize = 9;

    private ZoomableScrollPane scrollPane;
    private double labelFontSize = 9;
    private Boolean graphLabelsVisible = true;

    private AnimationCallback animationCallback;

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
        for (Node node : nodes)
            node.setGraphPaneScaleProperty(
                    scaleXProperty(),
                    defaultLabelFontSize
            );

    }

    public void setZoomableScrollPane(ZoomableScrollPane pane) {
        this.scrollPane = pane;
    }

    public AnimationCallback getAnimationCallback() {
        return animationCallback;
    }

    public interface AnimationCallback{
        public void animateEvents(double time, double interval);
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
