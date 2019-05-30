package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class InitHelpScreenController {

    private Stage stage;
    private final static Logger LOGGER = Logger.getLogger(InitHelpScreenController.class.getName());
    private String dataDirectoryPath = "data/";
    private String appStateFilePath = dataDirectoryPath + "skip_init";
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";
    private boolean skipWindow;

    @FXML
    private Button OkayButton;

    @FXML
    private CheckBox SkipCheckBox;

    @FXML
    private Label ContentLabel;

    public InitHelpScreenController() {
        this.skipWindow = false;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        this.ContentLabel.setText("The App automatically tracks activity from Apps you select. \n\n" +
                "The main view contains activity bars for the PC (when was it ON and when OFF) and for Custom Events. " +
                "To add a new app go to Actions -> Add App. To add a new custom event visit Actions -> Add Custom Event. " +
                "You can also delete custom events by right clicking on them and selecting the appropriate option.\n\n" +
                "The App also allows you to generate a XLSX or CSV formatted report of your activity (Actions -> Generate Report).\n\n" +
                "To save/load your app's configuration visit the Configuration sub-menu in Actions.\n\n" +
                "To customize the appearance of the app go to Customization. There you can change the theme of the app as well as " +
                "customize the progress bar for each app individually.\n\n" +
                "After exiting the app automatically goes to an idle state (the icon is still visible in the System Tray) where it " +
                "silently continues to track your activity. To full close the app - right click on Tray Icon -> Exit.\n\n" +
                "To totally reset the state of the app just delete the ./data folder.");
        this.ContentLabel.setWrapText(true);
        this.ContentLabel.setTextAlignment(TextAlignment.JUSTIFY);

        if (fileExists()) {
            this.skipWindow = true;
        }
    }

    public boolean shouldSkipWindow() {
        return this.skipWindow;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (this.SkipCheckBox.isSelected() && !fileExists()) {
            try {
                createAppStateFile();
            } catch (IOException e) {
                LOGGER.warning("Could not save a config file!");
                this.stage.close();
            }
        }
        this.stage.close();
    }

    private void createAppStateFile() throws IOException {
        new File(this.activityDataDirectoryPath).mkdirs();
        File file = new File(this.appStateFilePath);
        file.createNewFile();
    }

    private boolean fileExists() {
        File file = new File(this.appStateFilePath);
        return file.exists() && !file.isDirectory();
    }

}
