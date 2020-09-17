package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;

@Component
public class CicStatementsValidator extends BaseValidator {

	private static final String CIC_STATEMENTS_BASE_PATH = "$.cic_statements.report_statements.";

    private static final String CONSULTATION_WITH_STAKEHOLDERS_PATH
                                    = CIC_STATEMENTS_BASE_PATH + "consultation_with_stakeholders";

    private static final String DIRECTORS_REMUNERATION_PATH
                                    = CIC_STATEMENTS_BASE_PATH + "directors_remuneration";

    private static final String TRANSFER_OF_ASSETS_PATH
                                    = CIC_STATEMENTS_BASE_PATH + "transfer_of_assets";

    @Autowired
    public CicStatementsValidator(CompanyService companyService) {
		super(companyService);
	}

    public Errors validateCicStatementsUpdate(CicStatements cicStatements) {

        Errors errors = new Errors();

        ReportStatements reportStatements = cicStatements.getReportStatements();

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
