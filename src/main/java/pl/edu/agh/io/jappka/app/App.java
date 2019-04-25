package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.presenter.AppGUI;
import java.util.Timer;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;


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

        Timer timer = new Timer(true);

        ActivityTracker PCtracker = new PCActivityTracker();
        PCtracker.track();

        ActivityTracker chromeTracker = new AppActivityTracker("chrome");
        chromeTracker.track();

        ActivitySummary chromeSummary = new AppActivitySummary(chromeTracker.getActivityStream(), "chrome");
        chromeSummary.generate();

        Map<String, List<AbstractActivityPeriod>> data = new HashMap<>();
        data.put("chrome", chromeSummary.getAllPeriods());

        ObservableMap<String, List<AbstractActivityPeriod>> obData = FXCollections.observableHashMap();
        obData.putAll(data);

        AppGUI gui = new AppGUI(primaryStage,obData);

        gui.initApplication();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                chromeSummary.generate();
                obData.put("chrome", chromeSummary.getAllPeriods());
                gui.update();
            }
        }, 0, 1000);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void quit() {
        System.exit(0);
    }

}