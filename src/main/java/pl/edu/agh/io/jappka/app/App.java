package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.presenter.AppGUI;
;import java.io.IOException;
import java.util.*;


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

        Timer timer = new Timer();

        ActivityTracker PCtracker = new PCActivityTracker();
        PCtracker.track();

        ActivityTracker chromeTracker = new AppActivityTracker("firefox");
        chromeTracker.track();

        ActivitySummary chromeSummary = new AppActivitySummary(chromeTracker.getActivityStream(), "firefox");
        chromeSummary.generate();

        Map<String, List<AbstractActivityPeriod>> data = new HashMap<>();
        data.put("firefox", chromeSummary.getAllPeriods());

        ObservableMap<String, List<AbstractActivityPeriod>> obData = FXCollections.observableHashMap();
        obData.putAll(data);

        AppGUI gui = new AppGUI(primaryStage,obData);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                chromeSummary.generate();
                data.replace("chrome", chromeSummary.getAllPeriods());
            }
        }, 0, 1000);

        gui.initApplication();

    }

    public static void quit() {
        System.exit(0);
    }

}