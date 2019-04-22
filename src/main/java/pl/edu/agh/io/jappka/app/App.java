package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.presenter.AppGUI;
;


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

        ActivityTracker chromeTracker = new AppActivityTracker("chrome");
        chromeTracker.track();

        ActivitySummary chromeSummary = new AppActivitySummary(chromeTracker.getActivityStream(), "chrome");
        chromeSummary.generate();
        chromeSummary.getAllPeriods().forEach(period -> System.out.println(period.generateInfo()));

        AppGUI gui = new AppGUI(primaryStage);
        gui.initApplication();
    }

    public static void quit() {
        System.exit(0);
    }

}