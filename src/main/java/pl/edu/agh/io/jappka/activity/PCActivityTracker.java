package pl.edu.agh.io.jappka.activity;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;


public class PCActivityTracker implements ActivityTracker {

    private String dataDirectoryPath = "data/";
    private String appStateFilePath = dataDirectoryPath + "appState.dat";
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";
    private ActivityState currentState;
    private int refreshRateTickInSeconds;
    private Timer timer;

    public PCActivityTracker() {
        this.timer = new Timer(true);
        this.refreshRateTickInSeconds = 1;
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

        schedulePersistanceAction();
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
        createAppStateFile();
        this.currentState = new ActivityState();
        addActivityToState(System.currentTimeMillis(), StateTransitionEvent.Type.ON);
    }

    private void createAppStateFile() throws IOException {
        new File(this.activityDataDirectoryPath).mkdirs();
        File file = new File(this.appStateFilePath);
        file.createNewFile();
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
        addActivityToState(lastHeartbeatTime, StateTransitionEvent.Type.OFF);

        long currentTime = System.currentTimeMillis();
        addActivityToState(currentTime, StateTransitionEvent.Type.ON);
    }

    private void schedulePersistanceAction() {
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
        }, 0, this.refreshRateTickInSeconds * 1000);
    }

    private void addActivityToState(long time, StateTransitionEvent.Type type) {
        StateTransitionEvent newEvent = new StateTransitionEvent(type, time);
        this.currentState.getActivityStream().appendEvent(newEvent);
    }
}
