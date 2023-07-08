package com.example.loravisualizer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PlaybackControlsBox extends HBox {
    PlaybackControlsBox(LoraAnimator.PlayPauseTimer playPauseTimer){
        this.playPauseTimer = playPauseTimer;
        playImage = new Image("/images/play_icon.png",27,27,true,true);
        pauseImage = new Image("/images/pause_icon.png",27,27,true,true);
        generateBox();
    }

    private Image playImage, pauseImage;
    private final LoraAnimator.PlayPauseTimer playPauseTimer;
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
            if(playPauseTimer.isPaused()){
                playPauseTimer.start();
                playPauseImageView.setImage(pauseImage);
            }else{
                playPauseTimer.pause();
                playPauseImageView.setImage(playImage);
            }
        });

        Slider slider = new Slider(0, playPauseTimer.getMaxDuration(), 0.0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setSnapToTicks(true);
        slider.setPadding(new Insets(6));
        sliderBox.getChildren().add(slider);

        slider.setOnMousePressed(event -> {
            sliderDragHold = true;
            playPauseTimer.pause();
            playPauseImageView.setImage(playImage);
            playPauseTimer.setTime(slider.getValue());
        });

        slider.setOnMouseReleased(event -> {
            sliderDragHold = false;
            playPauseTimer.start();
            playPauseImageView.setImage(pauseImage);
        });

        slider.setOnMouseDragged(event -> {
            playPauseTimer.setTime(slider.getValue());
        });



        BorderPane sliderBoxPane = new BorderPane();
        sliderBoxPane.setCenter(sliderBox);

        Label currentDurationLabel = new Label("00.00");
        currentDurationLabel.setPadding(new Insets(6,0,6,6));

        playPauseTimer.setTimeListener(new LoraAnimator.PlayPauseTimer.TimeListener() {
            @Override
            public void onTimeProgress(double time) {
                double timeInSeconds = time/1000;
                if(timeInSeconds <= playPauseTimer.getMaxDuration()){
                    Platform.runLater(() -> {
                        currentDurationLabel.setText(String.format("%." + 3 + "f", timeInSeconds));
                        if(!sliderDragHold)
                           slider.setValue(timeInSeconds);
                    });
                }else{
                    playPauseTimer.reset();
                    playPauseImageView.setImage(playImage);
                }
            }
        });

        double maxDuration = playPauseTimer.getMaxDuration();
        Label totalDurationLabel = new Label(String.valueOf(maxDuration));
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
