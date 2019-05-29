package pl.edu.agh.io.jappka.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.ActivitySummary;
import pl.edu.agh.io.jappka.activity.CustomActivityPeriod;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.charts.HoveredNode;
import pl.edu.agh.io.jappka.util.Utils;

import java.nio.file.Paths;
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
    private ObservableList<XYChart.Series<Number, String>> chartData;

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
    private Button GoForwardsDay;

    @FXML
    private Button GoBackwardsDay;

    public String getCurrentTheme() {
        return currentTheme;
    }

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

        // Buttons Customization
        this.GoBackwardsDay.setCursor(Cursor.HAND);
        this.GoForwardsDay.setCursor(Cursor.HAND);
        this.GoForwardsDay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AudioClip audioClip = new AudioClip(Paths.get("src/main/resources/sound/button_click.wav")
                        .toUri().toString());
                audioClip.play();
            }
        });
        this.GoBackwardsDay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AudioClip audioClip = new AudioClip(Paths.get("src/main/resources/sound/button_click.wav")
                        .toUri().toString());
                audioClip.play();
            }
        });
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
        actionsControllerHelper.handleGenerateReport(currentTheme,obData, this);
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
        chartData = FXCollections.observableArrayList();
        mainChart = chartControllerHelper.initGanttChart(xAxis, mainPane, obData, yAxisCategories);
        mainChart.setData(chartData);
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

        chartData.clear();
        /*CategoryAxis yAxis=(CategoryAxis) mainChart.getYAxis();
        ArrayList<XYChart.Series<Number, String>> s=new ArrayList<>();*/

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
                    XYChart.Data<Number, String> entry = new XYChart.Data<Number, String>(start,series.getName(),new GanttChart.ExtraData(time,style));

                    if(a.getType() != AbstractActivityPeriod.Type.NONFOCUSED && a.getType() != AbstractActivityPeriod.Type.OFF) {
                        String title = "";
                        if(a instanceof CustomActivityPeriod){
                            CustomActivityPeriod event = (CustomActivityPeriod)a;
                            title = event.getActivityName();
                        }
                        entry.setNode(new HoveredNode(
                                Utils.millisecondsToCustomStrDate(a.getStartTime(), "HH:mm:ss"),
                                Utils.millisecondsToCustomStrDate(a.getEndTime(), "HH:mm:ss"), title));
                    }

                    series.getData().add(entry);
                    diff += time;
                }
            }

            skipBarChartDrawing = false;
            chartData.add(series);
        }

        for(XYChart.Series<Number, String> series : mainChart.getData()){
            for(XYChart.Data<Number, String> entry : series.getData()){
                Tooltip t = new Tooltip(entry.getYValue().toString());
                Tooltip.install(entry.getNode(), t);
            }
        }
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
