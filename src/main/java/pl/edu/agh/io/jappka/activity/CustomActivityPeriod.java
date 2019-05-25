package pl.edu.agh.io.jappka.activity;

public class CustomActivityPeriod extends AbstractActivityPeriod{
    private String activityName;

    public CustomActivityPeriod(long start, long end, String name){
        super(start, end, Type.FOCUSED);
        this.activityName = name;
    }

    public CustomActivityPeriod(long start, long end, String name, AbstractActivityPeriod.Type type){
        super(start, end, type);
        this.activityName = name;
    }

    @Override
    public String generateInfo() {
        return "";
    }

    public String getActivityName(){
        return this.activityName;
    }
}
