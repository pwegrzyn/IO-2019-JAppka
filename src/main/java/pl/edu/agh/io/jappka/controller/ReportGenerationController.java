package pl.edu.agh.io.jappka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.edu.agh.io.jappka.activity.AbstractActivityPeriod;
import pl.edu.agh.io.jappka.report.ReportTimeUnit;
import pl.edu.agh.io.jappka.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReportGenerationController {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerationController.class.getName());
    private Stage stage;
    private ObservableMap<String, List<AbstractActivityPeriod>> dataCollection;
    private final long MILISECONDS_IN_DAY = 86_400_000;

    private AppController appController;
    private Set<String> appsChosen = new HashSet<>();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setData(ObservableMap<String, List<AbstractActivityPeriod>> data) {
        this.dataCollection = data;
    }

    public void init(AppController appController) {
        this.appController = appController;
        for(String app : dataCollection.keySet())
            this.appsChosen.add(app);
        this.TimeUnitChoiceBox.setItems(FXCollections.observableArrayList(ReportTimeUnit.values()));

        // Set default values for dates
        LocalDate currentDate = LocalDate.now();
        this.dateStart.setValue(currentDate.minusDays(1));
        this.dateEnd.setValue(currentDate);

        // Pick default time unit
        this.TimeUnitChoiceBox.setValue(ReportTimeUnit.SECONDS);
    }

    public Set<String> getAppsChosen() {
        return appsChosen;
    }

    public void setAppsChosen(Set<String> appsChosen) {
        this.appsChosen = appsChosen;
    }

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private ChoiceBox<ReportTimeUnit> TimeUnitChoiceBox;

    @FXML
    private void handleGenerateReportButton(ActionEvent event) {
        if (validateInput()) {
            LocalDate startDate = this.dateStart.getValue();
            LocalDate endDate = this.dateEnd.getValue();
            try {
                File initialFile = chooseFileToSave();
                if (initialFile == null)
                    return;
                generateReport(startDate, endDate, initialFile, TimeUnitChoiceBox.getValue());
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
        alert.initOwner(this.stage);
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

    private void generateReport(LocalDate startDate, LocalDate endDate, File file, ReportTimeUnit timeUnit) throws IOException {

        switch(FilenameUtils.getExtension(file.getAbsolutePath())) {
            case "csv":
                printToCsv(startDate, endDate, file, timeUnit);
                break;
            case "xlsx":
                printToXlsx(startDate, endDate, file, timeUnit);
                break;
            default:
                LOGGER.warning("Failed to parse the correct extension from the FileChooser output file.");
                break;
        }

    }

    private void printToCsv(LocalDate startDate, LocalDate endDate, File file, ReportTimeUnit unit) throws IOException {
        long start = dateToEpochMilis(startDate);
        long end = dateToEpochMilis(endDate) + MILISECONDS_IN_DAY - 1;
        FileWriter out = new FileWriter(file.getAbsolutePath());
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(getHeaders(start, end));
            int days = (int) ((end - start) / MILISECONDS_IN_DAY) + 1;
            //get all apps we want to include in the report
            List<String> reportedApps = new ArrayList<>(filterData(start, end).keySet());
            //Iterate through days we want reported
            for (int i = 0; i < days; i++) {
                long nextDayStart = start + (i * MILISECONDS_IN_DAY);
                long nextDayEnd = nextDayStart + MILISECONDS_IN_DAY - 1;
                Map<String, Long> concreteDayUsages = filterData(nextDayStart, nextDayEnd);
                LocalDate calculatedDay = startDate.plusDays(i);
                long allAppsUsage = getAppsUsage(concreteDayUsages);
                //Remove PC from all apps usage - we have it separately reported by our own label
                reportedApps.remove("PC");
                //Get all wanted reported apps
                List<Double> usages = reportedApps.stream()
                        .map(concreteDayUsages::get)
                        .map(time -> epochMillisecondsUnitConvert(time, unit))
                        .collect(Collectors.toList());
                List<String> pcTurnOnAndOffTimes = calculatePCOnOffTimes(nextDayStart, nextDayEnd);
                List<Object> record = new ArrayList<>(
                        Arrays.asList(calculatedDay,
                                epochMillisecondsUnitConvert(concreteDayUsages.get("PC"), unit),
                                epochMillisecondsUnitConvert(allAppsUsage, unit),
                                (pcTurnOnAndOffTimes == null ? "-" : pcTurnOnAndOffTimes.get(0)),
                                (pcTurnOnAndOffTimes == null ? "-" : pcTurnOnAndOffTimes.get(1))
                        ));
                //Remove pc's value, we got it above
                concreteDayUsages.remove("PC");
                record.addAll(usages);
                printer.printRecord(record);
            }
        }
    }

    private List<String> calculatePCOnOffTimes(long dateStart, long dateEnd){
        /*
        * List<String> [0] -PC turn on time
        * List<String> [1] -PC turn off time
        * */

        List<AbstractActivityPeriod> activityPeriods = dataCollection.get("PC");
        activityPeriods = activityPeriods.stream().filter((activityPeriod) -> {
            if(activityPeriod.getEndTime() <= dateStart){
                return false;
            }
            if(activityPeriod.getStartTime() >= dateEnd){
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        if(activityPeriods.isEmpty()){
            return null;
        }

        long start = activityPeriods.get(0).getStartTime();
        long end = activityPeriods.get(activityPeriods.size()-1).getEndTime();

        start = (start < dateStart ? dateStart : start);
        end = (end < dateEnd ? end : dateEnd);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        return new ArrayList<>(Arrays.asList(
                dateFormat.format(new Date(start)),
                dateFormat.format(new Date(end))
        ));

    }

    private void printToXlsx(LocalDate startDate, LocalDate endDate, File file, ReportTimeUnit unit) throws IOException {

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

        // Create the headers
        long start = dateToEpochMilis(startDate);
        long end = dateToEpochMilis(endDate) + MILISECONDS_IN_DAY - 1;
        List<String> headers = getHeaders(start, end);
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Some slight changes were introduced here when re-using the generation code for the csv, be careful when refactoring
        int totalColumns = 0;
        int days = (int) ((end - start) / MILISECONDS_IN_DAY) + 1;
        List<String> reportedApps = new ArrayList<>(filterData(start, end).keySet());
        for (int i = 0; i < days; i++) {
            long nextDayStart = start + (i * MILISECONDS_IN_DAY);
            long nextDayEnd = nextDayStart + MILISECONDS_IN_DAY - 1;
            Map<String, Long> concreteDayUsages = filterData(nextDayStart, nextDayEnd);
            reportedApps.remove("PC");
            long allAppsUsage = getAppsUsage(concreteDayUsages);
            List<Double> usages = reportedApps.stream()
                    .map(concreteDayUsages::get)
                    .map(time -> epochMillisecondsUnitConvert(time, unit))
                    .collect(Collectors.toList());
            List<String> pcTurnOnAndOffTimes = calculatePCOnOffTimes(nextDayStart, nextDayEnd);
            List<Object> record = new ArrayList<>(
                    Arrays.asList(
                            Utils.millisecondsToCustomStrDate(nextDayStart, "dd-MM-yyyy"),
                            epochMillisecondsUnitConvert(concreteDayUsages.get("PC"), unit),
                            epochMillisecondsUnitConvert(allAppsUsage, unit),
                            (pcTurnOnAndOffTimes == null ? "-" : pcTurnOnAndOffTimes.get(0)),
                            (pcTurnOnAndOffTimes == null ? "-" : pcTurnOnAndOffTimes.get(1))
                            ));
            concreteDayUsages.remove("PC");
            record.addAll(usages);

            Row newRow = sheet.createRow(i + 1);
            int column = 0;
            for (Object recordElement : record) {
                Cell newCell = newRow.createCell(column++);
                if (recordElement instanceof String) {
                    newCell.setCellValue((String) recordElement);
                } else if (recordElement instanceof Double) {
                    newCell.setCellValue((Double) recordElement);
                } else {
                    LOGGER.warning("Incompatible record element type encountered in XLSX report generator: "
                            + recordElement.getClass().toString());
                    continue;
                }
            }
            if (totalColumns < column) {
                totalColumns = column;
            }
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private List<String> getHeaders(long start, long end) {
        List<String> activeApps = new ArrayList<>();
        activeApps.addAll(filterData(start, end).keySet());
        activeApps.remove("PC");
        //Add the apps to the standard headers
        List<String> headers = new ArrayList<>(Arrays.asList("DATE", "PC ON", "PC ACTIVE", "START TIME", "END TIME"));
        headers.addAll(activeApps);
        return headers;
    }

    //methods excludes pc from calculation and reduces the value to raw app usage
    private long getAppsUsage(Map<String, Long> usageForDay) {
        //Looks weird, but lambdas require the variable reference to be final
        final long[] wholeActivity = {0};
        usageForDay.entrySet().forEach((entry) -> {
            if (!Objects.equals(entry.getKey(), "PC") && !Objects.equals(entry.getKey(), "Custom"))
                wholeActivity[0] += entry.getValue();
        });
        return wholeActivity[0];
    }

    //Returns milliseconds representation of the start of the day `value`
    private long dateToEpochMilis(LocalDate value) {
        ZoneId zoneId = ZoneId.systemDefault();
        return value.atStartOfDay(zoneId).toEpochSecond() * 1000;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    // This one was fine
    private Map<String, Long> filterData(long start, long end) {

        Map<String, Long> outputUsage = new HashMap<>();

        for (String application : this.appsChosen) {
            //Get all periods for one application
            List<AbstractActivityPeriod> activityPeriods = dataCollection.get(application);
            //Filter periods which are active
            activityPeriods = activityPeriods.stream()
                    .filter((period) -> period.getType() == AbstractActivityPeriod.Type.FOCUSED || period.getType() == AbstractActivityPeriod.Type.ON)
                    .collect(Collectors.toList());

            outputUsage.put(application, 0L);
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
        if (this.TimeUnitChoiceBox.getValue() == null) return false;
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
        return true;
    }

    // Should be ok now
    private Long calculateRelevantTime(AbstractActivityPeriod activityPeriod, long startEpochTime, long endEpochTime) {
        //Returns part of activity period that's exactly between start and end of epoch time
        //Case 1: Period begins before startEpoch and ends after it (intersects with it)
        if (activityPeriod.getStartTime() < startEpochTime && activityPeriod.getEndTime() > startEpochTime) {
            //Case 1.1: it intersects with both of period frames, thus the whole epoch time frame is our interest
            if (activityPeriod.getEndTime() > endEpochTime) {
                return endEpochTime - startEpochTime;
            }
                //or it ends before the end of time frame, so we are interested in it's ending point and distance to beginning
            else {
                return activityPeriod.getEndTime() - startEpochTime;
            }
            //Case 2: It does not intersect with first time frame nor the second
        } else if (activityPeriod.getStartTime() >= startEpochTime && activityPeriod.getEndTime() <= endEpochTime) {
            return activityPeriod.getEndTime() - activityPeriod.getStartTime();
            //Case 3: it intersects with second frame (but not the first)
        } else if (activityPeriod.getStartTime() >= startEpochTime && activityPeriod.getEndTime() > endEpochTime && activityPeriod.getStartTime() <= endEpochTime) {
            return endEpochTime - activityPeriod.getStartTime();
        } else {
            return 0L;
        }
    }

    // convert milliseconds to a new given time unit
    private double epochMillisecondsUnitConvert(long time, ReportTimeUnit targetUnit) {
        switch (targetUnit) {
            case MILLISECONDS:
                return time;
            case SECONDS:
                return time / 1000;
            case MINUTES:
                return time / 1000.0 / 60;
            case HOURS:
                return time / 1000.0 / 60 / 60;
            case DAYS:
                return time / 1000.0 / 60 / 60 / 24;
            default: break;
        }
        return Double.NaN;
    }
    @FXML
    public void handlePickAppsForReportButton(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/pickAppsForReport.fxml"));
            AnchorPane layout = loader.load();

            PickAppsForReportController controller = loader.getController();
            Scene pickAppsForReportScene = new Scene(layout);
            Stage pickAppsForReportStage = new Stage();
            pickAppsForReportStage.setTitle("Select applications for a report");
            pickAppsForReportStage.setScene(pickAppsForReportScene);
            pickAppsForReportScene.getStylesheets().add(appController.getCurrentTheme());
            controller.setStage(pickAppsForReportStage);

            Set<String> allAppsInPeriod = dataCollection.keySet();
            controller.initialize(appController,allAppsInPeriod, this);
            pickAppsForReportStage.setResizable(false);
            pickAppsForReportStage.setAlwaysOnTop(true);
            pickAppsForReportStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

}
