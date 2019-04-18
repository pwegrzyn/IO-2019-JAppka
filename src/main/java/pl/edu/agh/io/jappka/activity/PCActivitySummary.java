package pl.edu.agh.io.jappka.activity;

import pl.edu.agh.io.jappka.util.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PCActivitySummary implements ActivitySummary {

    private ActivityStream activityStream;
    private List<AbstractActivityPeriod> periods;
    private boolean wasGenerated;

    public PCActivitySummary(ActivityStream activityStream) {
        this.activityStream = activityStream;
        this.periods = new LinkedList<>();
        this.wasGenerated = false;
    }

    @Override
    public void generate() {
        if(this.activityStream == null || this.activityStream.getEvents() == null) {
            System.err.println("PCActivitySummary:generate: ActivityStream cannot be null!");
            return;
        }

        boolean firstIter = true;
        long lastIterTime = -1;
        AbstractActivityPeriod newPeriod;
        for (StateTransitionEvent event : this.activityStream.getEvents()) {
            if(firstIter) {
                lastIterTime = event.getFiringTime();
                firstIter = false;
            } else {
                if(event.getType() == StateTransitionEvent.Type.ON) {
                    newPeriod = new PCActivityPeriod(lastIterTime, event.getFiringTime(), AbstractActivityPeriod.Type.OFF);
                    this.periods.add(newPeriod);
                    lastIterTime = event.getFiringTime();
                } else {
                    newPeriod = new PCActivityPeriod(lastIterTime, event.getFiringTime(), AbstractActivityPeriod.Type.ON);
                    this.periods.add(newPeriod);
                    lastIterTime = event.getFiringTime();
                }
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
