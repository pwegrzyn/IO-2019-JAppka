package pl.edu.agh.io.jappka.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.ActivitySummary;
import pl.edu.agh.io.jappka.activity.AppActivityPeriod;
import pl.edu.agh.io.jappka.activity.CustomActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AppController {

    private Stage primaryStage;
    private Scene primaryScene;
    private ObservableList<String> yAxisCategories;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private ActionsControllerHelper actionsControllerHelper;
    private ChartControllerHelper chartControllerHelper;
    private DataController dataController;

    private GanttChart<Number,String> mainChart;

    private NumberAxis xAxis;
    private String currentlyDisplayedDate;
    private String dateFormat;
    private String clockFormat;
    private static final int MILLISECONDS_IN_DAY = 86400000;

    private Map<String,ActivitySummary> activities;

    @FXML
    private AnchorPane mainPane;

    private String currentTheme = "/defaulttheme.css";
    private List<String> themesList = new LinkedList<String>(){
        {
            add("/defaulttheme.css");
            add("/darktheme.css");
        }
    };
    @FXML
    private MenuItem defaultTheme;

    @FXML
    private MenuItem darkTheme;

    public void setPrimaryStageElements(Stage primaryStage, Scene primaryScene) {
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
        this.actionsControllerHelper = new ActionsControllerHelper(primaryStage, primaryScene);
        this.chartControllerHelper = new ChartControllerHelper();
        this.dateFormat = "MMM dd,yyyy";
        this.clockFormat = "HH:mm:ss";
        initGanttChart();
        initCurrentDateBar();
        initGraphDataBoundaries();
        this.primaryScene.getStylesheets().add(currentTheme);
        this.defaultTheme.setOnAction(e->{
            this.currentTheme = "/defaulttheme.css";
            this.primaryScene.getStylesheets().removeAll(themesList);
            this.primaryScene.getStylesheets().add(currentTheme);

        });
        this.darkTheme.setOnAction(e->{
            this.currentTheme = "/darktheme.css";
            this.primaryScene.getStylesheets().removeAll(themesList);
            this.primaryScene.getStylesheets().add(currentTheme);

        });
        this.dataController = new DataController(this.obData);
    }

    public void setObData(ObservableMap<String, List<AbstractActivityPeriod>> obData){
        this.obData=obData;
    }

    public void setActivities(Map<String,ActivitySummary> activities){
        this.activities=activities;
    }

    public Map<String,ActivitySummary> getActivities(){
        return this.activities;
    }

    @FXML
    private void handleAddApplicationAction(ActionEvent event)  {
        actionsControllerHelper.handleAddApplicationAction(event, this, currentTheme);
    }

    @FXML
    private void handleGenerateReport(ActionEvent event){
        actionsControllerHelper.handleGenerateReport(currentTheme,obData);
    }

    @FXML
    private void handleSave(ActionEvent event){
        actionsControllerHelper.handleSave(event,currentTheme,this,true);
    }

    @FXML
    private void handleLoad(ActionEvent event){
        actionsControllerHelper.handleSave(event,currentTheme,this,false);
    }

    @FXML
    private void handleAddOwnEventAction(ActionEvent event){
        actionsControllerHelper.handleAddOwnEventAction(this, currentTheme);
    }

    @FXML
    private void handleGraphColorPicker(ActionEvent event){
        actionsControllerHelper.handleGraphColorPicker(event);
    }

    public void backToMainView() {
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("JAppka Activity Tracker");
        primaryStage.show();
    }

    @FXML
    private void handleGoBackwardsDayButton(ActionEvent event){
        ((Button) this.primaryScene.lookup("#GoForwardsDay")).setDisable(false);

        String currentDateMidnight = this.currentlyDisplayedDate + " 00:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat + " " + this.clockFormat);
        long startMilliseconds = 0;
        long endMilliseconds = 0;
        try {
            Date date = formatter.parse(currentDateMidnight);
            endMilliseconds = date.getTime() - 1;
            startMilliseconds = date.getTime() - MILLISECONDS_IN_DAY;
            this.currentlyDisplayedDate = Utils.millisecondsToCustomStrDate(startMilliseconds, this.dateFormat);
            changeCurrentlyDisplayedDate();
        } catch (ParseException e) {
            System.err.println("Error while parsing date!");
            e.printStackTrace();
        }
        this.xAxis.setLowerBound(startMilliseconds / 1000);
        this.xAxis.setUpperBound(endMilliseconds / 1000);
    }

    @FXML
    private void handleGoForwardsDayButton(ActionEvent event){
        String currentDateMidnight = this.currentlyDisplayedDate + " 00:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat + " " + this.clockFormat);
        long startMilliseconds = 0;
        long endMilliseconds = 0;
        try {
            Date date = formatter.parse(currentDateMidnight);
            startMilliseconds = date.getTime() + MILLISECONDS_IN_DAY;
            endMilliseconds = startMilliseconds + MILLISECONDS_IN_DAY - 1;
            this.currentlyDisplayedDate = Utils.millisecondsToCustomStrDate(startMilliseconds, this.dateFormat);
            changeCurrentlyDisplayedDate();
        } catch (ParseException e) {
            System.err.println("Error while parsing date!");
            e.printStackTrace();
        }
        this.xAxis.setLowerBound(startMilliseconds / 1000);
        this.xAxis.setUpperBound(endMilliseconds / 1000);

        long currentTimestamp = System.currentTimeMillis();
        String currentDate = Utils.millisecondsToCustomStrDate(currentTimestamp, this.dateFormat);
        if(currentDate.equals(this.currentlyDisplayedDate)) {
            ((Button)event.getSource()).setDisable(true);
        }
    }

    private void changeCurrentlyDisplayedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        SimpleDateFormat sdfEnglish = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(this.currentlyDisplayedDate);

        } catch (ParseException e) {
            System.err.println("Error while parsing date!");
            e.printStackTrace();
        }
        ((Label) this.primaryScene.lookup("#CurrentlyDisplayedDate")).textProperty().bind(Bindings.format("%s",
                sdfEnglish.format(date)));
    }

    private void initCurrentDateBar() {
        long currentTimestamp = System.currentTimeMillis();
        String currentDate = Utils.millisecondsToCustomStrDate(currentTimestamp, this.dateFormat);
        this.currentlyDisplayedDate = currentDate;
        changeCurrentlyDisplayedDate();
        ((Button) this.primaryScene.lookup("#GoForwardsDay")).setDisable(true);
    }

    private void initGraphDataBoundaries() {
        String currentDateMidnight = this.currentlyDisplayedDate + " 00:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat + " " + this.clockFormat);
        long startMilliseconds = 0;
        long endMilliseconds = 0;
        try {
            Date date = formatter.parse(currentDateMidnight);
            startMilliseconds = date.getTime();
            endMilliseconds = startMilliseconds + MILLISECONDS_IN_DAY - 1;
        } catch (ParseException e) {
            System.err.println("Error while parsing date!");
            e.printStackTrace();
        }
        this.xAxis.setLowerBound(startMilliseconds / 1000);
        this.xAxis.setUpperBound(endMilliseconds / 1000);
    }

    @FXML
    private void handleShowCharts(ActionEvent event){
        actionsControllerHelper.handleShowCharts(event, this, currentTheme, obData);
    }

    private void initGanttChart() {
        this.xAxis = new NumberAxis();
        yAxisCategories = FXCollections.observableList(obData.keySet().stream().collect(Collectors.toList()));
        mainChart = chartControllerHelper.initGanttChart(xAxis, mainPane, obData, yAxisCategories);
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }

    public void gatherData(){
        for (Map.Entry<String,ActivitySummary> e : activities.entrySet()){
            ActivitySummary a=e.getValue();
            a.generate();
            obData.put(e.getKey(),a.getAllPeriods());
        }
    }

    public void update(){
        String[] categories=obData.keySet().stream().toArray(String[]::new);
        yAxisCategories.setAll(categories);

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
                    String style = getChartStyle(a.getType());
                    long start = diff;
                    long time=(a.getEndTime()-a.getStartTime())/1000;
                    series.getData().add(new XYChart.Data<Number, String>(start,series.getName(),new GanttChart.ExtraData(time,style)));
                    diff += time;
                }
            }

            skipBarChartDrawing = false;
            s.add(series);
        }

        mainChart.setData(FXCollections.observableArrayList(s));
    }

    private String getChartStyle(AbstractActivityPeriod.Type type){
        if(type == AbstractActivityPeriod.Type.NONFOCUSED || type == AbstractActivityPeriod.Type.OFF){
            return "status-transparent";
        }
        else{
            return "status-green";
        }
    }

    public DataController getDataController(){
        return this.dataController;
    }
}
