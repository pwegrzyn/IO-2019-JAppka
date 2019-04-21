package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;

public class GenerateGraphController {

    private AppController appController;

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    public void initialize(AppController appController) {

        this.appController = appController;
    }


    @FXML
    private void handleBackButton(ActionEvent event){
        appController.backToMainView();
    }

    @FXML
    private void handleGenerateReportButton(ActionEvent event){
        System.out.println("Generate report");
    }


}
