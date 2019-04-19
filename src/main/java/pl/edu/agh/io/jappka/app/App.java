package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.controller.AppController;

import java.io.IOException;


public class App extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!OS.isFamilyWindows()) {
            System.err.println("Unsupported Operating System found!");
            quit();
        }

        ActivityTracker PCtracker = new PCActivityTracker();
        PCtracker.track();

        ActivitySummary PCsummary = new PCActivitySummary(PCtracker.getActivityStream());
        PCsummary.generate();
        PCsummary.getAllPeriods().forEach(period -> System.out.println(period.generateInfo()));

        this.primaryStage = primaryStage;
        initApplication();
    }

    private void initApplication() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Jappka/view/mainView.fxml"));
        BorderPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        AppController controller = loader.getController();
        controller.setPrimaryStageElements(primaryStage);
    }

    public static void quit() {
        System.exit(0);
    }

}
