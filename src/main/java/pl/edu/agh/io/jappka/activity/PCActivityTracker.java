package pl.edu.agh.io.jappka.activity;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;


public class PCActivityTracker implements ActivityTracker {

    private String dataDirectoryPath = "data/";
    private String appStateFilePath = dataDirectoryPath + "appState.dat";
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";
    private ActivityState currentState;
    private Timer timer;

    public PCActivityTracker() {
        this.timer = new Timer(true);
    }

    @Override
    public void track() {
        try {
            if (checkIfAppStateFileExist())
                recoverAppState();
            else
                initializeAppState();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        int refreshRateTickInSeconds = 1;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    persistState();
                } catch (IOException e) {
                    System.err.println("Error while persisting heartbeat!");
                    e.printStackTrace();
                }
            }
        }, 0, refreshRateTickInSeconds * 1000);
    }

    @Override
    public ActivityStream getActivityStream() {
        return this.currentState.getActivityStream();
    }

    private boolean checkIfAppStateFileExist() {
        File file = new File(this.appStateFilePath);
        return file.exists() && !file.isDirectory();
    }

    private void initializeAppState() throws IOException {
        File file = new File(this.appStateFilePath);
        file.createNewFile();

        this.currentState = new ActivityState();

        long startTime = System.currentTimeMillis();
        StateTransitionEvent newEvent = new StateTransitionEvent(StateTransitionEvent.Type.ON, startTime);
        this.currentState.getActivityStream().appendEvent(newEvent);
    }

    private void persistState() throws IOException {
        Heartbeat newHeartbeat = new Heartbeat(System.currentTimeMillis());
        this.currentState.setLastHeartbeat(newHeartbeat);
        FileOutputStream fileOutputStream = new FileOutputStream(this.appStateFilePath);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(this.currentState);
        outputStream.close();
        fileOutputStream.close();
    }

    private void recoverAppState() throws IOException, ClassNotFoundException {
        FileInputStream inputStream = new FileInputStream(this.appStateFilePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        this.currentState = (ActivityState) objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();

        long lastHeartbeatTime = this.currentState.getLastHeartbeat().getValue();
        StateTransitionEvent closeEvent = new StateTransitionEvent(StateTransitionEvent.Type.OFF, lastHeartbeatTime);
        this.currentState.getActivityStream().appendEvent(closeEvent);

        long currentTime = System.currentTimeMillis();
        StateTransitionEvent openEvent = new StateTransitionEvent(StateTransitionEvent.Type.ON, currentTime);
        this.currentState.getActivityStream().appendEvent(openEvent);
    }
}
