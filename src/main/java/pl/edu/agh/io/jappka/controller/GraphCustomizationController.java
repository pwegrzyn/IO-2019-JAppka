package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class GraphCustomizationController {

    private Stage stage;

    @FXML
    private Button CancelButton;

    @FXML
    private Button ApplyButton;

    @FXML
    private ListView<String> BarsListView;

    @FXML
    private ColorPicker ColorPicker;

    @FXML
    private Slider SizeSlider;

    @FXML
    private TextField OrderPriorityTextField;

    public void inti() {

    }

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
