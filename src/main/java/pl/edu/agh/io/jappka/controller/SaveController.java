package pl.edu.agh.io.jappka.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import pl.edu.agh.io.jappka.charts.GraphAppColor;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class SaveController {

    @FXML
    public Button saveButton;

    @FXML
    Label chosenFile;

    private static final Logger LOGGER = Logger.getLogger(SaveController.class.getName());
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private AppController appController;
    private boolean save;
    private Stage stage;
    private File file;
    private String dataDirectoryPath = "data/";
    private String appStateFilePath = dataDirectoryPath + "last_config_location.txt";;
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";

    public void initialize(AppController appController, boolean save){
        this.appController=appController;
        this.obData=appController.getObData();
        this.save=save;
        if (save) saveButton.setText("Save");
        else saveButton.setText("Load");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void save() throws Exception{
        if (this.file == null) {
            LOGGER.warning("You need to first choose a file!");
            return;
        }
        if (save){

            List<Object> fields = new ArrayList<>();

            List<String> apps=new ArrayList<String>();

            // Taking care of saving the order of apps on the graph
            Map<String, List<AbstractActivityPeriod>> obDataSorted = null;
            if (this.appController.obDataContainsSameKeysAsOrderList()) {
                obDataSorted = this.appController.sortGraphEntries();
            } else {
                obDataSorted = obData;
            }

            for (Map.Entry<String,List<AbstractActivityPeriod>> e : obDataSorted.entrySet()) apps.add(e.getKey());
            fields.add(apps);

            fields.add(this.appController.getColorMapping());

            String json=new Gson().toJson(fields);
            try (PrintWriter out=new PrintWriter(this.file)){
                out.println(json);
            } catch (Exception e){
                e.printStackTrace();
            }

            persistLastSaveLocation();
        }
        else load();
        this.stage.close();
    }

    private void persistLastSaveLocation() {
        if (fileExists()) {
            try{
                FileWriter fw = new FileWriter(this.appStateFilePath);
                fw.write(this.file.getAbsolutePath());
                fw.close();
            } catch(Exception e) {
                LOGGER.warning("Error while persisting last config file save");
                return;
            }
        } else {
            try {
                createAppStateFile();
                FileWriter fw = new FileWriter(this.appStateFilePath);
                fw.write(this.file.getAbsolutePath());
                fw.close();
            } catch (IOException e) {
                LOGGER.warning("Error while creating last config file save");
                return;
            }
        }
    }

    private void createAppStateFile() throws IOException {
        new File(this.activityDataDirectoryPath).mkdirs();
        File file = new File(this.appStateFilePath);
        file.createNewFile();
    }

    private boolean fileExists() {
        File file = new File(this.appStateFilePath);
        return file.exists() && !file.isDirectory();
    }

    private boolean containsApp(String name,ObservableMap<String,List<AbstractActivityPeriod>> obData){
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if (e.getKey().equals(name)) return true;
        }
        return false;
    }

    /*public void load() throws Exception{
        Gson gson=new Gson();
        JsonReader reader=null;
        try {
            reader=new JsonReader(new FileReader(this.file.getAbsoluteFile()));
        } catch(FileNotFoundException e) {
            return;
        }

        ArrayList<String> apps=gson.fromJson(reader,new TypeToken<List<String>>(){}.getType());

        // Taking care of restoring proper ordering
        this.appController.setAppsOrderOnGraph(apps);

        try {
            for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
                if ((!e.getKey().equals("PC"))) obData.remove(e.getKey());
            }
        } catch (ConcurrentModificationException e) {
            return;
        }

        Map<String,ActivitySummary> activities=new HashMap<>();
        activities.put("PC",appController.getActivities().get("PC"));

        for (String e : apps){
            if(e.equals("Custom")) continue;
            if (!containsApp(e,obData)){
                ActivityTracker newTracker=new AppActivityTracker(e);
                newTracker.track();

                ActivitySummary newSummary=new AppActivitySummary(newTracker.getActivityStream(),e);
                newSummary.generate();
                obData.put(e,newSummary.getAllPeriods());
                activities.put(e,newSummary);
            }
        }
        appController.setObData(obData);
        appController.setActivities(activities);
        appController.getDataController().loadPreviousEvents();
        return;
    }*/

    public void load() throws Exception{
        Gson gson=new Gson();
        JsonReader reader=null;
        try {
            reader=new JsonReader(new FileReader(this.file.getAbsoluteFile()));
        } catch(FileNotFoundException e) {
            return;
        }

        ArrayList<Object> fields = gson.fromJson(reader, new TypeToken<List<Object>>(){}.getType());

        // Taking care of restoring proper ordering
        this.appController.setAppsOrderOnGraph((List<String>) fields.get(0));

        try {
            for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
                if ((!e.getKey().equals("PC"))) obData.remove(e.getKey());
            }
        } catch (ConcurrentModificationException e) {
            return;
        }

        Map<String,ActivitySummary> activities=new HashMap<>();
        activities.put("PC",appController.getActivities().get("PC"));

        for (String e : (List<String>) fields.get(0)){
            if(e.equals("Custom")) continue;
            if (!containsApp(e,obData)){
                ActivityTracker newTracker=new AppActivityTracker(e);
                newTracker.track();

                ActivitySummary newSummary=new AppActivitySummary(newTracker.getActivityStream(),e);
                newSummary.generate();
                obData.put(e,newSummary.getAllPeriods());
                activities.put(e,newSummary);
            }
        }
        appController.setObData(obData);
        appController.setActivities(activities);
        appController.getDataController().loadPreviousEvents();

        // Recover colors
        Map<String, String> colorMappingStr = (Map<String, String>) fields.get(1);
        Map<String, GraphAppColor> colorMapping = new HashMap<>();
        for (Map.Entry<String, String> entry : colorMappingStr.entrySet()) {
            switch (entry.getValue()) {
                case "Red": colorMapping.put(entry.getKey(), GraphAppColor.Red); break;
                case "Green": colorMapping.put(entry.getKey(), GraphAppColor.Green); break;
                case "Blue": colorMapping.put(entry.getKey(), GraphAppColor.Blue); break;
                case "Black": colorMapping.put(entry.getKey(), GraphAppColor.Black); break;
                case "Yellow": colorMapping.put(entry.getKey(), GraphAppColor.Yellow); break;
                case "Pink": colorMapping.put(entry.getKey(), GraphAppColor.Pink); break;
                default: break;
            }
        }
        appController.setColorMapping(colorMapping);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose configuration file location");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter( "JSON", "*.json"));
        fileChooser.setInitialFileName("japkka_config");
        if(save) {
            handleChooseFileToSave(fileChooser);
        } else {
            handleChooseFileToLoad(fileChooser);
        }
        if (this.file != null) {
            ((Button) this.stage.getScene().lookup("#saveButton")).setDisable(false);
        }
    }

    private void handleChooseFileToLoad(FileChooser fileChooser) {
        this.file = fileChooser.showOpenDialog(stage);
        if (this.file != null) {
            this.chosenFile.textProperty().set(file.getAbsolutePath());
        }
    }

    private void handleChooseFileToSave(FileChooser fileChooser) {
        this.file = fileChooser.showSaveDialog(stage);
        if (this.file != null) {
            this.chosenFile.textProperty().set(file.getAbsolutePath());
        }
    }

    public void loadAutomatically(String configFileLocation) {
        this.file = new File(configFileLocation);
        try {
            load();
        } catch (Exception e) {
            LOGGER.warning("Error while automatically loading config file");
        }
        this.file = null;
    }
}