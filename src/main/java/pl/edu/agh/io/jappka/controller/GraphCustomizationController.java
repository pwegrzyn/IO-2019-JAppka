package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GraphCustomizationController {

    private Stage stage;

    @FXML
    private Button CancelButton;

    @FXML
    private Button ApplyButton;

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void handleApply(ActionEvent event) {
        this.stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
