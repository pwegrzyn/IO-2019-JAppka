package pl.edu.agh.io.jappka.activity;

import pl.edu.agh.io.jappka.util.Utils;

public class PCActivityPeriod extends AbstractActivityPeriod {

    private String notes;

    public PCActivityPeriod(long startTime, long endTime, Type type) {
        super(startTime, endTime, type);
    }

    @Override
    public String generateInfo() {
        StringBuilder info = new StringBuilder("");

        info.append("--- PC Activity Period: ---\n");
        info.append("FROM: " + Utils.millisecondsToStringDate(this.startTime) + "\n");
        info.append("TO: " + Utils.millisecondsToStringDate(this.endTime) + "\n");
        switch (this.type) {
            case ON:
                info.append("TYPE: ON" + "\n");
                break;
            case OFF:
                info.append("TYPE: OFF" + "\n");
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

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
