package pl.edu.agh.io.jappka.activity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class ActivityStream implements Serializable {

    private List<StateTransitionEvent> events;

    public ActivityStream() {
        this.events = new LinkedList<>();
    }

    public void appendEvent(StateTransitionEvent event) {
        this.events.add(event);
    }

    public List<StateTransitionEvent> getEvents() {
        return this.events;
    }

}
