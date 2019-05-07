package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.stage.Stage;

public class GraphColorPickerController {
    @FXML
    ColorPicker activeColorPicker;
    @FXML
    ColorPicker inactiveColorPicker;
    @FXML
    ColorPicker otherColorPicker;

    private Stage stage;

    @FXML
    private void handleChangeColors(ActionEvent event) {

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
