package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.logging.Logger;

public class ReportGenerationController {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationController.class.getName());
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
        validateDates();
    }

    private boolean validateDates() {
        LocalDate startDate = this.dateStart.getValue();
        LocalDate endDate = this.dateEnd.getValue();
        if (startDate.isAfter(LocalDate.now())) {
            LOGGER.warning("Start Date cannot be a value after the current date!");
            return false;
        }
        return true;
    }

}
