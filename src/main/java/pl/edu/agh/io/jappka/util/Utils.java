package pl.edu.agh.io.jappka.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    public static String millisecondsToStringDate(long timeInMilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(timeInMilliseconds);
        return sdf.format(resultdate);
    }

    public static String millisecondsToCustomStrDate(long timeInMilliseconds, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date resultdate = new Date(timeInMilliseconds);
        return sdf.format(resultdate);
    }

    public static long strDateToMillis(String date){
        LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        return localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long strDateToMillis(String date, String pattern){
        return 0;
    }

    public static String removeExtensionFromFilename(String filename){
        Pattern p = Pattern.compile("(.*)\\.[^.]+$");
        Matcher m = p.matcher(filename);
        m.matches();
        return m.group(1);
    }

}
