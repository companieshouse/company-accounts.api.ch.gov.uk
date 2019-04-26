package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class PreviousPeriodValidator extends BaseValidator {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BalanceSheetValidator balanceSheetValidator;

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";

    public Errors validatePreviousPeriod(PreviousPeriod previousPeriod, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (isMultipleYearFiler) {
            balanceSheetValidator.validateBalanceSheet(
                    previousPeriod.getBalanceSheet(), transaction, PREVIOUS_PERIOD_PATH, errors);
        } else {
            addError(errors, unexpectedData, PREVIOUS_PERIOD_PATH);
        }

        return errors;
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {

        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }
}
