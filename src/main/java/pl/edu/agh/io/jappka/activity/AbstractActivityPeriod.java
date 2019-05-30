package pl.edu.agh.io.jappka.activity;

import java.io.Serializable;

public abstract class AbstractActivityPeriod implements Serializable {

    protected final long startTime;
    protected final long endTime;
    protected final Type type;

    public AbstractActivityPeriod(long startTime, long endTime, Type type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public abstract String generateInfo();

    public enum Type {
        ON,
        OFF,
        FOCUSED,
        NONFOCUSED,
        OTHER
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Type getType() {
        return type;
    }

}
