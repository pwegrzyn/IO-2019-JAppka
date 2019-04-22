package pl.edu.agh.io.jappka.activity;

import pl.edu.agh.io.jappka.os.WindowsNativeAccessor;
import pl.edu.agh.io.jappka.util.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AppActivityTracker extends AbstractActivityTracker {
    private String app_name;
    private Timer focusTimer;
    private StateTransitionEvent.Type currentAppState;

    public AppActivityTracker(String app_name){
        super(StateTransitionEvent.Type.FOCUSED, StateTransitionEvent.Type.NONFOCUSED, app_name + ".dat");
        this.app_name = app_name;
        this.focusTimer = new Timer();
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
        scheduleRefreshAction();
    }

    @Override
    protected void initializeAppState() throws IOException {
        createAppStateFile();
        this.currentState = new ActivityState();
        StateTransitionEvent.Type currentType = isFocused() ? this.activeState : this.inactiveState;
        this.currentAppState = currentType;
        addActivityToState(System.currentTimeMillis(), currentType);
    }

    @Override
    protected void addRecoveryEvents() {
        long lastHeartbeatTime = this.currentState.getLastHeartbeat().getValue();
        addActivityToState(lastHeartbeatTime, this.inactiveState);

        long currentTime = System.currentTimeMillis();

        if(isFocused()) {
            this.currentAppState = this.activeState;
            addActivityToState(currentTime, this.activeState);
        }
        this.currentAppState = this.inactiveState;
    }

    private void refreshState(){
        StateTransitionEvent.Type newAppState = isFocused() ? this.activeState : this.inactiveState;
        if(newAppState != this.currentAppState){
            long currentTime = System.currentTimeMillis();
            addActivityToState(currentTime, newAppState);
            this.currentAppState = newAppState;
        }
    }

    private boolean isFocused(){
        String activeWindow = new WindowsNativeAccessor().getActiveWindowProcessName();
        String procName = activeWindow.isEmpty() ? "" : Utils.removeExtensionFromFilename(activeWindow);

        return procName.toLowerCase().equals(this.app_name.toLowerCase());
    }

    private void scheduleRefreshAction(){
        focusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshState();
            }
        }, 0, this.refreshRateTickInSeconds * 1000);
    }
}
