package com.example.loravisualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EventTimeline extends Application {

    private static final int START_TIME = 1; // Start time in seconds
    private static final int END_TIME = 10; // End time in seconds

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("No event");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 200, 100);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create a timeline
        Timeline timeline = new Timeline();

        // Add keyframes for each event time
        for (int time = START_TIME; time <= END_TIME; time++) {
            int finalTime = time;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(time), event -> {
                label.setText("Event at time " + finalTime);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        // Play the timeline
        timeline.play();
    }
}
