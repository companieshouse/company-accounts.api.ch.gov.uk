package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

@Component
public class CicReportStatementsValidator extends BaseValidator {

    private static final String CIC_REPORT_STATEMENTS_BASE_PATH = "$.cic_report_statements.report_statements.";

    private static final String CONSULTATION_WITH_STAKEHOLDERS_PATH
                                    = CIC_REPORT_STATEMENTS_BASE_PATH + "consultation_with_stakeholders";

    private static final String DIRECTORS_REMUNERATION_PATH
                                    = CIC_REPORT_STATEMENTS_BASE_PATH + "directors_remuneration";

    private static final String TRANSFER_OF_ASSETS_PATH
                                    = CIC_REPORT_STATEMENTS_BASE_PATH + "transfer_of_assets";

    public Errors validateCicReportStatementsUpdate(CicReportStatements cicReportStatements) {

        Errors errors = new Errors();

        ReportStatements reportStatements = cicReportStatements.getReportStatements();

        if (reportStatements.getConsultationWithStakeholders() == null) {
            addError(errors, mandatoryElementMissing, CONSULTATION_WITH_STAKEHOLDERS_PATH);
        }
        if (reportStatements.getDirectorsRemuneration() == null) {
            addError(errors, mandatoryElementMissing, DIRECTORS_REMUNERATION_PATH);
        }
        if (reportStatements.getTransferOfAssets() == null) {
            addError(errors, mandatoryElementMissing, TRANSFER_OF_ASSETS_PATH);
        }

        return errors;
    }
}
