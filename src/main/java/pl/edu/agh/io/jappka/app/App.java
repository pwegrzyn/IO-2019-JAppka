package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;


public class App extends Application {

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

        System.out.println("Feel free to kill this program at any time - PC activity data will be stored automatically");
        try {
            Thread.sleep(9000001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void quit() {
        System.exit(0);
    }

}
