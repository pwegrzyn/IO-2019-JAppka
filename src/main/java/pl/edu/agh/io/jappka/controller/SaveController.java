package pl.edu.agh.io.jappka.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            List<String> apps=new ArrayList<String>();
            for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()) apps.add(e.getKey());
            String json=new Gson().toJson(apps);
            try (PrintWriter out=new PrintWriter(this.file)){
                out.println(json);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else load();
        this.stage.close();
    }

    private boolean containsApp(String name,ObservableMap<String,List<AbstractActivityPeriod>> obData){
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if (e.getKey().equals(name)) return true;
        }
        return false;
    }

    public void load() throws Exception{
        Gson gson=new Gson();
        JsonReader reader=null;
        try {
            reader=new JsonReader(new FileReader(this.file.getAbsoluteFile()));
        } catch(FileNotFoundException e) {
            LOGGER.severe("This file does not exists!");
            return;
        }
        ArrayList<String> apps=gson.fromJson(reader,new TypeToken<List<String>>(){}.getType());

        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if ((!e.getKey().equals("PC"))) obData.remove(e.getKey());
        }

        Map<String,ActivitySummary> activities=new HashMap<>();
        activities.put("PC",appController.getActivities().get("PC"));

        for (String e : apps){
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
        return;
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
        if(save) {
            handleChooseFileToSave(fileChooser);
        } else {
            handleChooseFileToLoad(fileChooser);
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
}