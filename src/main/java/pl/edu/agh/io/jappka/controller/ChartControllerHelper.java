package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChartControllerHelper {


    public GanttChart<Number,String> initGanttChart(NumberAxis xAxis, AnchorPane mainPane, ObservableMap<String, List<AbstractActivityPeriod>> obData) {
        CategoryAxis yAxis = new CategoryAxis();
        GanttChart<Number,String> mainChart = new GanttChart<>(xAxis,yAxis);
        configureAxis(xAxis,yAxis, obData);
        configureChart(mainChart);
        //Add chart to the main pane
        ObservableList list = mainPane.getChildren();
        list.addAll(mainChart);
        return mainChart;
    }

    public void update(ObservableMap<String, List<AbstractActivityPeriod>> obData, GanttChart<Number,String> mainChart){
        String[] categories=obData.keySet().stream().toArray(String[]::new);

        CategoryAxis yAxis=(CategoryAxis) mainChart.getYAxis();
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));

        ArrayList<XYChart.Series<Number, String>> s=new ArrayList<>();
        int c=0;
        long diff = 0;
        boolean skipBarChartDrawing = false;
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            XYChart.Series series= new XYChart.Series();
            series.setName(categories[c]);
            c++;
            if(e.getValue().size() < 1) skipBarChartDrawing = true;
            if (!skipBarChartDrawing) {
                diff = e.getValue().get(0).getStartTime()/1000;
                for (AbstractActivityPeriod a : e.getValue()){
                    String style="status-green";
                    long start = diff;
                    long time=(a.getEndTime()-a.getStartTime())/1000;
                    if (a.getType()==AbstractActivityPeriod.Type.NONFOCUSED || a.getType() == AbstractActivityPeriod.Type.OFF)
                        style="status-transparent";
                    series.getData().add(new XYChart.Data<Number, String>(start,series.getName(),new GanttChart.ExtraData(time,style)));
                    diff += time;
                }
            }

            skipBarChartDrawing = false;
            s.add(series);
        }

        mainChart.setData(FXCollections.observableArrayList(s));
    }

    private void configureChart(GanttChart<Number,String> mainChart){
        mainChart.setLegendVisible(true);
        mainChart.setBlockHeight(50);
        mainChart.getData().clear();
        mainChart.setCursor(Cursor.CROSSHAIR);
        mainChart.setTitle("Usage State");
        mainChart.setAnimated(false);
        mainChart.setLayoutY(22.0);
        mainChart.setPrefHeight(610.0);
        mainChart.setPrefWidth(1200.0);
        mainChart.getStylesheets().add(getClass().getResource("/ganttchart.css").toExternalForm());
    }

    private void configureAxis(NumberAxis xAxis, CategoryAxis yAxis, ObservableMap<String, List<AbstractActivityPeriod>> obData){
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(3600);
        xAxis.setMinorTickVisible(false);
        yAxis.setLabel("Applications");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setAutoRanging(true);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Utils.millisecondsToCustomStrDate(object.longValue()*1000, "HH:mm");
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        //get initial category list
        String[] categories = obData.keySet().toArray(new String[0]);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
    }

}
