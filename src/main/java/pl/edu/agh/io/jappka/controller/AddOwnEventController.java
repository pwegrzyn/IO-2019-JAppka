package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.util.DateTimePicker;

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
        if (start.getValue() == null || end.getValue() == null || eventName.getText().equals("")){
            System.out.println("Rzuć tu jakimś tekstem żeby wybrać daty albo coś");
        }
        else{
            System.out.println(eventName.getText());
            System.out.println(start.getDateTimeValue());
            System.out.println(end.getDateTimeValue());
        }
    }


}
