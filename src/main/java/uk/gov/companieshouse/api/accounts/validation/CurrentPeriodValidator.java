package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class CurrentPeriodValidator {

    @Autowired
    private BalanceSheetValidator balanceSheetValidator;

    private static final String CURRENT_PERIOD_PATH = "$.current_period";


    public Errors validateCurrentPeriod(CurrentPeriod currentPeriod, Transaction transaction)
            throws DataException {

        Errors errors = new Errors();

        if (currentPeriod.getBalanceSheet() != null) {
            balanceSheetValidator.validateBalanceSheet(
                    currentPeriod.getBalanceSheet(), transaction, CURRENT_PERIOD_PATH, errors);
        }

        return errors;
    }
}