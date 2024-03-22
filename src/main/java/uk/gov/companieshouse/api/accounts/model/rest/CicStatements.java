package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class CicStatements extends RestObject {

    @NotNull
    @JsonProperty("has_completed_report_statements")
    private Boolean hasCompletedReportStatements;

    @Valid
    @NotNull
    @JsonProperty("report_statements")
    private ReportStatements reportStatements;

    public Boolean getHasCompletedReportStatements() {
        return hasCompletedReportStatements;
    }

    public void setHasCompletedReportStatements(Boolean hasCompletedReportStatements) {
        this.hasCompletedReportStatements = hasCompletedReportStatements;
    }

    public ReportStatements getReportStatements() {
        return reportStatements;
    }

    public void setReportStatements(
            ReportStatements reportStatements) {
        this.reportStatements = reportStatements;
    }
}
