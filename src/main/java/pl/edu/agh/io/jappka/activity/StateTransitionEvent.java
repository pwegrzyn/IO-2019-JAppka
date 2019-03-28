package pl.edu.agh.io.jappka.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StateTransitionEvent implements Serializable {

    final private Type type;
    final private long firingTime;

    public StateTransitionEvent(Type type, long time) {
        this.type = type;
        this.firingTime = time;
    }

    public Type getType() {
        return type;
    }

    public long getFiringTime() {
        return firingTime;
    }

    public enum Type {
        FOCUSED,
        NONFOCUSED,
        ON,
        OFF
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(this.firingTime);
        return "EVENT: " + this.type + " | TIMESTAMP: " + sdf.format(resultdate);
    }
}
