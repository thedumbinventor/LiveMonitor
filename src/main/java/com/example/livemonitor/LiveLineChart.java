package com.example.livemonitor;

import javafx.animation.AnimationTimer;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.fazecast.jSerialComm.SerialPort;

class LiveLineChart extends LineChart<Number, Number> {
    private static final int MAX_DATA_POINTS = 1100;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> series;
    private volatile boolean plottingEnabled = true;



    private final Queue<XYChart.Data<Number, Number>> dataBuffer = new LinkedList<>();

    public LiveLineChart(String name) {
        super(new NumberAxis(0, MAX_DATA_POINTS, 0), new NumberAxis(0, 1024, 0));
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);
      ((NumberAxis) getYAxis()).setAutoRanging(true);
        this.series = new XYChart.Series<>();
        getData().add(series);
        setAnimated(false);
        setCreateSymbols(false);
        createSymbolsProperty().set(false);
        setLegendVisible(false);

        this.series.setName("");

        // After creating the series, set its style
        series.getNode().setStyle("-fx-stroke-width: 1px; -fx-stroke: darkpurple;");

        setHorizontalGridLinesVisible(true);
        prepareTimeline();
        loadDataFromFileToBuffer(name);
    }

    private void logError(Exception ex) {
        // Log the error using a logging framework
        System.err.println(ex.getMessage());
    }

    private void notifyUser(String message) {

    }

    private void prepareTimeline() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (plottingEnabled) {
                    synchronized (dataBuffer) {
                        if (!dataBuffer.isEmpty()) {
                            int pointsToAdd =2 ;  // or any other number based on your needs1
                            for (int i = 0; i < pointsToAdd && !dataBuffer.isEmpty(); i++) {
                                XYChart.Data<Number, Number> dataPoint = dataBuffer.poll();
                                series.getData().add(dataPoint);
                                ((NumberAxis) getXAxis()).setLowerBound(dataPoint.getXValue().intValue() - 1099);
                                ((NumberAxis) getXAxis()).setUpperBound(dataPoint.getXValue().intValue());
                                readData();
                            }

                            // If there are more than 500 points plotted, remove the oldest one
                            if (series.getData().size() > 1100) {
                                series.getData().remove(0);
                            }


                        }
                    }
                }
            }
        }.start();
    }
    public void pausePlotting() {
        this.plottingEnabled = false;
    }

    public void resumePlotting() {
        this.plottingEnabled = true;
    }

    private void loadDataFromFileToBuffer(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            synchronized (dataBuffer) {
                for (String line : lines) {
                    int value = (int) Double.parseDouble(line.trim());
                    dataBuffer.add(new XYChart.Data<>(xSeriesData++, value));
                }
            }
        } catch (Exception e) {
            logError(e);
            notifyUser("Error reading data from the file.");
        }
    }

    InputStream inputStream;

    OutputStream outputStream;
    int baudRate = 115200;
    int StartBIT = 8;
    int StopBIT = 1;
    int ParityBIT = 0;


    void readData(){

        SerialPort serialPort = SerialPort.getCommPort("COM18");
        serialPort.setComPortParameters(baudRate, StartBIT, StopBIT, ParityBIT);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
        if(serialPort.isOpen()){
        this.inputStream=serialPort.getInputStream();
        this.outputStream=serialPort.getOutputStream();}

        if (serialPort.openPort()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
                ArrayList<String> dataLines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    dataLines.add(line);
                    if(dataLines.size()>500)
                    {
                        System.out.println(dataLines);
                        System.exit(-1);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Failed to open the serial port.");
        }

    }




}
