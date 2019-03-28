package pl.edu.agh.io.jappka.activity;

import java.io.Serializable;


public class ActivityState implements Serializable {

    private Heartbeat lastHeartbeat;
    private ActivityStream activityStream;

    public ActivityState() {
        this.activityStream = new ActivityStream();
    }

    public ActivityStream getActivityStream() {
        return activityStream;
    }

    public void setActivityStream(ActivityStream activityStream) {
        this.activityStream = activityStream;
    }

    public Heartbeat getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Heartbeat lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}
