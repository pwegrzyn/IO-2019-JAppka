package pl.edu.agh.io.jappka.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import pl.edu.agh.io.jappka.charts.GraphAppColor;
import pl.edu.agh.io.jappka.charts.HoveredNode;
import pl.edu.agh.io.jappka.util.Utils;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AppController {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getName());
    private Stage primaryStage;
    private Scene primaryScene;
    private ObservableList<String> yAxisCategories;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private ActionsControllerHelper actionsControllerHelper;
    private ChartControllerHelper chartControllerHelper;
    private DataController dataController;
    private ObservableList<XYChart.Series<Number, String>> chartData;

    private String dataDirectoryPath = "data/";
    private String lastConfigLocation = dataDirectoryPath + "last_config_location.txt";;
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";

    private GanttChart<Number,String> mainChart;

    private NumberAxis xAxis;
    private String currentlyDisplayedDate;
    private String dateFormat;
    private String clockFormat;
    private static final int MILLISECONDS_IN_DAY = 86400000;

    private Map<String,ActivitySummary> activities;

    private List<String> appsOrderOnGraph;

    public Map<String, GraphAppColor> getColorMapping() {
        return colorMapping;
    }

    public void setColorMapping(Map<String, GraphAppColor> colorMapping) {
        this.colorMapping = colorMapping;
    }

    private Map<String, GraphAppColor> colorMapping;

    @FXML
    private AnchorPane mainPane;

    private String currentTheme = "defaulttheme.css";
    private List<String> themesList = new LinkedList<String>(){
        {
            add("defaulttheme.css");
            add("darktheme.css");
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
        this.appsOrderOnGraph = new LinkedList<>();
        this.colorMapping = new HashMap<>();
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
            this.currentTheme = "defaulttheme.css";
            this.primaryScene.getStylesheets().removeAll(themesList);
            this.primaryScene.getStylesheets().add(currentTheme);

        });
        this.darkTheme.setOnAction(e->{
            this.currentTheme = "darktheme.css";
            this.primaryScene.getStylesheets().removeAll(themesList);
            this.primaryScene.getStylesheets().add(currentTheme);

        });

        this.dataController = new DataController(this.obData);
        this.dataController.loadPreviousEvents();

        // Buttons Customization
        this.GoBackwardsDay.setCursor(Cursor.HAND);
        this.GoForwardsDay.setCursor(Cursor.HAND);
        this.GoForwardsDay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playSound();
            }
        });
        this.GoBackwardsDay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playSound();
            }
        });

        // Show Init Help Screen when the main GUI loads
        showInitHelpScreen();

        // Check if can load last config file
        tryToLoadConfigFile();
    }

    private void playSound() {
        AudioClip audioClip = null;
        try {
            audioClip = new AudioClip(getClass().getClassLoader().getResource("sound/button_click.wav").toURI().toString());
        } catch (URISyntaxException e) {
            LOGGER.warning("Error while loading audio file!");
            return;
        }
        audioClip.setVolume(0.05);
        audioClip.play();
    }

    private void tryToLoadConfigFile() {
        if (fileExists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.lastConfigLocation));
                String line;
                List<String> records = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    records.add(line);
                }
                reader.close();
                this.tryToLoadAutomatically(records.get(0));
            }
            catch (Exception e) {
                return;
            }
        }
    }

    private void showInitHelpScreen() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/initHelpScreen.fxml"));
            AnchorPane layout = loader.load();
            Stage stage = new Stage();
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            Scene scene = new Scene(layout,400,850);
            scene.getStylesheets().add(currentTheme);
            stage.setScene(scene);
            java.awt.image.BufferedImage imageIcon = ImageIO.read(getClass().getClassLoader().getResource("image/icon2.png"));
            stage.getIcons().add(SwingFXUtils.toFXImage(imageIcon, null));
            stage.setTitle("Welcome To JAppka Activity Tracker");
            InitHelpScreenController controller = loader.getController();
            controller.init();
            if (controller.shouldSkipWindow()) {
                return;
            }
            controller.setStage(stage);
            stage.show();
        }catch(Exception e){
            LOGGER.warning("Exception occurred when loading Init Help Screen");
            return;
        }
    }

    public void setObData(ObservableMap<String, List<AbstractActivityPeriod>> obData){
        this.obData=obData;
        this.dataController = new DataController(obData);
    }

    public void setAppsOrderOnGraph(List<String> ordered) {
        this.appsOrderOnGraph = ordered;
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

    private void tryToLoadAutomatically(String configFileLocation) {
        actionsControllerHelper.handleLoadAutomatically(this, configFileLocation);
    }

    @FXML
    private void handleAddOwnEventAction(ActionEvent event){
        actionsControllerHelper.handleAddOwnEventAction(this, currentTheme);
    }

    @FXML
    private void handleGraphCustomization(ActionEvent event){
        actionsControllerHelper.handleGraphCustomization(event, this.currentTheme, this);
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

        // Order the apps on the graph according to user specification
        Map<String, List<AbstractActivityPeriod>> obDataSorted = null;
        if (obDataContainsSameKeysAsOrderList()) {
            obDataSorted = sortGraphEntries();
        } else {
            obDataSorted = obData;
        }

        String[] categories=obDataSorted.keySet().stream().toArray(String[]::new);
        yAxisCategories.setAll(categories);

        chartData.clear();

        int c=0;
        long diff = 0;
        boolean skipBarChartDrawing = false;
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obDataSorted.entrySet()){
            XYChart.Series series= new XYChart.Series();
            series.setName(categories[c]);
            c++;
            if(e.getValue().size() < 1) skipBarChartDrawing = true;
            if (!skipBarChartDrawing) {
                diff = e.getValue().get(0).getStartTime()/1000;
                for (AbstractActivityPeriod a : e.getValue()){
                    String style = getChartStyle(a.getType(), e.getKey());
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
                                a.getStartTime(),
                                a.getEndTime(),
                                title,
                                this));
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

    public boolean obDataContainsSameKeysAsOrderList() {
        if (this.appsOrderOnGraph == null || this.obData == null) return false;
        if (this.appsOrderOnGraph.size() != this.obData.size()) return false;
        for (Map.Entry<String, List<AbstractActivityPeriod>> entry : this.obData.entrySet()) {
            boolean found = false;
            for (String appName : this.appsOrderOnGraph) {
                if (appName.equals(entry.getKey())) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public LinkedHashMap<String, List<AbstractActivityPeriod>> sortGraphEntries() {
        LinkedHashMap<String, List<AbstractActivityPeriod>> result = new LinkedHashMap<>();
        for (String appName : this.appsOrderOnGraph) {
            result.put(appName, this.obData.get(appName));
        }
        return result;
    }

    private String getChartStyle(AbstractActivityPeriod.Type type, String app){
        if(type == AbstractActivityPeriod.Type.NONFOCUSED || type == AbstractActivityPeriod.Type.OFF){
            return "status-transparent";
        }
        if (this.colorMapping.get(app) == null) {
            return "status-green";
        }
        GraphAppColor color = this.colorMapping.get(app);
        switch (color) {
            case Red: return "status-red";
            case Green: return "status-green";
            case Blue: return "status-blue";
            case Pink: return "status-pink";
            case Black: return "status-black";
            case Yellow: return "status-yellow";
            default: return "status-green";
        }
    }

    public void removePeriod(long start, long end, String title){
        List<AbstractActivityPeriod> customEvents = obData.get("Custom");

        Optional<AbstractActivityPeriod> period = customEvents.stream()
                .filter(e -> e.getStartTime() == start && e.getEndTime() == end).findFirst();

        if(period.isPresent()){
            customEvents.remove(period.get());
            this.dataController.getCustomEventManager().persist(customEvents);
        }

    }

    public DataController getDataController(){
        return this.dataController;
    }

    private boolean fileExists() {
        File file = new File(this.lastConfigLocation);
        return file.exists() && !file.isDirectory();
    }

}
