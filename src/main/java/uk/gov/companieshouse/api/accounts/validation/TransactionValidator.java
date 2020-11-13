package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class TransactionValidator extends BaseValidator {

    private static final String TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START = "$.transaction.breakdown.balance_at_period_start";

    @Autowired
    public TransactionValidator(CompanyService companyService) {
        super(companyService);
    }

    public Errors validateRptTransaction(RptTransaction rptTransaction, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if(!isMultipleYearFiler && rptTransaction.getBreakdown().getBalanceAtPeriodStart() != null ) {
            addError(errors, unexpectedData, TRANSACTION_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START);
            return errors;
        }

        return errors;
    }
}
