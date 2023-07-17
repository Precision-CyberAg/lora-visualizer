package com.example.loravisualizer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class PlaybackControlsBox extends HBox {
    PlaybackControlsBox(AnimatorTimeline timeline, GraphPane graphPane){

        this.timeline = timeline;
        playImage = new Image("/images/play_icon.png",27,27,true,true);
        pauseImage = new Image("/images/pause_icon.png",27,27,true,true);
        generateBox(graphPane);

    }

    private AnimatorTimeline timeline;
    private Image playImage, pauseImage;

    private Boolean playbackStatus = false;
    boolean sliderDragHold = false;
    private void generateBox(GraphPane graphPane){

        VBox vBox = new VBox();

        HBox hBox = new HBox();
        hBox.setSpacing(15);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(3,9,15,9));

        HBox sliderBox = new HBox();

        ImageView playPauseImageView = new ImageView();
        playPauseImageView.setImage(playImage);
        playPauseImageView.setPickOnBounds(true);
        playPauseImageView.setOnMouseClicked(event -> {

            if(!playbackStatus){
                timeline.play();
                playPauseImageView.setImage(pauseImage);
                playbackStatus = true;
            }else{
                timeline.pause();
                playPauseImageView.setImage(playImage);
                playbackStatus = false;
            }

        });


        BorderPane sliderBoxPane = new BorderPane();
        sliderBoxPane.setCenter(sliderBox);

        Label currentDurationLabel = new Label("00.00");
        currentDurationLabel.setPadding(new Insets(6,0,6,6));

        Slider slider = new Slider(0, timeline.getTotalDuration().toSeconds(), 0.0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setSnapToTicks(true);
        slider.setPadding(new Insets(6));
        sliderBox.getChildren().add(slider);

        slider.setOnMousePressed(event -> {
            System.out.println("MOUSE pressed: "+slider.getValue());
            sliderDragHold = true;
            timeline.pause();
        });

        slider.setOnMouseReleased(event -> {
            System.out.println("MOUSE released: "+slider.getValue());
            sliderDragHold = false;
            graphPane.resetGraph();
            LiveLogBox.resetLog();
            timeline.playFrom(new Duration(slider.getValue()*1000));
            if(playbackStatus){
                timeline.play();
            }else{
                timeline.pause();
            }
        });

        slider.setOnMouseDragged(event -> {
            currentDurationLabel.setText(String.format("%." + 3 + "f", slider.getValue()));
        });




        timeline.addListener((observable, oldValue, newValue) -> {
            if(!sliderDragHold){
                currentDurationLabel.setText(String.format("%." + 3 + "f", newValue.toSeconds()));
                slider.setValue(newValue.toSeconds());
            }
        });


        Label totalDurationLabel = new Label(String.valueOf(timeline.getTotalDuration().toSeconds()));
        totalDurationLabel.setPadding(new Insets(6,6,6,0));
        sliderBoxPane.setLeft(currentDurationLabel);
        sliderBoxPane.setRight(totalDurationLabel);


        hBox.getChildren().add(playPauseImageView);
        Label playbackSpeedLabel = new Label();
        playbackSpeedLabel.setText("1X");
        playbackSpeedLabel.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;");
        playbackSpeedLabel.setOnMouseClicked(event -> {
            switch (playbackSpeedLabel.getText()){
                case "1X" -> {
                    timeline.setRate(2);
                    playbackSpeedLabel.setText("2X");
                }
                case "2X" -> {
                    timeline.setRate(4);
                    playbackSpeedLabel.setText("4X");
                }
                case "4X" -> {
                    timeline.setRate(8);
                    playbackSpeedLabel.setText("8X");
                }
                case "8X" -> {
                    timeline.setRate(16);
                    playbackSpeedLabel.setText("16X");
                }
                case "16X" ->{
                    timeline.setRate(1);
                    playbackSpeedLabel.setText("1X");
                }
            }
        });

        hBox.getChildren().add(playbackSpeedLabel);


        vBox.getChildren().add(sliderBoxPane);
        vBox.getChildren().add(hBox);
        vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(9), Insets.EMPTY)));



        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(9));
        this.getChildren().add(vBox);

        HBox.setHgrow(slider, Priority.ALWAYS);
        HBox.setHgrow(vBox, Priority.ALWAYS);
    }
}
