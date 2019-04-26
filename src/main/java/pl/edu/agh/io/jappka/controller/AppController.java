package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AppController {

    private Stage primaryStage;
    private Scene primaryScene;
    private Scene graphScene;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;

    private int lastAppTime = 0;
    private boolean wasLastAppTimeSet = false;

    private GanttChart<Number,String> mainChart;
    private String[] categories;

    @FXML
    private AnchorPane mainPane;

    public void setPrimaryStageElements(Stage primaryStage, Scene primaryScene) {
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
        initGanttChart();
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
        primaryStage.setTitle("Activity Tracker");
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

    private void initGanttChart() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        mainChart = new GanttChart<Number, String>(xAxis,yAxis);
        configureAxis(xAxis,yAxis);
        configureChart();
        //Add chart to the main pane
        ObservableList list = mainPane.getChildren();
        list.addAll(mainChart);

    }

    private void configureChart(){
        mainChart.setLegendVisible(true);
        mainChart.setBlockHeight(50);
        mainChart.getData().clear();
        mainChart.setCursor(Cursor.CROSSHAIR);
        mainChart.setTitle("Usage State");
        mainChart.setAnimated(false);
        mainChart.setLayoutY(22.0);
        mainChart.setPrefHeight(405.0);
        mainChart.setPrefWidth(700.0);
        mainChart.getStylesheets().add(getClass().getResource("/ganttchart.css").toExternalForm());
    }

    private void configureAxis(NumberAxis xAxis, CategoryAxis yAxis){
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);
        xAxis.setAutoRanging(true);
        yAxis.setLabel("Applications");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setAutoRanging(true);
        //get initial category list
        categories = obData.keySet().toArray(new String[0]);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }

    public void update(){
        ArrayList<XYChart.Series> s=new ArrayList<>();
        int c=0;
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            XYChart.Series series= new XYChart.Series();
            series.setName(categories[c]);
            c++;
            int diff = 0;
            for (AbstractActivityPeriod a : e.getValue()){
                String style="status-green";
                int start=diff;
                int time=(int) ((a.getEndTime()-a.getStartTime())/1000);
                if (a.getType()==AbstractActivityPeriod.Type.NONFOCUSED || a.getType() == AbstractActivityPeriod.Type.OFF)
                    style="status-red";

                series.getData().add(new XYChart.Data(start,series.getName(),new GanttChart.ExtraData(time,style)));
                diff += time;
            }
            /*Workaround to simulate drawing the last active state for PC (since it's not available until we close
            the app and reopen it again) - we add an artificial green strip which 'chases' the app strip*/
            /*
            if(!e.getKey().equals("PC")) {
                this.lastAppTime = diff;
                this.wasLastAppTimeSet = true;
            }
            if(e.getKey().equals("PC")) {
                if(this.wasLastAppTimeSet) {
                    String style="status-green";
                    int time = this.lastAppTime - diff;
                    series.getData().add(new XYChart.Data(diff,series.getName(),new GanttChart.ExtraData(time,style)));
                }
            }*/

            s.add(series);
        }

        for (int i=0; i<s.size(); i++){
            mainChart.getData().add(s.get(i));
        }
    }
}
