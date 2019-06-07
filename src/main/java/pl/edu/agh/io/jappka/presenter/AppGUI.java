package pl.edu.agh.io.jappka.presenter;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.ActivitySummary;
import pl.edu.agh.io.jappka.controller.AppController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AppGUI {

    private static final Logger LOGGER = Logger.getLogger(AppGUI.class.getName());
    private Stage primaryStage;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private AppController controller;
    private Map<String,ActivitySummary> activities;

    public AppGUI(Stage primaryStage, ObservableMap<String, List<AbstractActivityPeriod>> obData, Map<String,ActivitySummary> activities){
        this.primaryStage = primaryStage;
        this.obData=obData;
        this.activities=activities;
    }

    public void initApplication() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/mainView.fxml"));
        BorderPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("JAppka Activity Tracker");

        // Set app icon
        java.awt.image.BufferedImage imageIcon = ImageIO.read(getClass().getClassLoader().getResource("image/icon2.png"));
        primaryStage.getIcons().add(SwingFXUtils.toFXImage(imageIcon, null));

        // DO NOT Call System.exit(), since the tray icon needs to stay
        primaryStage.setOnCloseRequest(e ->{
            primaryStage.close();
        });

        primaryStage.setResizable(false);
        controller = loader.getController();
        controller.setObData(obData);
        controller.setActivities(activities);
        controller.setPrimaryStageElements(primaryStage, scene);

        // Add Tray Icon
        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            LOGGER.warning("System Tray is not supported on this OS version");
            return;
        }

        // Get the System Tray
        final PopupMenu popup = new PopupMenu();
        java.awt.Image image = ImageIO.read(getClass().getClassLoader().getResource("image/icon3.png"));
        final TrayIcon trayIcon = new TrayIcon(image, "JAppka Activity Tracker");
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem displayMenu = new MenuItem("Display");
        displayMenu.addActionListener(e -> {
            Platform.runLater(() -> primaryStage.show());
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //Add components to pop-up menu
        popup.add(displayMenu);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        trayIcon.addActionListener(e -> {
            Platform.runLater(() -> primaryStage.show());
        });

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOGGER.warning("Tray Icon could not be added");
            return;
        }
    }

    public void gatherData(){
        Platform.runLater(()-> {
            controller.gatherData();
            controller.update();
        });
    }
}