package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class CIC34ReportValidator extends BaseValidator {

    @Autowired
    private CompanyService companyService;

    private static final String CIC34_REPORT_PATH = "$.cic34_report";

    public Errors validateCIC34ReportSubmission(Transaction transaction) throws DataException {

        Errors errors = new Errors();

        try {
            if (!companyService.isCIC(transaction)) {
                addError(errors, unexpectedData, CIC34_REPORT_PATH);
            }
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }

        return errors;
    }
}
