package pl.edu.agh.io.jappka.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveController {

    @FXML
    public Button button;

    @FXML
    TextArea sign;

    @FXML
    TextField textField;

    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private AppController appController;
    private boolean save;

    public void initialize(AppController appController, boolean save){
        this.appController=appController;
        this.obData=appController.getObData();
        this.save=save;
        if (save) button.setText("Save");
        else button.setText("Load");
    }

    public void save() throws Exception{
        if (save){
            List<String> apps=new ArrayList<String>();
            for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()) apps.add(e.getKey());
            String json=new Gson().toJson(apps);
            try (PrintWriter out=new PrintWriter(textField.getText()+".json")){
                out.println(json);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else load();
        Stage stage=(Stage) button.getScene().getWindow();
        stage.close();
    }

    private boolean containsApp(String name,ObservableMap<String,List<AbstractActivityPeriod>> obData){
        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if (e.getKey().equals(name)) return true;
        }
        return false;
    }

    public void load() throws Exception{
        Gson gson=new Gson();
        JsonReader reader=new JsonReader(new FileReader(textField.getText()+".json"));
        ArrayList<String> apps=gson.fromJson(reader,new TypeToken<List<String>>(){}.getType());

        for (Map.Entry<String,List<AbstractActivityPeriod>> e : obData.entrySet()){
            if (!apps.contains(e.getKey()) && (!e.getKey().equals("PC"))) obData.remove(e.getKey());
        }

        for (String e : apps){
            if (!containsApp(e,obData)){
                ActivityTracker newTracker=new AppActivityTracker(e);
                newTracker.track();

                ActivitySummary newSummary=new AppActivitySummary(newTracker.getActivityStream(),e);
                newSummary.generate();
                obData.put(e,newSummary.getAllPeriods());
            }
        }
        appController.setObData(obData);
    }
}