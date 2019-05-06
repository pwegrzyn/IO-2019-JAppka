package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

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
    @FXML
    private void handleChangeGraphColors(ActionEvent event){
        try{
            AnchorPane colorPickerLayout = FXMLLoader.load(getClass().getClassLoader().getResource("JAppka/popups/gridColorPicker.fxml"));
            Stage graphColorDialog = new Stage();
            Scene colorPickerScene = new Scene(colorPickerLayout,500,300);
            graphColorDialog.setScene(colorPickerScene);
            graphColorDialog.setTitle("Grid's color scheme");
            graphColorDialog .show();
        }catch(IOException e){
            System.out.println("Exception occurred when loading gridColorPicker's FXML file, Reason: ");
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
        //xAxis.setMinorTickCount(10);
        xAxis.setAutoRanging(false);
        yAxis.setLabel("Applications");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        //yAxis.setTickLabelGap(10);
        yAxis.setAutoRanging(true);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Utils.millisecondsToStringDate(object.longValue()*1000);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        //timestamp of lower date bound
        xAxis.setLowerBound(1556626800);
        //timestamp of upper date bound
        xAxis.setUpperBound(1556628000);
        //get initial category list
        categories = obData.keySet().toArray(new String[0]);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }

    public void update(){
        ArrayList<XYChart.Series<Number, String>> s=new ArrayList<>();
        int c=0;


        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            XYChart.Series series= new XYChart.Series();
            series.setName(categories[c]);
            c++;
            long diff = e.getValue().get(0).getStartTime()/1000;
            for (AbstractActivityPeriod a : e.getValue()){
                String style="status-green";
                long start = diff;
                long time=(a.getEndTime()-a.getStartTime())/1000;
                if (a.getType()==AbstractActivityPeriod.Type.NONFOCUSED || a.getType() == AbstractActivityPeriod.Type.OFF)
                    style="status-transparent";

                series.getData().add(new XYChart.Data<Number, String>(start,series.getName(),new GanttChart.ExtraData(time,style)));
                diff += time;
            }

            s.add(series);
        }

        mainChart.setData(FXCollections.observableArrayList(s));
    }
}
