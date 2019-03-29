package pl.edu.agh.io.jappka.activity;

import java.io.Serializable;

public class Heartbeat implements Serializable {

    final private long value;

    public Heartbeat(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

}
