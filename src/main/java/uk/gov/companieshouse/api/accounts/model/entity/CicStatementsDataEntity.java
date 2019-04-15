package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class CicStatementsDataEntity extends BaseDataEntity {

    @Field("has_completed_report_statements")
    private Boolean hasCompletedReportStatements;

    @Field("report_statements")
    private ReportStatementsEntity reportStatements;

    public Boolean getHasCompletedReportStatements() {
        return hasCompletedReportStatements;
    }

    public void setHasCompletedReportStatements(Boolean hasCompletedReportStatements) {
        this.hasCompletedReportStatements = hasCompletedReportStatements;
    }

    public ReportStatementsEntity getReportStatements() {
        return reportStatements;
    }

    public void setReportStatements(
            ReportStatementsEntity reportStatements) {
        this.reportStatements = reportStatements;
    }
}
