package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

import java.util.Arrays;
import java.util.List;

public class ChartControllerHelper {

    private GanttChart<Number,String> mainChart;

    public GanttChart<Number,String> initGanttChart(NumberAxis xAxis, AnchorPane mainPane, ObservableMap<String, List<AbstractActivityPeriod>> obData) {
        CategoryAxis yAxis = new CategoryAxis();
        mainChart = new GanttChart<>(xAxis,yAxis);
        configureAxis(xAxis,yAxis, obData);
        configureChart();
        //Add chart to the main pane
        ObservableList list = mainPane.getChildren();
        list.addAll(mainChart);
        return mainChart;
    }

    private void configureChart(){
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
