package pl.edu.agh.io.jappka.controller;

import javafx.application.Platform;
import pl.edu.agh.io.jappka.Exceptions.InvalidEventException;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.AppActivityPeriod;
import pl.edu.agh.io.jappka.activity.CustomActivityPeriod;
import pl.edu.agh.io.jappka.activity.CustomEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataController {

    private Map<String, List<AbstractActivityPeriod>> data;
    private CustomEventManager customEventManager;
    private String customEventsKey = "Custom";

    public DataController(Map<String, List<AbstractActivityPeriod>> data){

        this.data = data;
        this.customEventManager = new CustomEventManager();
    }

    public void initData() {}

    public void addCustomEvent(long start, long end, String name) throws InvalidEventException {
        String key = this.customEventsKey;
        AbstractActivityPeriod period = new CustomActivityPeriod(start, end, name);
        List<AbstractActivityPeriod> periods;
        if(data.containsKey(key) && data.get(key).size() > 0){
            periods = data.get(key);

            if(checkIfOverlaps(period, periods)){
                throw new InvalidEventException();
            }

            long oldEnd = periods.get(periods.size() - 1).getEndTime();
            AbstractActivityPeriod emptyPeriod = new CustomActivityPeriod(oldEnd, start, "", AbstractActivityPeriod.Type.NONFOCUSED);
            periods.add(emptyPeriod);
        }
        else{
            periods = new ArrayList<>();
        }
        periods.add(period);
        data.put(key, periods);
        this.customEventManager.persist(periods);
    }

    public boolean checkIfOverlaps(AbstractActivityPeriod period, List<AbstractActivityPeriod> periods){
        for(AbstractActivityPeriod p : periods){
            long start = period.getStartTime();
            long end = period.getEndTime();
            boolean overlaps = p.getEndTime() > start && p.getStartTime() < end;
            if(overlaps && p.getType() != AbstractActivityPeriod.Type.NONFOCUSED) {
                return true;
            }
        }

        return false;
    }

    public void loadPreviousEvents() {
        List<AbstractActivityPeriod> previousPeriods = this.customEventManager.load();
        if (previousPeriods != null) {
            this.data.put(this.customEventsKey, previousPeriods);
        }
    }

    private void removeOverlapping(AbstractActivityPeriod period){
        for(Map.Entry<String, List<AbstractActivityPeriod>> e : data.entrySet()){
            if(e.getValue().isEmpty() || !(e.getValue().get(0) instanceof AppActivityPeriod)){
                continue;
            }

            List<AbstractActivityPeriod> toRemove = new ArrayList<>();
            List<AbstractActivityPeriod> toAdd = new ArrayList<>();

            for(AbstractActivityPeriod p : e.getValue()) {
                AppActivityPeriod oldPeriod = (AppActivityPeriod)p;
                AbstractActivityPeriod.Type activityType = p.getType();
                if(activityType == AbstractActivityPeriod.Type.NONFOCUSED)
                    continue;

                long start = period.getStartTime();
                long end = period.getEndTime();
                boolean overlaps = p.getEndTime() > start && p.getStartTime() < end;

                if(overlaps){
                    if(p.getStartTime() < start){
                        AbstractActivityPeriod preceding = new AppActivityPeriod(p.getStartTime(), start, activityType, oldPeriod.getAppName());
                        toAdd.add(preceding);
                    }
                    if(p.getEndTime() > end){
                        AbstractActivityPeriod succeeding = new AppActivityPeriod(end, p.getEndTime(), activityType, oldPeriod.getAppName());
                        toAdd.add(succeeding);
                    }

                    toRemove.add(p);
                    System.out.println(oldPeriod.getAppName());
                }
            }
            Platform.runLater(() ->{
                e.getValue().addAll(toAdd);
                e.getValue().removeAll(toRemove);
            });
        }

    }

    public CustomEventManager getCustomEventManager() {
        return customEventManager;
    }
}
