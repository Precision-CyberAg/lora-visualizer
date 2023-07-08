package com.example.loravisualizer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PlaybackOptionsBox extends VBox {
    PlaybackOptionsBox(GraphPane.GraphPaneCallback graphPaneCallback){
        this.graphPaneCallback = graphPaneCallback;
        generateBox();
    }

    private final GraphPane.GraphPaneCallback graphPaneCallback;

    private void generateBox(){
        VBox iconsLayout = new VBox();

        iconsLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(9), Insets.EMPTY)));
        iconsLayout.setSpacing(15);
        iconsLayout.setPadding(new Insets(9));

        ImageView axisLabelToggleImageView = new ImageView();
        axisLabelToggleImageView.setImage(new Image("/images/number_icon.png",27,27,true,true));
        axisLabelToggleImageView.setPickOnBounds(true);
        Pane axisLabelToggleImageViewPane = new Pane(axisLabelToggleImageView);
        Line line = new Line(3, 3, axisLabelToggleImageViewPane.getWidth(), axisLabelToggleImageViewPane.getHeight());
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
        axisLabelToggleImageViewPane.setOnMouseClicked(event -> {
            if (graphPaneCallback.numberIconClicked()) {
                axisLabelToggleImageViewPane.getChildren().remove(line);
            } else {
                axisLabelToggleImageViewPane.getChildren().add(line);
            }
            line.setEndX(axisLabelToggleImageViewPane.getWidth()-3);
            line.setEndY(axisLabelToggleImageViewPane.getHeight()-3);
        });


        ImageView gridLinesToggleImageView = new ImageView();
        gridLinesToggleImageView.setImage(new Image("/images/grid_lines.png", 27,27,true, true));
        gridLinesToggleImageView.setPickOnBounds(true);
        Pane gridLinesToggleImageViewPane = new Pane(gridLinesToggleImageView);
        Line line2 = new Line(3, 3, gridLinesToggleImageViewPane.getWidth(), gridLinesToggleImageViewPane.getHeight());
        line2.setStroke(Color.RED);
        line2.setStrokeWidth(2);
        gridLinesToggleImageViewPane.setOnMouseClicked(event -> {
            if(graphPaneCallback.graphLinesIconClicked()){
                gridLinesToggleImageViewPane.getChildren().remove(line2);
            }else{
                gridLinesToggleImageViewPane.getChildren().add(line2);
            }
            line2.setEndX(gridLinesToggleImageViewPane.getWidth()-3);
            line2.setEndY(gridLinesToggleImageViewPane.getHeight()-3);
        });

        ImageView zoomInImageView = new ImageView();
        zoomInImageView.setImage(new Image("/images/zoom_in.png", 27,27,true, true));
        zoomInImageView.setPickOnBounds(true);
        zoomInImageView.setOnMouseClicked(event -> {
            graphPaneCallback.zoomInClicked();
        });

        ImageView zoomOutImageView = new ImageView();
        zoomOutImageView.setImage(new Image("/images/zoom_out.png", 27,27,true, true));
        zoomOutImageView.setPickOnBounds(true);
        zoomOutImageView.setOnMouseClicked(event -> {
            graphPaneCallback.zoomOutClicked();
        });

        ImageView idImageView = new ImageView();
        idImageView.setImage(new Image("/images/id_icon.png",27,27,true,true));
        idImageView.setPickOnBounds(true);
        Pane idImageViewPane = new Pane(idImageView);
        Line line3 = new Line(3,3,idImageViewPane.getWidth(),idImageViewPane.getHeight());
        line3.setStroke(Color.RED);
        line3.setStrokeWidth(2);
        idImageView.setOnMouseClicked(event -> {
            if(graphPaneCallback.iDClicked()){
                idImageViewPane.getChildren().remove(line3);
            }else{
                idImageViewPane.getChildren().add(line3);
            }
            line3.setEndX(idImageViewPane.getWidth()-3);
            line3.setEndY(idImageViewPane.getHeight()-3);
        });

        iconsLayout.getChildren().add(axisLabelToggleImageViewPane);
        iconsLayout.getChildren().add(gridLinesToggleImageViewPane);
        iconsLayout.getChildren().add(zoomInImageView);
        iconsLayout.getChildren().add(zoomOutImageView);
        iconsLayout.getChildren().add(idImageViewPane);

        this.getChildren().add(iconsLayout);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(9));
    }
}
