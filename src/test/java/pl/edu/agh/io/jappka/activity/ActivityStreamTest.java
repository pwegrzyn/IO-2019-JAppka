package pl.edu.agh.io.jappka.activity;

import org.junit.Test;

import static org.junit.Assert.*;

public class ActivityStreamTest {

    @Test
    public void testActivityStream() {
        ActivityStream activityStream = new ActivityStream();
        long time = System.currentTimeMillis();
        StateTransitionEvent stateTransitionEvent = new StateTransitionEvent(StateTransitionEvent.Type.ON, time);
        activityStream.appendEvent(stateTransitionEvent);

        assertNotNull(activityStream.getEvents());
        assertTrue(activityStream.getEvents().contains(stateTransitionEvent));
        assertFalse(activityStream.getEvents().isEmpty());
    }
}