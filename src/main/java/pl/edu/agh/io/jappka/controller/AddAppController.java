package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import pl.edu.agh.io.jappka.activity.*;
import javafx.scene.control.TextField;
import pl.edu.agh.io.jappka.os.NativeAccessor;
import pl.edu.agh.io.jappka.os.WindowsNativeAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAppController {

    private AppController appController;

    @FXML private TextField field;
    @FXML private ComboBox comboBox;
    private String appName;

    @FXML
    public void initialize(AppController appController) {
        this.appController=appController;

        NativeAccessor accessor = new WindowsNativeAccessor();
        List<String> apps = accessor.getActiveProcessesNames();

        comboBox.getItems().setAll(apps);
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        appController.backToMainView();
    }

    @FXML
    public void handleOnClick(ActionEvent event) {
        appName = comboBox.getSelectionModel().getSelectedItem().toString();
        field.setText(appName);
    }
    @FXML

    public void handleAddButton(ActionEvent event) {

        if(appName == null) appName = field.getText();

        ActivityTracker appTracker = new AppActivityTracker(appName);
        appTracker.track();

        ActivitySummary chromeSummary = new AppActivitySummary(appTracker.getActivityStream(), appName);
        chromeSummary.generate();

        Map<String, List<AbstractActivityPeriod>> data = new HashMap<>();
        data.put(appName, chromeSummary.getAllPeriods());

        ObservableMap<String, List<AbstractActivityPeriod>> obData = FXCollections.observableHashMap();
        obData.putAll(data);

        appController.setObData(obData);
    }
}
