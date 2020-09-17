package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class PreviousPeriodValidator extends BaseValidator {

    private final BalanceSheetValidator balanceSheetValidator;

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";

    @Autowired
    public PreviousPeriodValidator(CompanyService companyService, BalanceSheetValidator balanceSheetValidator) {
        super(companyService);
        this.balanceSheetValidator = balanceSheetValidator;
    }

    public Errors validatePreviousPeriod(PreviousPeriod previousPeriod, Transaction transaction) throws DataException {

        Errors errors = new Errors();

            boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

            if (isMultipleYearFiler) {
                if (previousPeriod.getBalanceSheet() != null) {

                    balanceSheetValidator.validateBalanceSheet(
                            previousPeriod.getBalanceSheet(), transaction, PREVIOUS_PERIOD_PATH, errors);
                }
            } else {

                addError(errors, unexpectedData, PREVIOUS_PERIOD_PATH);
            }

        return errors;
    }
}
