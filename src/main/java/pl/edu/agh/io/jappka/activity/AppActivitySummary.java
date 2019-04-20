package pl.edu.agh.io.jappka.activity;

import pl.edu.agh.io.jappka.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AppActivitySummary implements ActivitySummary {

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

        boolean firstIter = true;
        long lastIterTime = -1;
        AbstractActivityPeriod newPeriod;
        StateTransitionEvent.Type lastType = StateTransitionEvent.Type.NONFOCUSED;
        for (StateTransitionEvent event : this.activityStream.getEvents()) {
            if(firstIter) {
                lastIterTime = event.getFiringTime();
                firstIter = false;
                lastType = event.getType();
            } else {
                if(lastType == StateTransitionEvent.Type.FOCUSED)
                    newPeriod = new AppActivityPeriod(lastIterTime, event.getFiringTime(), AbstractActivityPeriod.Type.FOCUSED, this.appName);
                else
                    newPeriod = new AppActivityPeriod(lastIterTime, event.getFiringTime(), AbstractActivityPeriod.Type.NONFOCUSED, this.appName);

                this.periods.add(newPeriod);
                lastIterTime = event.getFiringTime();
                lastType = event.getType();
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

}
