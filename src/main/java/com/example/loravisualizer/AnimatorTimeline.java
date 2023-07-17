package com.example.loravisualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class AnimatorTimeline {

    private Timeline timeline;
    private Integer playbackRate;

    AnimatorTimeline(){
        this.timeline = new Timeline();
        this.playbackRate = 1;
    }

    public void play(){
        this.timeline.play();
    }

    public void setRate(Integer playbackRate){
        this.playbackRate = playbackRate;
        this.timeline.setRate(playbackRate);
    }

    public void playFrom(Duration duration){
        double skipRate = (duration.toMillis()/timeline.getTotalDuration().toMillis())*1000;
        timeline.stop();
        timeline.setRate(skipRate);
        timeline.currentTimeProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                System.out.println("new Val: " + newValue + "\tdur: " + duration);
                if (newValue.greaterThanOrEqualTo(duration)) {
                    timeline.pause();
                    timeline.setRate(playbackRate);
                    timeline.play();
                    timeline.currentTimeProperty().removeListener(this);
                    return;
                }
                timeline.setRate(Math.max(playbackRate, (1 - (newValue.toMillis() / duration.toMillis())) * skipRate));
            }
        });
        timeline.play();
    }

    public void pause(){
        this.timeline.pause();
    }


    public <E> ObservableList<KeyFrame> getKeyFrames() {
        return this.timeline.getKeyFrames();
    }

    public Timeline getTimeline(){
        return this.timeline;
    }
    public Duration getTotalDuration() {
        return this.timeline.getTotalDuration();
    }

    public Observable currentTimeProperty() {
        return this.timeline.currentTimeProperty();

    }

    public void addListener(ChangeListener<Duration> changeListener){
        this.timeline.currentTimeProperty().addListener(changeListener);
    }
}
