package pl.edu.agh.io.jappka.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppController {

    private Stage primaryStage;
    private Scene primaryScene;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private ActionsControllerHelper actionsControllerHelper;
    private ChartControllerHelper chartControllerHelper;

    private GanttChart<Number,String> mainChart;

    private NumberAxis xAxis;
    private String currentlyDisplayedDate;
    private String dateFormat;
    private String clockFormat;
    private static final int MILLISECONDS_IN_DAY = 86400000;

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
    private long firstLastAppTime;



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
    }

    public void setObData(ObservableMap<String, List<AbstractActivityPeriod>> obData){
        this.obData=obData;
    }

    @FXML
    private void handleAddApplicationAction(ActionEvent event)  {
        actionsControllerHelper.handleAddApplicationAction(event, this, currentTheme);
    }

    @FXML
    private void handleGenerateReport(ActionEvent event){
        actionsControllerHelper.handleGenerateReport(currentTheme);
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
        mainChart = chartControllerHelper.initGanttChart(xAxis, mainPane, obData);
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }

    public void update(){
        chartControllerHelper.update(obData, mainChart);
    }
}
