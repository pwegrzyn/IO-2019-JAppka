package pl.edu.agh.io.jappka.activity;

import java.util.List;

public interface ActivitySummary {

    /*Reconstruct a summary from an ActivityStream object*/
    void generate();

    /*Return a continuous list of all the periods of activity/inactivity*/
    List<AbstractActivityPeriod> getAllPeriods();

    /*Return only the periods starting from a given moment up till now*/
    List<AbstractActivityPeriod> getPeriodsFrom(long startTime);

    /*Return only the periods ending in a given moment (from the beginning)*/
    List<AbstractActivityPeriod> getPeriodsTo(long endTime);

    /*Return only the periods between given moments*/
    List<AbstractActivityPeriod> getPeriodsBetween(long startTime, long endTime);

}
