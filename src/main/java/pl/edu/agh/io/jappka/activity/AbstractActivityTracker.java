package pl.edu.agh.io.jappka.activity;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractActivityTracker implements ActivityTracker {

    private final static Logger LOGGER = Logger.getLogger(AbstractActivityTracker.class.getName());
    private String fileName;
    private String dataDirectoryPath = "data/";
    private String appStateFilePath;
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";
    protected int refreshRateTickInSeconds;
    private Timer timer;

    protected ActivityState currentState;
    protected StateTransitionEvent.Type activeState, inactiveState;

    public AbstractActivityTracker(StateTransitionEvent.Type activeState, StateTransitionEvent.Type inactiveState, String fileName){
        this.timer = new Timer(true);
        this.refreshRateTickInSeconds = 1;

        this.activeState = activeState;
        this.inactiveState = inactiveState;

        this.fileName = fileName;
        this.appStateFilePath = this.dataDirectoryPath + fileName;
    }

    @Override
    public abstract void track();

    @Override
    public ActivityStream getActivityStream() {
        return this.currentState.getActivityStream();
    }

    protected boolean checkIfAppStateFileExist() {
        File file = new File(this.appStateFilePath);
        return file.exists() && !file.isDirectory();
    }

    protected void createAppStateFile() throws IOException {
        new File(this.activityDataDirectoryPath).mkdirs();
        File file = new File(this.appStateFilePath);
        file.createNewFile();
    }

    private void persistState() throws IOException {
        Heartbeat newHeartbeat = new Heartbeat(System.currentTimeMillis());
        this.currentState.setLastHeartbeat(newHeartbeat);
        FileOutputStream fileOutputStream = new FileOutputStream(this.appStateFilePath);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        try {
            outputStream.writeObject(this.currentState);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE,"Fatal error occurred while persisting app state!", e);
            return;
        } finally {
            outputStream.close();
            fileOutputStream.close();
        }
    }

    protected void recoverAppState() throws IOException {
        FileInputStream inputStream = new FileInputStream(this.appStateFilePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            this.currentState = (ActivityState) objectInputStream.readObject();
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Fatal error occurred while recovering app state!", e);
        } finally {
            objectInputStream.close();
            inputStream.close();
        }
        addRecoveryEvents();
    }

    protected void schedulePersistenceAction() {
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

    protected void addActivityToState(long time, StateTransitionEvent.Type type) {
        StateTransitionEvent newEvent = new StateTransitionEvent(type, time);
        this.currentState.getActivityStream().appendEvent(newEvent);
    }

    protected abstract void initializeAppState() throws IOException;
    protected abstract void addRecoveryEvents();
}
