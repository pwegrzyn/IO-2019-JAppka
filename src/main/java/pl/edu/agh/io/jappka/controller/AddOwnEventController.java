package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.Exceptions.InvalidEventException;
import pl.edu.agh.io.jappka.util.DateTimePicker;
import pl.edu.agh.io.jappka.util.Utils;

import java.time.ZoneId;

public class AddOwnEventController {

    private AppController appController;
    private Stage stage;

    @FXML
    private TextField eventName;

    @FXML
    private DateTimePicker start;

    @FXML
    private DateTimePicker end;

    public void initialize(AppController appController) {
        this.appController = appController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleBackButton(ActionEvent event){
        this.stage.close();
    }


    @FXML
    private void handleAddEventAction(ActionEvent event) {
        if(addEventParametersAreOk())
        {
            long startTime = start.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTime = end.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            DataController controller = appController.getDataController();
            try {
                controller.addCustomEvent(startTime, endTime, eventName.getText());
            }
            catch(InvalidEventException e){
                showPopUpDialog(Alert.AlertType.ERROR, "Error", "Error while adding event", "Events are overlapping!");
            }
        }
    }

    private boolean addEventParametersAreOk(){
        if (start.getValue() == null || end.getValue() == null || eventName.getText().equals("")){
            showPopUpDialog(Alert.AlertType.ERROR, "Error", "Error while adding event", "One of the values is null");
            return false;
        }
        long startTime = start.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = end.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if(startTime >= endTime){
            showPopUpDialog(Alert.AlertType.ERROR, "Error", "Error while adding event", "End time is not later than start time.");
            return false;
        }
        return true;
    }

    private void showPopUpDialog(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
