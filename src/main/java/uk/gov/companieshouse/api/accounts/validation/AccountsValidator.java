package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccountsValidator {

    @Autowired
    private CurrentPeriodTnClosureValidator currentPeriodTnClosureValidator;

    public Errors validationSubmission(Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Errors errors;

        // Current period validation.
        errors = currentPeriodTnClosureValidator.isValid(companyAccountsId, request);

        return errors;
    }
}
