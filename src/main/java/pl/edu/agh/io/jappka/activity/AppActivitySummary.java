package pl.edu.agh.io.jappka.activity;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AppActivitySummary implements ActivitySummary {

    private final static Logger LOGGER = Logger.getLogger(AppActivitySummary.class.getName());
    private ActivityStream activityStream;
    private List<AbstractActivityPeriod> periods;
    private boolean wasGenerated;
    private String appName;

    public AppActivitySummary(ActivityStream activityStream, String appName) {
        this.activityStream = activityStream;
        this.periods = new LinkedList<>();
        this.wasGenerated = false;
        this.appName = appName;
    }

    @Override
    public void generate() {
        if(this.activityStream == null || this.activityStream.getEvents() == null) {
            System.err.println("AppActivitySummary:generate: ActivityStream cannot be null!");
            return;
        }

        this.periods.clear();
        boolean firstIter = true;
        long lastIterTime = -1;
        AbstractActivityPeriod newPeriod;
        StateTransitionEvent.Type lastType = StateTransitionEvent.Type.NONFOCUSED;
        // FIXME: check if this try-catch doesn't mess up the class (previously only for-loop)
        try {
            for (StateTransitionEvent event : this.activityStream.getEvents()) {
                if(firstIter) {
                    lastIterTime = event.getFiringTime();
                    firstIter = false;
                    lastType = event.getType();
                } else {
                    if(lastType != event.getType()) {
                        newPeriod = new AppActivityPeriod(lastIterTime, event.getFiringTime(), getPeriodType(lastType), this.appName);
                        this.periods.add(newPeriod);
                        lastIterTime = event.getFiringTime();
                        lastType = event.getType();
                    }
                }
            }
        } catch (ConcurrentModificationException exception) {
            LOGGER.warning("Concurrently modifying app activity stream");
        }
        if(!this.activityStream.getEvents().isEmpty()){
            StateTransitionEvent lastEvent = this.activityStream.getEvents().get(this.activityStream.getEvents().size() - 1);
            if(lastIterTime != lastEvent.getFiringTime()) {
                newPeriod = new AppActivityPeriod(lastIterTime, lastEvent.getFiringTime(), getPeriodType(lastEvent.getType()), this.appName);
                this.periods.add(newPeriod);
            }
        }

        this.wasGenerated = true;
    }

    @Override
    public List<AbstractActivityPeriod> getAllPeriods() {
        if(!this.wasGenerated) {
            System.err.println("First you need to generate the summary!");
            return null;
        }
        return this.periods;
    }

    @Override
    public List<AbstractActivityPeriod> getPeriodsFrom(long startTime) {
        if(!this.wasGenerated) {
            System.err.println("First you need to generate the summary!");
            return null;
        }
        return this.periods.stream().filter(period -> period.startTime > startTime).collect(Collectors.toList());
    }

    @Override
    public List<AbstractActivityPeriod> getPeriodsTo(long endTime) {
        if(!this.wasGenerated) {
            System.err.println("First you need to generate the summary!");
            return null;
        }
        return this.periods.stream().filter(period -> period.endTime < endTime).collect(Collectors.toList());
    }

    @Override
    public List<AbstractActivityPeriod> getPeriodsBetween(long startTime, long endTime) {
        if(!this.wasGenerated) {
            System.err.println("First you need to generate the summary!");
            return null;
        }
        return this.periods.stream().filter(period -> period.startTime > startTime && period.endTime < endTime)
                .collect(Collectors.toList());
    }

    public AbstractActivityPeriod.Type getPeriodType(StateTransitionEvent.Type type){
        return type == StateTransitionEvent.Type.FOCUSED ? AbstractActivityPeriod.Type.FOCUSED :
                AbstractActivityPeriod.Type.NONFOCUSED;
    }

}
