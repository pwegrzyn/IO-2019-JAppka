package pl.edu.agh.io.jappka.activity;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StateTransitionEventTest {

    @Test
    public void stateTransitionEventTest() {

        long firingTime = System.currentTimeMillis();
        StateTransitionEvent.Type type = StateTransitionEvent.Type.ON;

        StateTransitionEvent event = new StateTransitionEvent(type, firingTime);

        assertEquals(event.getFiringTime(), firingTime);
        assertEquals(event.getType(), type);
        assertNotNull(event.toString());
        assertTrue(event.toString().contains("EVENT"));
        assertTrue(event.toString().contains("TIMESTAMP"));

    }

}
