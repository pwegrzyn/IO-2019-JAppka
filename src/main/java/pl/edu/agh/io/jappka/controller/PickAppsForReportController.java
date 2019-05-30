package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PickAppsForReportController {

    private AppController appController;
    private ReportGenerationController reportGenerationController;

    @FXML
    private TextField appNameField;
    @FXML
    private ListView selectAppsView;
    @FXML
    private Button addAppButton;
    @FXML
    private ListView appsChosenView;

    private String appName;
    private Set<String> apps = new HashSet<>();
    private Stage stage;

    @FXML
    public void initialize(AppController appController, Set<String> allAppsInPeriod, ReportGenerationController reportGenerationController) {
        this.appController=appController;
        this.reportGenerationController = reportGenerationController;
        for(String app : allAppsInPeriod)
            if (!app.equals("PC")) this.apps.add(app);
        selectAppsView.getItems().setAll(apps);
    }

    public void setStage(Stage stage) {

        this.stage = stage;
    }

    @FXML
    public void handleAddButton(ActionEvent event) {

        if(!this.appsChosenView.getItems().contains(appName)){
            this.appsChosenView.getItems().add(appName);
        }
        addAppButton.setDisable(true);
    }

    @FXML
    public void handleOnAppClicked(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            appName = selectAppsView.getSelectionModel().getSelectedItem().toString();
        }
        catch (NullPointerException ex) {
            return;
        }
        addAppButton.setDisable(false);
        appNameField.setText(appName);
    }

    public void handleOnTextChange(KeyEvent keyEvent) {
        if(!keyEvent.getCode().isLetterKey() && !keyEvent.getCode().isArrowKey() && !keyEvent.getCode().equals(KeyCode.BACK_SPACE)) return;
        if (keyEvent.getCode().equals(KeyCode.DOWN) || keyEvent.getCode().equals(KeyCode.UP)){
            selectAppsView.getFocusModel().focus(0);
            selectAppsView.requestFocus();
            return;
        }
        String textInput = appNameField.getText();
        String lastLetter = keyEvent.getCode().toString().toLowerCase();
        if(!keyEvent.getCode().equals(KeyCode.BACK_SPACE))
            textInput = appNameField.getText() + lastLetter;
        else
            if(textInput.length() > 0)
                textInput = textInput.substring(0, textInput.length()-1);
        updateView(textInput);

    }

    private void updateView(String word){
        Set<String> appsUpdated = this.apps.stream()
                .filter(app -> app.toLowerCase().startsWith(word.toLowerCase()))
                .collect(Collectors.toSet());
        selectAppsView.getItems().setAll(appsUpdated);
    }


    public void handleSelectApp(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            appName = selectAppsView.getFocusModel().getFocusedItem().toString();
            appNameField.setText(appName);
        }
    }

    @FXML
    public void handleDoneButton(ActionEvent event) {
        Set<String> appsChosen = new HashSet<>();
        appsChosen.add("PC");
        appsChosen.addAll(appsChosenView.getItems());
        this.reportGenerationController.setAppsChosen(appsChosen);
        this.stage.close();
    }

}
