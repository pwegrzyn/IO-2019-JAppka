package pl.edu.agh.io.jappka.report;

import java.time.LocalDate;

public class ReportParameters {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ReportFileFormat chosenFormat;

    public ReportParameters(LocalDate startDate, LocalDate endDate, ReportFileFormat chosenFormat) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.chosenFormat = chosenFormat;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReportFileFormat getChosenFormat() {
        return chosenFormat;
    }
}
