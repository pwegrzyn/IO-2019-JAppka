package pl.edu.agh.io.jappka.controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.stage.Stage;

public class AppController {

    private Stage primaryStage;

    @FXML
    private BarChart barChart;

    public void setPrimaryStageElements(Stage primaryStage) {
        this.primaryStage = primaryStage;
        prepareBarChart();
    }

    @FXML
    private void handleAddApplicationAction(ActionEvent event)  {
        System.out.println("Add App");
    }

    @FXML
    private void handleGenerateReport(ActionEvent event){
        System.out.println("Generate Report");
    }

    @FXML
    private void handleShowCharts(ActionEvent event){
        System.out.println("Show charts");
    }

    private void prepareBarChart() {
        this.barChart.getData().clear();
        barChart.setCursor(Cursor.CROSSHAIR);
        barChart.setTitle("Usage state");
        barChart.setAnimated(false);
        barChart.getXAxis().setTickLabelRotation(90);
        barChart.getXAxis().setLabel("Time");
        barChart.getYAxis().setLabel("Applications");
    }
}
