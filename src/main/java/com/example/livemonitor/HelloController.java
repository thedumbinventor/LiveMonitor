package com.example.livemonitor;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private VBox chartContainer1;


    private LiveLineChart fillChart;
    private LiveLineChart cleanChart;

    @FXML
    public void initialize() {
        fillChart = new LiveLineChart("v6_data(2).txt");
        cleanChart = new LiveLineChart("v6_data(2).txt");

        chartContainer1.getChildren().add(fillChart);

    }

    @FXML
    private void startPlot() {
        fillChart.resumePlotting();

    }

    @FXML
    private void stopPlot() {
        fillChart.pausePlotting();

    }
}
