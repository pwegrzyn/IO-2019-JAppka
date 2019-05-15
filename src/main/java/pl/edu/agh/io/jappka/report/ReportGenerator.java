package pl.edu.agh.io.jappka.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReportGenerator {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class.getName());
    private List<ReportFileFormat> supportedFormats;
    private ReportParameters reportParameters;

    public ReportGenerator() {
        this.supportedFormats = new ArrayList<>();
        this.supportedFormats.addAll(Arrays.asList(ReportFileFormat.values()));
    }

    public List<ReportFileFormat> getSupportedFormats() {
        return supportedFormats;
    }

    public void setReportParameters(ReportParameters reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Report generateReport() {
        if (this.reportParameters == null) {
            LOGGER.warning("No Report Parameters have been provided!");
            return null;
        }
        // TODO Actually generate the report
        return null;
    }
}
