package pl.edu.agh.io.jappka.controller;

import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

public class AppController {

    private Stage primaryStage;
    private Scene primaryScene;
    private Scene graphScene;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;

    @FXML
    private StackedBarChart mainChart;

    public void setPrimaryStageElements(Stage primaryStage, Scene primaryScene) {
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
        prepareBarChart();
    }

    public void setObData(ObservableMap<String, List<AbstractActivityPeriod>> obData){
        this.obData=obData;
    }

    @FXML
    private void handleAddApplicationAction(ActionEvent event)  {
        System.out.println("Add App");
    }

    @FXML
    private void handleGenerateReport(ActionEvent event){

        try{
            primaryStage.setTitle("Report generation");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/generateGraphView.fxml"));
            AnchorPane layout = loader.load();

            GenerateGraphController controller = loader.getController();
            controller.initialize(this);
            graphScene = new Scene(layout);
            primaryStage.setScene(graphScene);
            primaryStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void backToMainView() {
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    @FXML
    private void handleShowCharts(ActionEvent event){
        try{
            primaryStage.setTitle("Chart generation");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/chartView.fxml"));
            AnchorPane layout = loader.load();

            ChartController controller = loader.getController();
            controller.initialize(this);
            controller.setData(obData);
            controller.drawGraph();
            graphScene = new Scene(layout);
            primaryStage.setScene(graphScene);
            primaryStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void prepareBarChart() {
        mainChart.getData().clear();
        mainChart.setCursor(Cursor.CROSSHAIR);
        mainChart.setTitle("Usage state");
        mainChart.setAnimated(false);
        mainChart.getXAxis().setTickLabelRotation(90);
        mainChart.getXAxis().setLabel("Time");
        mainChart.getYAxis().setLabel("Applications");
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }
}
