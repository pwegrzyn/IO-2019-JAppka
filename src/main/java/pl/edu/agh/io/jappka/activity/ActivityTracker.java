package pl.edu.agh.io.jappka.activity;


public interface ActivityTracker {

    /*Initiate the process of tracking a given system in the background*/
    void track();

    /*Get all the activities which were captured by the tracker during the tracking period.
    This stream of state transition events can be then transformed to an activity summary object.*/
    ActivityStream getActivityStream();

}
