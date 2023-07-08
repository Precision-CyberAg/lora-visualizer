package com.example.loravisualizer;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Node;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoraAnimator extends Scene{
    public LoraAnimator(ArrayList<Node> nodes, double graphPaneWidth, double graphPaneHeight, double duration) {
        super(createScene(nodes, graphPaneWidth, graphPaneHeight, duration), 900,450);
    }

    private static ArrayList<Node> nodes;
    public static Parent createScene(ArrayList<Node> nodes, double graphPaneWidth, double graphPaneHeight, double duration){
        LoraAnimator.nodes = nodes;
        BorderPane root = new BorderPane();

        GraphPane graphPane = new GraphPane(nodes);
        graphPane.setPrefSize(graphPaneWidth,graphPaneHeight);

        ZoomableScrollPane pane = new ZoomableScrollPane(graphPane);
        graphPane.setZoomableScrollPane(pane);
        pane.setPadding(new Insets(27));
        root.setCenter(pane);

        PlaybackOptionsBox optionsBox = new PlaybackOptionsBox(graphPane.getCallback());
        root.setLeft(optionsBox);

        PlayPauseTimer playPauseTimer = new PlayPauseTimer(duration, graphPane.getAnimationCallback());

        PlaybackControlsBox controlsBox = new PlaybackControlsBox(playPauseTimer);
        root.setBottom(controlsBox);


        return root;

    }

    public static class PlayPauseTimer{
        private Timer timer;
        private TimerTask task;
        private boolean isPaused;

        private double maxDuration;

        public TimeListener getTimeListener() {
            return timeListener;
        }

        public void setTimeListener(TimeListener timeListener) {
            this.timeListener = timeListener;
        }

        private TimeListener timeListener;

        public boolean isPaused() {
            return isPaused;
        }

        private double interval = 100;


        private double time;

        private GraphPane.AnimationCallback animationCallback;

        public PlayPauseTimer(double maxDuration, GraphPane.AnimationCallback animationCallback){
            this.animationCallback = animationCallback;
            this.maxDuration = maxDuration;
            timer = new Timer();
            time = 0;
            isPaused = true;
            timeListener = null;
        }

        public void start(){
            if(isPaused){
                task = new TimerTask() {
                    @Override
                    public void run() {
                        time = time+interval;
                        if(timeListener!=null) timeListener.onTimeProgress(time);
                        animationCallback.animateEvents(time/1000, interval/1000);
//                        System.out.println("Timer Task is running: "+time+" "+(time/1000));

                        //process node's events and send the animation info to graph pane
                    }
                };
                timer.scheduleAtFixedRate(task, 0, (long) interval);
                isPaused = false;
            }
        }
        public void pause() {
            if (!isPaused && task != null) {
                task.cancel();
                isPaused = true;
            }
        }

        public double getMaxDuration() {
            return maxDuration;
        }

        public void reset() {
            pause();
            time=0;
        }

        public void setTime(double time) {
            this.time = time*1000;
        }

        public interface TimeListener{
            public void onTimeProgress(double time);
        }
    }
}
