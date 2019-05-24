package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AddOwnEventController {

    private AppController appController;
    private Stage stage;

    @FXML
    private TextField eventName;

    @FXML
    private DatePicker start;

    @FXML
    private DatePicker end;

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
        if (start.getValue() == null || end.getValue() == null || eventName.getText() == null){
            System.out.println("Rzuć tu jakimś tekstem żeby wybrać daty albo coś");
        }
        else{
            System.out.println(eventName.getText());
            System.out.println(start.getValue());
            System.out.println(end.getValue());
        }
    }


}
