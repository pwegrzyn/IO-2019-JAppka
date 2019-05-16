package pl.edu.agh.io.jappka.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;
import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.presenter.AppGUI;

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

        Timer timer = new Timer(true);

        ActivityTracker PCtracker = new PCActivityTracker();
        PCtracker.track();

        ActivitySummary PCSummary = new PCActivitySummary(PCtracker.getActivityStream());
        PCSummary.generate();

        Map<String, List<AbstractActivityPeriod>> data = new HashMap<>();
        data.put("PC", PCSummary.getAllPeriods());

        ObservableMap<String, List<AbstractActivityPeriod>> obData = FXCollections.observableHashMap();
        obData.putAll(data);

        Map<String, ActivitySummary> activities=new HashMap<>();
        activities.put("PC",PCSummary);

        AppGUI gui = new AppGUI(primaryStage,obData,activities);
        gui.initApplication();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gui.gatherData();
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