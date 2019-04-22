package pl.edu.agh.io.jappka.activity;

import pl.edu.agh.io.jappka.util.Utils;

public class AppActivityPeriod extends AbstractActivityPeriod {

    private String notes;
    private String appName;

    public AppActivityPeriod(long startTime, long endTime, Type type, String appName) {
        super(startTime, endTime, type);
        this.appName = appName;
    }

    @Override
    public String generateInfo() {
        StringBuilder info = new StringBuilder("");

        info.append("--- App Activity Period: ---\n");
        info.append("FROM: " + Utils.millisecondsToStringDate(this.startTime) + "\n");
        info.append("TO: " + Utils.millisecondsToStringDate(this.endTime) + "\n");
        switch (this.type) {
            case FOCUSED:
                info.append("TYPE: FOCUSED" + "\n");
                break;
            case NONFOCUSED:
                info.append("TYPE: NONFOCUSED" + "\n");
                break;
            case OTHER:
                info.append("TYPE: NO INFO" + "\n");
                break;
        }
        if(this.notes != null) info.append("NOTES: " + this.notes + "\n");
        info.append("---------------------------\n");

        return info.toString();
    }

    public String getNotes() {
        return notes;
    }

    public String getAppName() {return appName; }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
