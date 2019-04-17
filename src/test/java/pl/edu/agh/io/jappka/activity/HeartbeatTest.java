package pl.edu.agh.io.jappka.activity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeartbeatTest {

    @Test
    public void heartBeatTest() {

        long heartBeatTime = System.currentTimeMillis();
        Heartbeat heartbeat = new Heartbeat(heartBeatTime);
        assertEquals(heartBeatTime, heartbeat.getValue());

    }

}
