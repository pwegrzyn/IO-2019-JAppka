package pl.edu.agh.io.jappka.controller;

import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReportGenerationController {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationController.class.getName());
    private Stage stage;
    private ObservableMap<String, List<AbstractActivityPeriod>> dataCollection;
    private final long MILISECONDS_IN_DAY = 86_400_000;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(ObservableMap<String, List<AbstractActivityPeriod>> data) {
        this.dataCollection = data;
    }

    public void init() {
    }

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private void handleGenerateReportButton(ActionEvent event) {

        if (validateInput()) {
            LocalDate startDate = this.dateStart.getValue();
            LocalDate endDate = this.dateEnd.getValue();
            // TODO - parsing this to csv data
            try {

                File initialFile = chooseFileToSave();
                if (initialFile == null)
                    return;
                generateReport(startDate, endDate, initialFile);
            } catch (IOException ex) {
                //Generate an alert
                String header = "File generation error";
                String content = "Error has occurred while saving the file. Try again, or contact your administrator";
                reportGenerateDialog(header, content, Alert.AlertType.ERROR);
            }
            this.stage.close();
        }
    }

    private void reportGenerateDialog(String header, String content, Alert.AlertType type) {
        String title = "Report generation dialog";
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private File chooseFileToSave() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter CSVfilter = new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
        FileChooser.ExtensionFilter XLSXfilter = new FileChooser.ExtensionFilter("XLSX file (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(CSVfilter);
        fileChooser.getExtensionFilters().add(XLSXfilter);
        fileChooser.setTitle("Save report file to...");
        fileChooser.setInitialFileName("usage_report-" + dateStart.getValue().toString() + "--" + dateEnd.getValue().toString());
        return fileChooser.showSaveDialog(stage);
    }

    private void generateReport(LocalDate startDate, LocalDate endDate, File file) throws IOException {

        switch(FilenameUtils.getExtension(file.getAbsolutePath())) {
            case "csv":
                printToCsv(startDate, endDate, file);
                break;
            case "xlsx":
                printToXlsx(startDate, endDate, file);
                break;
            default:
                LOGGER.warning("Failed to parse the correct extension from FileChooser output file.");
                break;
        }

    }

    private void printToCsv(LocalDate startDate, LocalDate endDate, File file) throws IOException {
        long start = dateToEpochMilis(startDate);
        long end = dateToEpochMilis(endDate);
        FileWriter out = new FileWriter(file.getAbsolutePath());
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(getHeaders(start, end));
            int days = (int) ((end - start) / MILISECONDS_IN_DAY);
            //FIXME: remove this
            System.out.println("Generating report for " + days + " days");
            //get all apps we want to include in the report
            List<String> reportedApps = new ArrayList<>(filterData(start, end).keySet());

            //Iterate through days we want reported
            for (int i = 0; i < days; i++) {
                //Filter the data accordingly to day processed
                Map<String, Long> concreteDayUsages = filterData(start + (i * MILISECONDS_IN_DAY), end + (i * MILISECONDS_IN_DAY));
                LocalDate calculatedDay = startDate.plusDays(i);
                long allAppsUsage = getAppsUsage(concreteDayUsages);
                //Remove PC from all apps usage - we have it separately reported by our own label
                reportedApps.remove("PC");
                //Get all wanted reported apps

                List<Long> usages = reportedApps.stream()
                        .map(concreteDayUsages::get)
                        .collect(Collectors.toList());

                List<Object> record = new ArrayList<>(Arrays.asList(calculatedDay, concreteDayUsages.get("PC"), allAppsUsage));
                //Remove pc's value, we got it above
                concreteDayUsages.remove("PC");
                record.addAll(usages);

                printer.printRecord(record);
            }
        }
    }

    // TODO: fill the workbook with report data
    private void printToXlsx(LocalDate startDate, LocalDate endDate, File file) throws IOException {
        long start = dateToEpochMilis(startDate);
        long end = dateToEpochMilis(endDate);

        // Init the XLSX workbook
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Activity Report");

        // Prepare header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create the header (stub)
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < 5; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue("Col " + i);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Actually create all rows
        //TODO: fill in the report data

        // Resize all columns to fit the data
        for(int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    //TODO: If user wants specific apps, we want to adjust headers to this scenario
    private List<String> getHeaders(long start, long end) {
        List<String> activeApps = new ArrayList<>();
        activeApps.addAll(filterData(start, end).keySet());
        activeApps.remove("PC");
        //Add the apps to the standard headers
        List<String> headers = new ArrayList<>(Arrays.asList("DATE", "PC ON", "PC ACTIVE"));
        headers.addAll(activeApps);
        return headers;
    }

    //methods excludes pc from calculation and reduces the value to raw app usage
    private long getAppsUsage(Map<String, Long> usageForDay) {
        //Looks weird, but lambdas require the variable reference to be final
        final long[] wholeActivity = {0};
        usageForDay.entrySet().forEach((entry) -> {
            if (!Objects.equals(entry.getKey(), "PC"))
                wholeActivity[0] += entry.getValue();
        });
        return wholeActivity[0];
    }

    //Returns miliseconds (pretty obvious imo)
    private long dateToEpochMilis(LocalDate value) {
        ZoneId zoneId = ZoneId.systemDefault();
        return value.atStartOfDay(zoneId).toEpochSecond() * 1000;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    //FIXME: need to test this method
    private Map<String, Long> filterData(long start, long end) {

        Map<String, Long> outputUsage = new HashMap<>();

        for (String application : dataCollection.keySet()) {
            //Get all periods for one application
            List<AbstractActivityPeriod> activityPeriods = dataCollection.get(application);
            //Filter periods which are active
            activityPeriods = activityPeriods.stream()
                    .filter((period) -> period.getType() == AbstractActivityPeriod.Type.FOCUSED || period.getType() == AbstractActivityPeriod.Type.ON)
                    .collect(Collectors.toList());

            for (AbstractActivityPeriod activityPeriod : activityPeriods) {
                //Get time delta (the amount of period between start and end epoch time)
                Long timeDelta = calculateRelevantTime(activityPeriod, start, end);

                Long correctedUsage = outputUsage.get(application);

                outputUsage.put(application, (correctedUsage == null ? 0 : correctedUsage) + timeDelta);

            }
        }
        return outputUsage;
    }

    private boolean validateInput() {
        if (this.dateStart.getValue() == null || this.dateEnd.getValue() == null) return false;
        LocalDate startDate = this.dateStart.getValue();
        LocalDate endDate = this.dateEnd.getValue();
        if (startDate.isAfter(LocalDate.now())) {
            String header = "Wrong date selection";
            String content = "Start Date cannot be a value after the current date!";
            reportGenerateDialog(header,content, Alert.AlertType.ERROR);
            return false;
        }
        if (startDate.isAfter(endDate)) {
            String header = "Wrong date selection";
            String content = "End Date cannot be a value after the start date!";
            reportGenerateDialog(header,content, Alert.AlertType.ERROR);
            return false;
        }
        if (startDate.equals(endDate)) {
            String header = "Wrong date selection";
            String content = "End Date cannot the same as the start date!";
            reportGenerateDialog(header,content, Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    //FIXME: Test this method. Although i think it's pretty correct (cases described below)
    private Long calculateRelevantTime(AbstractActivityPeriod activityPeriod, long startEpochTime, long endEpochTime) {
        //Returns part of activity period that's exactly between start and end of epoch time
        //Case 1: Period begins before startEpoch and ends after it (intersects with it)
        if (activityPeriod.getStartTime() < startEpochTime && activityPeriod.getEndTime() > startEpochTime) {
            //Case 1.1: it intersects with both of period frames, thus the whole epoch time frame is our interest
            if (activityPeriod.getEndTime() > endEpochTime)
                return endEpochTime - startEpochTime;
                //or it ends before the end of time frame, so we are interested in it's ending point and distance to beginning
            else
                return activityPeriod.getEndTime() - startEpochTime;

            //Case 2: It does not intersect with first time frame nor the second
        } else if (activityPeriod.getStartTime() >= startEpochTime && activityPeriod.getEndTime() <= endEpochTime) {

            return activityPeriod.getEndTime() - activityPeriod.getStartTime();
            //Case 3: it intersects with second frame (but not the first)
        } else if (activityPeriod.getStartTime() >= startEpochTime && activityPeriod.getEndTime() > endEpochTime) {
            return endEpochTime - activityPeriod.getStartTime();
        } else
            return 0L;
    }

}
