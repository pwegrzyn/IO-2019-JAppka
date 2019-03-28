package pl.edu.agh.io.jappka.app;

import pl.edu.agh.io.jappka.activity.ActivityTracker;
import pl.edu.agh.io.jappka.activity.PCActivityTracker;

import org.apache.commons.exec.OS;
import pl.edu.agh.io.jappka.activity.StateTransitionEvent;


public class App {

    public static void main(String[] args) {

        if(!OS.isFamilyWindows()) {
            System.err.println("Unsupported Operating System found!");
            quit();
        }

        ActivityTracker PCtracker = new PCActivityTracker();
        PCtracker.track();

        System.out.println(System.getProperty("user.dir"));


        // For PC Activity Tracking purpose
        for(StateTransitionEvent ev : PCtracker.getActivityStream().getEvents()) {
            System.out.println(ev);
        }
        System.out.println("Feel free to kill this program at any moment.");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void quit() {
        System.exit(0);
    }

}
