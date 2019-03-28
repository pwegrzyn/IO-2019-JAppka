package pl.edu.agh.io.jappka.app;

import pl.edu.agh.io.jappka.activity.ActivityTracker;
import pl.edu.agh.io.jappka.os.OSTypeManager;


public class App {

    public static void main(String[] args) {

        OSTypeManager osTypeManager = new OSTypeManager();
        if(osTypeManager.isUnsupported()) {
            System.err.println("Unsupported Operating System found!");
            quit();
        }

        ActivityTracker tracker = new ActivityTracker(osTypeManager.getType());
        tracker.init();
        tracker.track();

    }

    public static void quit() {
        System.exit(0);
    }

}
