package pl.edu.agh.io.jappka.presenter;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.ActivitySummary;
import pl.edu.agh.io.jappka.controller.AppController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppGUI {
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
        loader.setLocation(getClass().getClassLoader().getResource("Jappka/view/mainView.fxml"));
        BorderPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("JAppka Activity Tracker");

        primaryStage.setOnCloseRequest(e ->{
                primaryStage.close();
                System.exit(0);
            });

        controller = loader.getController();
        controller.setObData(obData);
        controller.setActivities(activities);
        controller.setPrimaryStageElements(primaryStage, scene);
    }

    public void gatherData(){
        Platform.runLater(()-> {
            controller.gatherData();
            controller.update();
        });
    }
}