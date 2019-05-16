package pl.edu.agh.io.jappka.controller;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.report.ReportFileFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReportGenerationController {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationController.class.getName());
    private Stage stage;
    private ObservableMap<String, List<AbstractActivityPeriod>> dataCollection;
    private final long MILISECONDS_IN_DAY = 86_400 * 1000;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(ObservableMap<String, List<AbstractActivityPeriod>> data) {
        this.dataCollection = data;
    }

    public void init() {
        this.FormatChoiceBox.setItems(FXCollections.observableArrayList(ReportFileFormat.CSV, ReportFileFormat.XLSX));
    }

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private ChoiceBox<ReportFileFormat> FormatChoiceBox;

    @FXML
    private void handleGenerateReportButton(ActionEvent event) {

        if (validateInput()) {
            LocalDate startDate = this.dateStart.getValue();
            LocalDate endDate = this.dateEnd.getValue();


            // TODO - parsing this to csv data
            try {
                generateReport(startDate, endDate, FormatChoiceBox.getValue());

            } catch (IOException ex) {
                //TODO generate window with errror
                ex.printStackTrace();
            }


            this.stage.close();
        }
    }

    private void generateXLSXReport(Map<String, Long> outputUsage) {

    }

    private void generateReport(LocalDate startDate, LocalDate endDate, ReportFileFormat fileFormat) throws IOException {
        //TODO determining format and path
        String filedir = "C:/Users/Timelock/Documents/raport-test1.csv";
        //TODO move this to another method and correct file creation + truncate
        try {
            File file = new File(filedir);
            if (file.createNewFile()) {
                System.out.println("Creating file succesful");
            } else throw new IOException("Creating file failed!");
        } catch (IOException ex) {
            //TODO error creating new file
            ex.printStackTrace();
        }

        long start = dateToEpochMilis(startDate);
        long end = dateToEpochMilis(endDate);
        //Get all active apps in given period of time minus PC
        List<String> activeApps = new ArrayList<>();
        activeApps.addAll(filterData(start, end).keySet());
        activeApps.remove("PC");
        //Add the apps to the standard headers
        List<String> headers = new ArrayList<>(Arrays.asList("DATA", "PC WŁĄCZONY", "PC AKTYWNY"));
        headers.addAll(activeApps);
        //Init headers and file
        FileWriter out = new FileWriter(filedir);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(headers);
            int days = (int) ((end - start) / MILISECONDS_IN_DAY);
            //Iterate through days we want reported
            for (int i = 0; i < days; i++) {
                //Filter the data accordingly to day processed
                Map<String, Long> usageForDay = filterData(start + (i * MILISECONDS_IN_DAY), end + (i * MILISECONDS_IN_DAY));
                LocalDate calculatedDay = startDate.plusDays(i);
                long allAppsUsage = getAppsUsage(usageForDay);

                //Sequentially print out records for:
                List<Long> usages = activeApps.stream()
                        .map((app) -> usageForDay.get(app))
                        .collect(Collectors.toList());

                List<Object> record = Arrays.asList(calculatedDay, usageForDay.get("PC"), allAppsUsage);
                record.addAll(usages);

                printer.printRecord(record);
            }

        }


    }

    //methods excludes pc from calculation and reduces the value to raw app usage
    private long getAppsUsage(Map<String, Long> usageForDay) {
        final long[] wholeActivity = {0};
        usageForDay.forEach((key, value) -> {
            if (!Objects.equals(key, "PC"))
                wholeActivity[0] += value;
        });
        return wholeActivity[0];
    }


    private long dateToEpochMilis(LocalDate value) {
        ZoneId zoneId = ZoneId.systemDefault();
        return value.atStartOfDay(zoneId).toEpochSecond() * 1000;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    private Map<String, Long> filterData(long start, long end) {

        Map<String, Long> outputUsage = new HashMap<>();

        for (String application : dataCollection.keySet()) {
            //get all periods for the specific app
            List<AbstractActivityPeriod> activityPeriods = dataCollection.get(application);
            //Filter relevant periods

            activityPeriods = activityPeriods.stream()
                    .filter((period) -> period.getType() == AbstractActivityPeriod.Type.FOCUSED || period.getType() == AbstractActivityPeriod.Type.ON)
                    .collect(Collectors.toList());
            for (AbstractActivityPeriod activityPeriod : activityPeriods) {
                //verify which periods we want to include in report
                Long time_to_add = calculateRelevantTime(activityPeriod, start, end);
                //If the time is relevant
                Long correctedUsage = outputUsage.get(application);

                if (!time_to_add.equals(0L))
                    outputUsage.put(application, (correctedUsage == null ? 0 : correctedUsage) + time_to_add);

            }
        }
        return outputUsage;
    }

    private boolean validateInput() {
        if (FormatChoiceBox.getValue() == null) {
            LOGGER.warning("Report file format not specified!");
            return false;
        }
        if (this.dateStart.getValue() == null || this.dateEnd.getValue() == null) return false;
        LocalDate startDate = this.dateStart.getValue();
        LocalDate endDate = this.dateEnd.getValue();
        if (startDate.isAfter(LocalDate.now())) {
            LOGGER.warning("Start Date cannot be a value after the current date!");
            return false;
        }
        if (startDate.isAfter(endDate)) {
            LOGGER.warning("End Date cannot be a value after the start date!");
            return false;
        }
        if (startDate.equals(endDate)) {
            LOGGER.warning("Cannot be the same day!");
            return false;
        }
        return true;
    }


    @NotNull
    private Long calculateRelevantTime(AbstractActivityPeriod activityPeriod, long startEpochTime, long endEpochTime) {
        //need to catch all the cases and calculate precisely what we want to calculate
        //Case 1: Period begins before startEpoch and ends after it
        if (activityPeriod.getStartTime() < startEpochTime && activityPeriod.getEndTime() > startEpochTime) {
            //check if it ends before or after endEpochTime
            if (activityPeriod.getEndTime() > endEpochTime)
                return endEpochTime - startEpochTime;
            else
                return activityPeriod.getEndTime() - startEpochTime;

        } else if (activityPeriod.getStartTime() >= startEpochTime && activityPeriod.getEndTime() <= endEpochTime) {
            if (activityPeriod.getEndTime() <= endEpochTime)
                return activityPeriod.getEndTime() - activityPeriod.getStartTime();
            else
                return endEpochTime - activityPeriod.getStartTime();
        } else
            return 0L;
    }
}
