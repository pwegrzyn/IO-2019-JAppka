package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartController {
    @FXML
    private NumberAxis yAxis=new NumberAxis();

    @FXML
    private CategoryAxis xAxis=new CategoryAxis();

    @FXML
    private BarChart barChart=new BarChart(xAxis,yAxis);

    @FXML
    Map<String, List<AbstractActivityPeriod>> data = new HashMap<>();

    private AppController appController;

    public void initialize(AppController appController) {
        this.appController=appController;
    }

    public void setData(Map<String, List<AbstractActivityPeriod>> obData){
        this.data=obData;
    }

    public void drawGraph(){
        xAxis.setLabel("Apps");
        yAxis.setLabel("Time active");

        barChart.setTitle("App activity");
        XYChart.Series<String,Number> dataSeries=new XYChart.Series<>();
        dataSeries.setName("App activity time (s)");
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : data.entrySet()){
            for (AbstractActivityPeriod a : e.getValue()){
                long time=(a.getEndTime()-a.getStartTime())/1000;
                dataSeries.getData().add(new XYChart.Data<>(e.getKey(),time));
            }
        }
        barChart.getData().clear();
        barChart.getData().addAll(dataSeries);
    }

    @FXML
    private void testStuff(ActionEvent event){
        System.out.println("napalmstrike");
        drawGraph();
    }
}
