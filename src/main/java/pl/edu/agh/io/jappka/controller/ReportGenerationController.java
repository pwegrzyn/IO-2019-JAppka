package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.report.ReportFileFormat;
import pl.edu.agh.io.jappka.report.ReportGenerator;
import pl.edu.agh.io.jappka.report.ReportParameters;

import java.time.LocalDate;
import java.util.logging.Logger;

public class ReportGenerationController {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationController.class.getName());
    private Stage stage;
    private ReportGenerator reportGenerator;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        this.reportGenerator = new ReportGenerator();
        this.FormatChoiceBox.setItems(FXCollections.observableArrayList(this.reportGenerator.getSupportedFormats()));
    }

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private ChoiceBox<ReportFileFormat> FormatChoiceBox;

    @FXML
    private void handleGenerateReportButton(ActionEvent event){
        if (!validateInput()) return;
        ReportParameters reportParameters = new ReportParameters(this.dateStart.getValue(), this.dateEnd.getValue(),
                this.FormatChoiceBox.getValue());
        this.reportGenerator.setReportParameters(reportParameters);
        this.reportGenerator.generateReport();
        this.stage.close();
        return;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    private boolean validateInput() {
        if(FormatChoiceBox.getValue() == null) {
            LOGGER.warning("Report file format not specified!");
            return false;
        }
        if (this.dateStart.getValue() == null || this.dateEnd.getValue() == null) return false;
        LocalDate startDate = this.dateStart.getValue();
        LocalDate endDate = this.dateEnd.getValue();
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            LOGGER.warning("Start/End Date cannot be a value after the current date!");
            return false;
        }
        if (startDate.isAfter(endDate)) {
            LOGGER.warning("End Date cannot be a value after the start date!");
            return false;
        }
        return true;
    }

}
