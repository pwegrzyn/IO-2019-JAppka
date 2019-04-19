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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        prepareBarChart();
    }

    @FXML
    private void handleAddApplicationAction(ActionEvent event)  {
        System.out.println("Works");
    }

    @FXML
    private void handleGenerateRaport(ActionEvent event){
        System.out.println("Works too");
    }

    private void prepareBarChart() {
        this.barChart.getData().clear();
        barChart.setCursor(Cursor.CROSSHAIR);
        barChart.setTitle("Usage state");
        barChart.setAnimated(false);
        barChart.getXAxis().setTickLabelRotation(90);
    }
}
