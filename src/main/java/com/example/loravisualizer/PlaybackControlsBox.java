package com.example.loravisualizer;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class PlaybackControlsBox extends HBox {
    PlaybackControlsBox(Timeline timeline){

        this.timeline = timeline;
        playImage = new Image("/images/play_icon.png",27,27,true,true);
        pauseImage = new Image("/images/pause_icon.png",27,27,true,true);
        generateBox();

    }

    private Timeline timeline;
    private Image playImage, pauseImage;

    private Boolean playbackStatus = false;
    boolean sliderDragHold = false;
    private void generateBox(){

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

        Slider slider = new Slider(0, timeline.getTotalDuration().toSeconds(), 0.0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setSnapToTicks(true);
        slider.setPadding(new Insets(6));
        sliderBox.getChildren().add(slider);

        slider.setOnMousePressed(event -> {
            sliderDragHold = true;
            playPauseImageView.setImage(playImage);
            timeline.playFrom(new Duration(slider.getValue()*1000));
        });

        slider.setOnMouseReleased(event -> {
            sliderDragHold = false;
            timeline.play();
            playPauseImageView.setImage(pauseImage);
        });

        slider.setOnMouseDragged(event -> {
            timeline.playFrom(new Duration(slider.getValue()*1000));
        });



        BorderPane sliderBoxPane = new BorderPane();
        sliderBoxPane.setCenter(sliderBox);

        Label currentDurationLabel = new Label("00.00");
        currentDurationLabel.setPadding(new Insets(6,0,6,6));

        timeline.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            currentDurationLabel.setText(String.format("%." + 3 + "f", newValue.toSeconds()));
            if(!sliderDragHold){
                slider.setValue(newValue.toSeconds());
            }
        });

        Label totalDurationLabel = new Label(String.valueOf(timeline.getTotalDuration().toSeconds()));
        totalDurationLabel.setPadding(new Insets(6,6,6,0));
        sliderBoxPane.setLeft(currentDurationLabel);
        sliderBoxPane.setRight(totalDurationLabel);


        hBox.getChildren().add(playPauseImageView);


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
