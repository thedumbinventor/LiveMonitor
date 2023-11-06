package com.example.livemonitor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication2 extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("12x1 Live Line Plotters");

        VBox vBox = new VBox(0);

        for (int i = 0; i < 12; i++) {
            LiveLineChart chart = new LiveLineChart("v6_data(2).txt");
            chart.setMinHeight(450);
            chart.setPrefHeight(450);
            chart.setMaxHeight(450);
            chart.setCreateSymbols(false);


            vBox.getChildren().add(chart);
            VBox.setMargin(chart, new Insets(-30, 1, 1, 1));
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);
        scrollPane.setFitToWidth(true);

        stage.setScene(new Scene(scrollPane, 800, 800));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
