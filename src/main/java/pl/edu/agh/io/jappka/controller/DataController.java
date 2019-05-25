package pl.edu.agh.io.jappka.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import pl.edu.agh.io.jappka.Exceptions.InvalidEventException;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.activity.AppActivityPeriod;
import pl.edu.agh.io.jappka.activity.CustomActivityPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataController {
    private Map<String, List<AbstractActivityPeriod>> data;

    public DataController(Map<String, List<AbstractActivityPeriod>> data){
        this.data = data;
    }

    public void initData(){

    }

    public void addCustomEvent(long start, long end, String name) throws InvalidEventException {
        String key = "Custom events";
        AbstractActivityPeriod period = new CustomActivityPeriod(start, end, name);
        List<AbstractActivityPeriod> periods;
        if(data.containsKey(key)){
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
    }

    public boolean checkIfOverlaps(AbstractActivityPeriod period, List<AbstractActivityPeriod> periods){
        for(AbstractActivityPeriod p : periods){
            long start = period.getStartTime();
            long end = period.getEndTime();
            boolean overlaps = p.getEndTime() > start && p.getStartTime() < end;
            if(overlaps) {
                return true;
            }
        }

        return false;
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
}
