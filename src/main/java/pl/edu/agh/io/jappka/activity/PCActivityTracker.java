package pl.edu.agh.io.jappka.activity;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;


public class PCActivityTracker extends AbstractActivityTracker {

    public PCActivityTracker() {
        super(StateTransitionEvent.Type.ON, StateTransitionEvent.Type.OFF, "appState.dat");
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

        schedulePersistenceAction();
    }

    @Override
    protected void initializeAppState() throws IOException {
        createAppStateFile();
        this.currentState = new ActivityState();
        addActivityToState(System.currentTimeMillis(), this.activeState);
    }

    @Override
    protected void addRecoveryEvents() {
        long lastHeartbeatTime = this.currentState.getLastHeartbeat().getValue();
        addActivityToState(lastHeartbeatTime, this.inactiveState);

        long currentTime = System.currentTimeMillis();
        addActivityToState(currentTime, this.activeState);
    }
}
