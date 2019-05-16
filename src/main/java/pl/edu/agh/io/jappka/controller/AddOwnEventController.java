package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AddOwnEventController {

    private AppController appController;
    private Stage stage;

    @FXML
    private DatePicker start;

    @FXML
    private DatePicker end;

    public void initialize(AppController appController) {

        this.appController = appController;
    }

    @FXML
    public void handleOnTextChange(KeyEvent keyEvent) {
        // TODO Tutaj tez masz :V
        System.out.println(keyEvent.getText());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleBackButton(ActionEvent event){
        this.stage.close();
    }

    @FXML
    private void handleAddEventAction(ActionEvent event){
        System.out.println("Uzupełnij to Patryk :V");
    }


}
