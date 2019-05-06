package pl.edu.agh.io.jappka.presenter;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.controller.AppController;

import java.io.IOException;
import java.util.List;

public class AppGUI {
    private Stage primaryStage;
    private ObservableMap<String, List<AbstractActivityPeriod>> obData;
    private AppController controller;

    public AppGUI(Stage primaryStage,ObservableMap<String, List<AbstractActivityPeriod>> obData){
        this.primaryStage = primaryStage;
        this.obData=obData;
    }

    public void initApplication() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Jappka/view/mainView.fxml"));
        BorderPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("JAppka Activity Tracker");
        controller = loader.getController();
        controller.setObData(obData);
        controller.setPrimaryStageElements(primaryStage, scene);
    }

    public void update(){
        Platform.runLater(()-> {
            controller.update();
        });
    }
}