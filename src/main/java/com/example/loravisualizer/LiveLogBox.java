package com.example.loravisualizer;

import com.example.loravisualizer.model.Event;
import com.example.loravisualizer.model.Event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LiveLogBox extends Stage {
    private static LiveLogBox logBox;
    private static TextArea logArea;

    private static final StringBuilder stringBuilder = new StringBuilder();

    public static LiveLogBox getLogBox(){
        if(logBox == null){
            generateBox();
        }
        return logBox;
    }

    public static void resetLog(){
        stringBuilder.delete(0, stringBuilder.length());
        if(logArea!=null)
            logArea.setText(stringBuilder.toString());
    }

    public static synchronized void appendLog(double time, EventType eventType, String data){
        stringBuilder.append("[").append(String.format("%.6f", time)).append("]")
                .append("\t")
                .append(eventType)
                .append(System.lineSeparator())
                .append("\t\t\t")
                .append(data)
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        if(logArea!=null){
            logArea.setText(stringBuilder.toString());
            logArea.positionCaret(logArea.getText().length());
        }
    }

    private static String getEventTypeString(EventType eventType){
        int s = 36 - eventType.toString().length();
        stringBuilder
                .append(eventType)
                .append(" ".repeat(s));
        return "";
    }





    public static void generateBox(){

        logBox = new LiveLogBox();

        VBox vBox = new VBox();

        Label label = new Label("Log");

        logArea = new TextArea();
        logArea.setText(stringBuilder.toString());
        logArea.setEditable(false);

        vBox.setPadding(new Insets(9));
        vBox.getChildren().add(label);
        vBox.getChildren().add(logArea);

        VBox.setVgrow(logArea, Priority.ALWAYS);

        Scene scene = new Scene(vBox, 700,600);
        logBox.setScene(scene);
        logBox.setOnCloseRequest(event -> MenuBarManager.setLogMenuItemChecked(false));

    }
}
