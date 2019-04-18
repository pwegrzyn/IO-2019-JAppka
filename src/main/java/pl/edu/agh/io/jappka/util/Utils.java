package pl.edu.agh.io.jappka.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {

    public static String millisecondsToStringDate(long timeInMilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(timeInMilliseconds);
        return sdf.format(resultdate);
    }

}
