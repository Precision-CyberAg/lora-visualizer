package com.example.loravisualizer;

import com.example.loravisualizer.model.Node;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoraAnimator extends Scene{
    public LoraAnimator(ArrayList<Node> nodes, double graphPaneWidth, double graphPaneHeight, LoraTimeline loraTimeline) {
        super(createScene(nodes, graphPaneWidth, graphPaneHeight, loraTimeline), 900,700);
    }

    private static ArrayList<Node> nodes;
    public static Parent createScene(ArrayList<Node> nodes, double graphPaneWidth, double graphPaneHeight, LoraTimeline loraTimeline){
        LoraAnimator.nodes = nodes;
        BorderPane root = new BorderPane();


        AnimatorTimeline timeline = new AnimatorTimeline();


        GraphPane graphPane = new GraphPane(nodes, loraTimeline, timeline);
        graphPane.setPrefSize(graphPaneWidth,graphPaneHeight);

        ZoomableScrollPane pane = new ZoomableScrollPane(graphPane);
        graphPane.setZoomableScrollPane(pane);
        pane.setPadding(new Insets(27));
        root.setCenter(pane);

        PlaybackOptionsBox optionsBox = new PlaybackOptionsBox(graphPane.getCallback());
        root.setLeft(optionsBox);

        PlaybackControlsBox controlsBox = new PlaybackControlsBox(timeline, graphPane);
        root.setBottom(controlsBox);

        root.setTop(MenuBarManager.createMenuBar());


        return root;

    }
}
