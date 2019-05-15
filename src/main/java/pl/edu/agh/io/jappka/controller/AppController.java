package pl.edu.agh.io.jappka.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pl.edu.agh.io.jappka.activity.*;
import pl.edu.agh.io.jappka.charts.GanttChart;
import pl.edu.agh.io.jappka.util.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppController {

    private Stage primaryStage;
    private Scene primaryScene;
    private Scene graphScene;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;

    private long lastAppTime = 0;
    private boolean wasLastAppTimeSet = false;

    private GanttChart<Number,String> mainChart;
    private String[] categories;

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

    public void handleSave(){
        List<String> apps=new ArrayList<String>();
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()) apps.add(e.getKey());
        String json=new Gson().toJson(apps);
        try (PrintWriter out=new PrintWriter("config.json")){
            out.println(json);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean containsApp(String name){
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if (e.getKey().equals(name)) return true;
        }
        return false;
    }

    public void handleLoad() throws Exception{
        Gson gson=new Gson();
        JsonReader reader=new JsonReader(new FileReader("config.json"));
        ArrayList<String> apps=gson.fromJson(reader,new TypeToken<List<String>>(){}.getType());

        for (String e : apps){
            if (!containsApp(e)){
                /*ActivityTracker newTracker=new AppActivityTracker(e);
                newTracker.track();

                ActivitySummary newSummary=new AppActivitySummary(newTracker.getActivityStream(),e);
                newSummary.generate();
                obData.put(e,newSummary.getAllPeriods());*/
                System.out.println(e);
            }
        }
        //System.out.println(obData);
    }

    public void setPrimaryStageElements(Stage primaryStage, Scene primaryScene) {
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
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
        
        try{
            primaryStage.setTitle("Add app");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/addAppView.fxml"));
            AnchorPane layout = loader.load();

            AddAppController controller = loader.getController();
            controller.initialize(this);
            graphScene = new Scene(layout);
            graphScene.getStylesheets().add(currentTheme);
            primaryStage.setScene(graphScene);
            primaryStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
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
            graphScene.getStylesheets().add(currentTheme);
            primaryStage.setScene(graphScene);
            primaryStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGraphColorPicker(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/popups/graphColorPicker.fxml"));
            GridPane colorPickerLayout = loader.load();
            Stage colorPickerStage= new Stage();
            Scene colorPickerScene = new Scene(colorPickerLayout,500,300);
            colorPickerStage.setScene(colorPickerScene);
            colorPickerStage.setTitle("Grid's color scheme");
            GraphColorPickerController colorPickerController = loader.getController();
            colorPickerController.setStage(colorPickerStage);
            colorPickerStage.show();
        }catch(IOException e){
            System.out.println("Exception occurred when loading graph color picker's FXML file, Reason: ");
            e.printStackTrace();
        }

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
            graphScene.getStylesheets().add(currentTheme);
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
        this.xAxis = xAxis;
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
        mainChart.setPrefHeight(770.0);
        mainChart.setPrefWidth(1500.0);
        mainChart.getStylesheets().add(getClass().getResource("/ganttchart.css").toExternalForm());
    }

    private void configureAxis(NumberAxis xAxis, CategoryAxis yAxis){
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(900);
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
        categories = obData.keySet().toArray(new String[0]);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
    }

    public ObservableMap<String, List<AbstractActivityPeriod>> getObData() {
        return obData;
    }

    public void update(){
        categories=obData.keySet().toArray(new String[0]);
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
            boolean atLeastOnePeriodHappend = false;
            if (!skipBarChartDrawing) {
                diff = e.getValue().get(0).getStartTime()/1000;
                for (AbstractActivityPeriod a : e.getValue()){
                    atLeastOnePeriodHappend = true;
                    String style="status-green";
                    long start = diff;
                    long time=(a.getEndTime()-a.getStartTime())/1000;
                    if (a.getType()==AbstractActivityPeriod.Type.NONFOCUSED || a.getType() == AbstractActivityPeriod.Type.OFF)
                        style="status-transparent";
                    series.getData().add(new XYChart.Data<Number, String>(start,series.getName(),new GanttChart.ExtraData(time,style)));
                    diff += time;
                }
            }
            /*Workaround to simulate drawing the last active state for PC (since it's not available until we close
            the app and reopen it again) - we add an artificial green strip which 'chases' the app strip*/
            if(!e.getKey().equals("PC") && atLeastOnePeriodHappend && e.getValue().size() > 1) {
                this.lastAppTime = diff;
                this.wasLastAppTimeSet = true;
            } else if (!e.getKey().equals("PC") && atLeastOnePeriodHappend && e.getValue().size() == 1) {
                this.firstLastAppTime = diff;
                this.wasLastAppTimeSet = true;
            }
            if(e.getKey().equals("PC") && e.getValue().size() > 0 && this.wasLastAppTimeSet) {
                String style="status-green";
                long time = this.lastAppTime - diff;
                //series.getData().add(new XYChart.Data<Number,String>(diff,series.getName(),new GanttChart.ExtraData(time,style)));
                series.getData().add(new XYChart.Data(diff,series.getName(),new GanttChart.ExtraData(time+1,style)));
            } else if (e.getKey().equals("PC") && e.getValue().size() < 1 && this.wasLastAppTimeSet) {
                String style="status-green";
                long time = this.lastAppTime - this.firstLastAppTime;
                //series.getData().add(new XYChart.Data<Number,String>(diff,series.getName(),new GanttChart.ExtraData(time,style)));
                series.getData().add(new XYChart.Data(this.firstLastAppTime,series.getName(),new GanttChart.ExtraData(time+1,style)));
            }
            skipBarChartDrawing = false;
            s.add(series);
            s.get(c-1).getName();
        }

        mainChart.setData(FXCollections.observableArrayList(s));
    }
}
